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
public class FacilityHoliday extends BaseDto
{
	private static final long serialVersionUID = 1183286932931712409L;
	
	private Long ref_facility;
	private Long ref_holiday;
	
	private Holiday holiday;
}
