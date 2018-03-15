#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "esp_system.h"
#include "esp_wifi.h"
#include "esp_event_loop.h"
#include "esp_log.h"
#include "nvs_flash.h"

#include "lwip/err.h"
#include "lwip/sockets.h"
#include "lwip/sys.h"
#include "lwip/netdb.h"
#include "lwip/dns.h"

#include <time.h>
#include <sys/time.h>

#include "driver/gpio.h"

#include "wifi.h"
#include "bluetooth.h"
#include "configuration.h"
#include "protocol.h"

static const char *TAG_MAIN = "MAIN";
static const char *TAG_WORK = "WORK";

#define GPIO_OUTPUT_IO_0    	32
#define GPIO_OUTPUT_PIN_SEL  	(1ULL<<GPIO_OUTPUT_IO_0) 

volatile uint8_t state = 0;
uint8_t plan[96];

void getHourMinute( int * h, int * m, int * s )
{
	time_t now;
	struct tm * ti;
	
	time(&now);
	ti = localtime(&now);  
	
	*h = ti->tm_hour;
	*m = ti->tm_min;
	*s = ti->tm_sec;
}

void working_task(void *p)
{
	while ( 1 )
	{
		int h, m, s;
		getHourMinute( &h, &m, &s );

		int index = (h * 4) + m/15;
		uint8_t action = plan[index];

		ESP_LOGI(TAG_WORK, "Current time %02d:%02d:%02d", h, m, s);
		ESP_LOGI(TAG_WORK, "index = %d -> %s", index, action == 1 ? "on" : "off" );
		
		if ( action != state )
		{
			gpio_set_level(GPIO_OUTPUT_IO_0, action);
			state = action;
			ESP_LOGI(TAG_WORK, "State changed to %s...", state ? "on" : "off" );
		}
		else
			ESP_LOGI(TAG_WORK, "State mantains %s...", state ? "on" : "off" );
		
		ESP_LOGI(TAG_WORK, "Waiting for %d seconds...", 60-s );
		vTaskDelay((60-s)*1000 / portTICK_PERIOD_MS);
	}
}

void initialize_io()
{
	gpio_config_t io_conf;
    io_conf.intr_type = GPIO_PIN_INTR_DISABLE;
    io_conf.mode = GPIO_MODE_OUTPUT;
    io_conf.pin_bit_mask = GPIO_OUTPUT_PIN_SEL;
    io_conf.pull_down_en = 0;
    io_conf.pull_up_en = 0;

    gpio_config(&io_conf);
}

void reboot( char * message )
{
	ESP_LOGE(TAG_MAIN, "%s", message );
	vTaskDelay(10000 / portTICK_PERIOD_MS);
	esp_restart();
}
	
void app_main()
{
    esp_err_t err = nvs_flash_init();
    if (err == ESP_ERR_NVS_NO_FREE_PAGES) {
        // NVS partition was truncated and needs to be erased
        // Retry nvs_flash_init
        ESP_ERROR_CHECK(nvs_flash_erase());
        err = nvs_flash_init();
    }
    ESP_ERROR_CHECK( err );

	vTaskDelay(5000 / portTICK_PERIOD_MS);
	read_configuration();

	CONFIGURATION * config = get_configuration();

	xTaskCreate(&bluetooth_config_task, "bluetooth_config_task", 4096, NULL, 5, NULL);
	xTaskCreate(&wifi_connection_task, "wifi_connection_task", 4096, config, 5, NULL);
	
	while ( !wifi_connected() )
	{
		ESP_LOGI(TAG_MAIN, "Waiting for wifi connection...");
		vTaskDelay(1000 / portTICK_PERIOD_MS);
		
		if ( configuration_changed() )
			reboot("Configuration changed. Restarting...");
	}

	int retries = 3;
	while ( retries )
	{
		if ( synchronizeTime( config ) )
			break;
		
		if ( !(retries--) )
			reboot( "Time synchronization failed. Restarting..." );
			
		ESP_LOGI(TAG_MAIN, "Waiting to retry...");
		vTaskDelay(10000 / portTICK_PERIOD_MS);
	}

	retries = 3;
	while ( retries )
	{
		if ( getWorkingPlan( config, plan ) )
			break;
			
		if ( !(retries--) )
			reboot("Working plan not retrieved. Restarting...");
			
		ESP_LOGI(TAG_MAIN, "Waiting to retry...");
		vTaskDelay(10000 / portTICK_PERIOD_MS);
	}
		
	xTaskCreate(&working_task, "working_task", 4096, plan, 5, NULL);

	while ( 1 )
	{
		int h, m, s;
		getHourMinute( &h, &m, &s );

		if ( h == 0 && m == 0 )
		{
			uint8_t new_plan[96];

			if ( !getWorkingPlan( config, new_plan ) )
			{
				ESP_LOGE(TAG_MAIN, "Could not retrieve new working plan. Current plan remains." );
			}
			else
				memcpy( plan, new_plan, sizeof(plan) );
				
			if ( !synchronizeTime( config ) )
				ESP_LOGE(TAG_MAIN, "Time synchronization failed. Current time remains." );
		}
		
		ESP_LOGI(TAG_MAIN, "Sending alive signal...");
		sendAliveSignal( config, state );

		ESP_LOGI(TAG_MAIN, "Waiting for %d seconds...", 60-s );
		vTaskDelay((60-s)*1000 / portTICK_PERIOD_MS);

		if ( configuration_changed() )
			reboot("Configuration changed. Restarting...");
	}
}
