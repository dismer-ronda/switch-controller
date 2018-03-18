#include "globals.h"

#include "wifi.h"
#include "bluetooth.h"
#include "configuration.h"
#include "protocol.h"
#include "critical.h"
#include "io.h"

#define TAG_MAIN 	"MAIN"
#define TAG_WORK 	"WORK"

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

		uint8_t action = get_plan( index );

		if ( h != 0 || m != 0 )
		{
			ESP_LOGI(TAG_WORK, "Current time %02d:%02d:%02d", h, m, s);

			if ( action != get_state() )
			{
				set_state( action );
				ESP_LOGI(TAG_WORK, "State changed to %s...", action ? "ON" : "OFF" );

				io_relay( action );
			}
			else
				ESP_LOGI(TAG_WORK, "State continue %s...", action ? "ON" : "OFF" );
		}
		else
			ESP_LOGI(TAG_WORK, "At this time a new plan is being retrieved. Action %s is delayed...", action ? "ON" : "OFF" );
		
		ESP_LOGI(TAG_WORK, "Waiting for %d seconds...", 60-s );
		vTaskDelay((60-s)*1000 / portTICK_PERIOD_MS);
	}
}

void reboot( char * message )
{
	ESP_LOGI(TAG_MAIN, "%s", message );
	vTaskDelay(3000 / portTICK_PERIOD_MS);
	esp_restart();
}
	
void load_plan( CONFIGURATION * config )
{
	set_plan_loaded( 0 );

	uint8_t new_plan[96];
	if ( !getWorkingPlan( config, new_plan ) )
	{
		ESP_LOGE(TAG_MAIN, "Could not retrieve new working plan. Current plan continue." );
	}
	else
	{
		set_plan( new_plan );
		set_plan_loaded( 1 );
	}

	if ( !synchronizeTime( config ) )
		ESP_LOGE(TAG_MAIN, "Time synchronization failed. Current time continue." );
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

	initialize_io();
	io_relay( get_state() );

	int connection = 0;
	led_connection( connection );

	xTaskCreate(&bluetooth_config_task, "bluetooth_config_task", 4096, NULL, 5, NULL);
	xTaskCreate(&wifi_connection_task, "wifi_connection_task", 4096, config, 5, NULL);
	
	while ( !is_wifi_connected() )
	{
		ESP_LOGI(TAG_MAIN, "Waiting for wifi connection...");
		vTaskDelay(3000 / portTICK_PERIOD_MS);
		
		connection = 1-connection;
		led_connection( connection );

		if ( is_configuration_changed() )
			reboot("Configuration changed. Restarting...");
	}

	int retries = 5;
	while ( 1 )
	{
		if ( synchronizeTime( config ) )
			break;
		
		if ( !(retries--) )
			reboot( "Time synchronization failed. Restarting..." );
			
		ESP_LOGI(TAG_MAIN, "Waiting to retry...");
		vTaskDelay(30000 / portTICK_PERIOD_MS);

		if ( is_configuration_changed() )
			reboot("Configuration changed. Restarting...");
	}

	retries = 5;
	while ( 1 )
	{
		uint8_t new_plan[96];
		if ( getWorkingPlan( config, new_plan ) )
		{
			set_plan( new_plan );
			set_plan_loaded( 1 );

			break;
		}

		if ( !(retries--) )
			reboot("Working plan not retrieved. Restarting...");
			
		ESP_LOGI(TAG_MAIN, "Waiting to retry...");
		vTaskDelay(30000 / portTICK_PERIOD_MS);
	}
		
	xTaskCreate(&working_task, "working_task", 4096, NULL, 5, NULL);

	while ( 1 )
	{
		int h, m, s;
		getHourMinute( &h, &m, &s );

		if ( (h == 0 && m == 0) || !is_plan_loaded() )
			load_plan(config);

		ESP_LOGI(TAG_MAIN, "Waiting for %d seconds...", 60-s );
		vTaskDelay((60-s)*1000 / portTICK_PERIOD_MS);

		if ( is_configuration_changed() )
			reboot("Configuration changed. Restarting...");

		ESP_LOGI(TAG_MAIN, "Sending alive signal...");
		led_connection( sendAliveSignal( config, get_state() ) );
	}
}
