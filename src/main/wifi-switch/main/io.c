#include "io.h"

#define GPIO_OUTPUT_IO_0    	32
#define GPIO_OUTPUT_IO_1    	33

#define GPIO_OUTPUT_PIN_SEL  ((1ULL<<GPIO_OUTPUT_IO_0) | (1ULL<<GPIO_OUTPUT_IO_1))

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

void io_relay( uint8_t state )
{
	gpio_set_level(GPIO_OUTPUT_IO_0, state);
}

void led_connection( uint8_t on )
{
	gpio_set_level(GPIO_OUTPUT_IO_1, on);
}

