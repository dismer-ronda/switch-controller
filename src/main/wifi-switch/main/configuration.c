#include "critical.h"
#include "configuration.h"

#define STORAGE_NAMESPACE "storage"

#define TAG_CONFIGURATION "CONFIGURATION"

CONFIGURATION configuration;

nvs_handle open_storage()
{
    ESP_LOGI(TAG_CONFIGURATION, "Opening NVS Storage");

    nvs_handle my_handle;
    if (nvs_open(STORAGE_NAMESPACE, NVS_READWRITE, &my_handle) != ESP_OK) 
    {
		ESP_LOGE(TAG_CONFIGURATION, "Error opening storage");
    	return -1;
	}
    
    ESP_LOGI(TAG_CONFIGURATION, "Storage open");
    return my_handle;
}

void write_configuration()
{
    ESP_LOGI(TAG_CONFIGURATION, "Saving configuration file");
    
    nvs_handle my_handle = open_storage();
    
    if ( my_handle != -1 )
    {
		size_t required_size = sizeof( configuration );
    
		esp_err_t err = nvs_set_blob(my_handle, "configuration", &configuration, required_size);
	
		if (err == ESP_OK) 
			ESP_LOGI(TAG_CONFIGURATION, "Configuration written");

		nvs_close(my_handle);
		
		set_configuration_changed( 1 );
	}
}

void log_configuration()
{
	ESP_LOGI(TAG_CONFIGURATION, "intName = %s", configuration.intName );
	
	ESP_LOGI(TAG_CONFIGURATION, "wifiSSID = %s", configuration.wifiSSID );
	ESP_LOGI(TAG_CONFIGURATION, "wifiPass = %s", configuration.wifiPass );

	ESP_LOGI(TAG_CONFIGURATION, "serverAddress = %s", configuration.serverAddress );
	ESP_LOGI(TAG_CONFIGURATION, "serverPort = %s", configuration.serverPort );
}

void read_configuration()
{
    ESP_LOGI(TAG_CONFIGURATION, "Reading configuration file");
    
    nvs_handle my_handle = open_storage();
    
    if ( my_handle != -1 )
    {
		size_t required_size = sizeof( configuration );
		esp_err_t err = nvs_get_blob(my_handle, "configuration", &configuration, &required_size );
        
		if (err == ESP_OK) 
			log_configuration();

		nvs_close(my_handle);

		ESP_LOGI(TAG_CONFIGURATION, "Done");
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
