#include "globals.h"
#include "critical.h"

#define TAG_CRITICAL "CRITICAL"

static volatile uint8_t state = 1;
static uint8_t plan[96];
static volatile uint8_t plan_loaded = 0;
static volatile uint8_t wifi_connected = 0;
static volatile uint8_t configuration_changed = 0;
static volatile uint8_t forced_action = 0;

static portMUX_TYPE myMutex=portMUX_INITIALIZER_UNLOCKED;

uint8_t get_plan( uint8_t index )
{
	//ESP_LOGI(TAG_CRITICAL, "enter get_plan");

	taskENTER_CRITICAL(&myMutex);
	uint8_t ret = plan[index];
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit get_plan");

	return ret;
}

uint8_t get_state()
{
	//ESP_LOGI(TAG_CRITICAL, "enter get_state");

	taskENTER_CRITICAL(&myMutex);
	uint8_t ret = state;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit get_state");

	return ret;
}

void set_state( uint8_t value )
{
	//ESP_LOGI(TAG_CRITICAL, "enter set_state");

	taskENTER_CRITICAL(&myMutex);
	state = value;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit set_state");
}

void set_plan( uint8_t * new_plan )
{
	//ESP_LOGI(TAG_CRITICAL, "enter set_plan");

	taskENTER_CRITICAL(&myMutex);
	memcpy( plan, new_plan, sizeof( plan ) );
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit set_plan");
}

uint8_t is_plan_loaded()
{
	//ESP_LOGI(TAG_CRITICAL, "enter is_plan_loaded");

	taskENTER_CRITICAL(&myMutex);
	uint8_t ret = plan_loaded;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit is_plan_loaded");

	return ret;
}

void set_plan_loaded( uint8_t value )
{
	//ESP_LOGI(TAG_CRITICAL, "enter set_plan");

	taskENTER_CRITICAL(&myMutex);
	plan_loaded = value;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit set_plan");
}

uint8_t is_wifi_connected()
{
	//ESP_LOGI(TAG_CRITICAL, "enter is_wifi_connected");

	taskENTER_CRITICAL(&myMutex);
	uint8_t ret = wifi_connected;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit is_wifi_connected");

	return ret;
}

void set_wifi_connected( uint8_t value )
{
	//ESP_LOGI(TAG_CRITICAL, "enter set_wifi_connected");

	taskENTER_CRITICAL(&myMutex);
	wifi_connected = value;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit set_wifi_connected");
}

uint8_t is_configuration_changed()
{
	//ESP_LOGI(TAG_CRITICAL, "enter is_configuration_changed");

	taskENTER_CRITICAL(&myMutex);
	uint8_t ret = configuration_changed;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit is_configuration_changed");

	return ret;
}

void set_configuration_changed( uint8_t value )
{
	//ESP_LOGI(TAG_CRITICAL, "enter set_configuration_changed");

	taskENTER_CRITICAL(&myMutex);
	configuration_changed = value;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit set_configuration_changed");
}

uint8_t get_forced_action()
{
	//ESP_LOGI(TAG_CRITICAL, "enter get_state");

	taskENTER_CRITICAL(&myMutex);
	uint8_t ret = forced_action;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit get_state");

	return ret;
}

uint8_t set_forced_action( uint8_t value )
{
	//ESP_LOGI(TAG_CRITICAL, "enter set_state");

	taskENTER_CRITICAL(&myMutex);
	uint8_t ret = forced_action;
	forced_action = value;
	taskEXIT_CRITICAL(&myMutex);

	//ESP_LOGI(TAG_CRITICAL, "exit set_state");
	return ret;
}
