#include <string.h>

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "nvs_flash.h"

#include "esp_vfs.h"
#include "esp_vfs_fat.h"
#include "esp_system.h"

#include "configuration.h"

#define STORAGE_NAMESPACE "storage"

static const char *TAG = "CONF";

CONFIGURATION configuration;

volatile int configurationChanged = 0;

nvs_handle open_storage()
{
    ESP_LOGI(TAG, "Opening NVS Storage");

    nvs_handle my_handle;
    if (nvs_open(STORAGE_NAMESPACE, NVS_READWRITE, &my_handle) != ESP_OK) 
    {
		ESP_LOGI(TAG, "Error opening storage");
    	return -1;
	}
    
    ESP_LOGI(TAG, "Storage open");
    return my_handle;
}

void write_configuration()
{
    ESP_LOGI(TAG, "Saving configuration file");
    
    nvs_handle my_handle = open_storage();
    
    if ( my_handle != -1 )
    {
		size_t required_size = sizeof( configuration );
    
		esp_err_t err = nvs_set_blob(my_handle, "configuration", &configuration, required_size);
	
		if (err == ESP_OK) 
			ESP_LOGI(TAG, "Configuration written");

		nvs_close(my_handle);
		
		configurationChanged = 1;
	}
}

void log_configuration()
{
	ESP_LOGI(TAG, "hubName = %s", configuration.intName );
	
	ESP_LOGI(TAG, "wifiSSID = %s", configuration.wifiSSID );
	ESP_LOGI(TAG, "wifiPass = %s", configuration.wifiPass );

	ESP_LOGI(TAG, "serverAddress = %s", configuration.serverAddress );
	ESP_LOGI(TAG, "serverPort = %s", configuration.serverPort );
}

void read_configuration()
{
    ESP_LOGI(TAG, "Reading configuration file");
    
    nvs_handle my_handle = open_storage();
    
    if ( my_handle != -1 )
    {
		size_t required_size = sizeof( configuration );
		esp_err_t err = nvs_get_blob(my_handle, "configuration", &configuration, &required_size );
        
		if (err == ESP_OK) 
			log_configuration();

		nvs_close(my_handle);

		ESP_LOGI(TAG, "Done");
	}
}

CONFIGURATION * get_configuration()
{
	return &configuration;
}

int getAttribute( const char * str, int * pos, char * dest )
{
	int i = 0;
	int length = strlen( str );
	
	while ( (*pos) < length && str[*pos] != '\n' )
		dest[i++] = str[(*pos)++];
	dest[i] = 0;
	
	(*pos)++;

	return i > 0;
}

void set_configuration( char * setupCommand )
{
	int pos = 0;
	
	getAttribute( setupCommand, &pos, configuration.intName );
	
	getAttribute( setupCommand, &pos, configuration.wifiSSID );
	getAttribute( setupCommand, &pos, configuration.wifiPass );
	
	getAttribute( setupCommand, &pos, configuration.serverAddress );
	getAttribute( setupCommand, &pos, configuration.serverPort );
	
	log_configuration();
	
	write_configuration();
}	

int configuration_changed()
{
	return configurationChanged;
}
