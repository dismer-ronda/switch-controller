#ifndef CONFIGURATION_H

#define CONFIGURATION_H

typedef struct 
{
    char intName[64];

	char wifiSSID[32];
	char wifiPass[64];
	
	char serverAddress[128];
	char serverPort[128];
} CONFIGURATION;

void write_configuration();
void read_configuration();
CONFIGURATION * get_configuration();

void set_configuration( char * setupCommand );

void configuration_task(void *pvParameters);

int configuration_changed();

#endif
