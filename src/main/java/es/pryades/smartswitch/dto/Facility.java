package es.pryades.smartswitch.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class Facility extends BaseDto
{
	private static final long serialVersionUID = -4189217681090064789L;
	
	private Integer list_order;
	private String name;
	private String description;
	private Double power;
	private Integer	enabled;
	
	private List<FacilityInterruptor> interruptors;
	private List<FacilityHoliday> holidays;
}
