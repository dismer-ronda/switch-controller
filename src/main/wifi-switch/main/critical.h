#ifndef CRITICAL_H
#define CRITICAL_H

#include "globals.h"

uint8_t get_plan( uint8_t index );
void set_plan( uint8_t * new_plan );

uint8_t get_state();
void set_state( uint8_t new_state );

uint8_t is_plan_loaded();
void set_plan_loaded( uint8_t value );

uint8_t is_wifi_connected();
void set_wifi_connected( uint8_t value );

uint8_t is_configuration_changed();
void set_configuration_changed( uint8_t value );

#endif
