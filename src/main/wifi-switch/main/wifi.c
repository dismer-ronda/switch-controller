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

#include "configuration.h"

static EventGroupHandle_t wifi_event_group;

const int CONNECTED_BIT = BIT0;

static const char *TAG_WIFI = "WIFI";

static esp_err_t event_handler(void *ctx, system_event_t *event)
{
    switch(event->event_id) {
    case SYSTEM_EVENT_STA_START:
        ESP_LOGI(TAG_WIFI, "SYSTEM_EVENT_STA_START");
        esp_wifi_connect();
        break;
    case SYSTEM_EVENT_STA_GOT_IP:
        ESP_LOGI(TAG_WIFI, "SYSTEM_EVENT_STA_GOT_IP");
        xEventGroupSetBits(wifi_event_group, CONNECTED_BIT);
        break;
    case SYSTEM_EVENT_STA_DISCONNECTED:
        ESP_LOGI(TAG_WIFI, "SYSTEM_EVENT_STA_DISCONNECTED");
        esp_wifi_connect();
        xEventGroupClearBits(wifi_event_group, CONNECTED_BIT);
        break;
    default:
        break;
    }
    return ESP_OK;
}

static void initialise_wifi( CONFIGURATION * config )
{
	tcpip_adapter_init();

    wifi_event_group = xEventGroupCreate();
    ESP_ERROR_CHECK( esp_event_loop_init(event_handler, NULL) );
    
    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    
    ESP_ERROR_CHECK( esp_wifi_init(&cfg) );
    ESP_ERROR_CHECK( esp_wifi_set_storage(WIFI_STORAGE_RAM) );

    wifi_config_t wifi_config;
    strcpy( &wifi_config.sta.ssid, &config->wifiSSID );
    strcpy( &wifi_config.sta.password, &config->wifiPass );
    
    ESP_LOGI(TAG_WIFI, "Setting WiFi configuration SSID %s...", wifi_config.sta.ssid);
    ESP_ERROR_CHECK( esp_wifi_set_mode(WIFI_MODE_STA) );
    ESP_ERROR_CHECK( esp_wifi_set_config(ESP_IF_WIFI_STA, &wifi_config) );
    ESP_ERROR_CHECK( esp_wifi_start() );
}

static volatile int wifiConnected = 0;
int wifi_connected()
{
	return wifiConnected;
}

void wifi_connection_task( void * p )
{
    initialise_wifi( (CONFIGURATION *)p );

	xEventGroupWaitBits(wifi_event_group, CONNECTED_BIT, false, true, portMAX_DELAY);
	ESP_LOGI(TAG_WIFI, "Connected to AP");

	wifiConnected = 1;
	
	while ( 1 )
	{
		vTaskDelay(60000 / portTICK_PERIOD_MS);
	}
}

