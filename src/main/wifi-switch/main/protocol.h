#ifndef PROTOCOL_H
#define PROTOCOL_H

int synchronizeTime( CONFIGURATION * config );
int sendCurrentAddress( CONFIGURATION * config );
int getWorkingPlan( CONFIGURATION * config, uint8_t * plan );
int sendAliveSignal( CONFIGURATION * config, uint8_t state );

#endif
