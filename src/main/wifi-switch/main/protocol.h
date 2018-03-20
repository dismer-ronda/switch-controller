#ifndef PROTOCOL_H
#define PROTOCOL_H

#include "globals.h"

int synchronize_time( CONFIGURATION * config );
int get_working_plan( CONFIGURATION * config, uint8_t * plan, uint8_t * forced_action );
int send_alive_signal( CONFIGURATION * config, uint8_t state, uint8_t * reload_plan, uint8_t * forced_action );

#endif
