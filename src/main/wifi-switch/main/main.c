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

void execute_action( int h, int m, int s )
{
	int index = (h * 4) + m/15;

	uint8_t forced_action = get_forced_action();
	const char * msg = forced_action != 0 ? "FORCED" : "PLANNED";

	uint8_t action = forced_action == 0 ? get_plan( index ) : (forced_action == 1 ? 0 : 1);

	ESP_LOGI(TAG_WORK, "Current time %02d:%02d:%02d", h, m, s);

	if ( action != get_state() )
	{
		set_state( action );
		ESP_LOGI(TAG_WORK, "State %s changed to %s...", msg, action ? "ON" : "OFF" );

		io_relay( action );
	}
	else
		ESP_LOGI(TAG_WORK, "State %s continue %s...", msg, action ? "ON" : "OFF" );
}

void working_task(void *p)
{
	while ( 1 )
	{
		int h, m, s;
		getHourMinute( &h, &m, &s );

		if ( h != 0 || m != 0 )
			execute_action( h, m, s );
		else
			ESP_LOGI(TAG_WORK, "At this time a new plan is being retrieved. Action is delayed..." );

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
	
void load_plan( CONFIGURATION * config, int h, int m, int s )
{
	set_plan_loaded( 0 );

	ESP_LOGI(TAG_MAIN, "Retrieving plan...");
	uint8_t new_plan[96], forced_action;
	if ( !get_working_plan( config, new_plan, &forced_action ) )
	{
		ESP_LOGE(TAG_MAIN, "Could not retrieve new working plan. Current plan continue." );
	}
	else
	{
		set_plan( new_plan );
		set_plan_loaded( 1 );

		set_forced_action( forced_action );

		execute_action( h, m, s );
	}
}

void try_send_alive_signal( CONFIGURATION * config )
{

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

	vTaskDelay(3000 / portTICK_PERIOD_MS);
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
		ESP_LOGI(TAG_MAIN, "Synchronizing time...");
		if ( synchronize_time( config ) )
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
		ESP_LOGI(TAG_MAIN, "Retrieving plan...");
		uint8_t new_plan[96], forced_action;
		if ( get_working_plan( config, new_plan, &forced_action ) )
		{
			set_plan( new_plan );
			set_plan_loaded( 1 );

			set_forced_action( forced_action );

			break;
		}

		if ( !(retries--) )
			reboot("Working plan not retrieved. Restarting...");
			
		ESP_LOGI(TAG_MAIN, "Waiting to retry...");
		vTaskDelay(30000 / portTICK_PERIOD_MS);
	}
		
	xTaskCreate(&working_task, "working_task", 4096, NULL, 5, NULL);

	led_connection( 1 );

	int wait = 1;
	while ( 1 )
	{
		int h, m, s;
		getHourMinute( &h, &m, &s );

		if ( (h == 0 && m == 0) || !is_plan_loaded() )
		{
			load_plan( config, h, m, s );

			ESP_LOGI(TAG_MAIN, "Synchronizing time...");
			if ( !synchronize_time( config ) )
				ESP_LOGE(TAG_MAIN, "Time synchronization failed. Current time continue." );
		}

		if ( wait )
		{
			ESP_LOGI(TAG_MAIN, "Waiting for %d seconds...", 65-s );
			vTaskDelay((65-s)*1000 / portTICK_PERIOD_MS);
		}

		if ( is_configuration_changed() )
			reboot("Configuration changed. Restarting...");

		uint8_t reload_plan, forced_action;
		ESP_LOGI(TAG_MAIN, "Sending alive signal...");
		led_connection( send_alive_signal( config, get_state(), &reload_plan, &forced_action ) );

		uint8_t last_forced_action = set_forced_action( forced_action );
		
		wait = last_forced_action == forced_action && !reload_plan;

		if ( reload_plan )
			load_plan( config, h, m, s  );
		else if ( last_forced_action != get_forced_action() )
			execute_action( h, m, s );
	}
}
