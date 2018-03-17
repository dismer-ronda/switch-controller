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
public class Holiday extends BaseDto
{
	private static final long serialVersionUID = -5891964392398548549L;
	
	private String holiday_name;
	private Integer holiday_type;
	private String holiday_value;
}
