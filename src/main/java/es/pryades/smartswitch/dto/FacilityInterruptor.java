package es.pryades.smartswitch.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class FacilityInterruptor extends BaseDto
{
	private static final long serialVersionUID = -491402897132565610L;
	
	private Long ref_facility;
	private Long ref_interruptor;
	
	private Interruptor interruptor;
}
