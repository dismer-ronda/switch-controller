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
public class Interruptor extends BaseDto
{
	private static final long serialVersionUID = 5053984524754008591L;
	
	private Integer list_order;
	private String name;
	private String description;
	private String address;
	private Double power;

	private byte[] plan_labor;
	private byte[] plan_free;

	private Integer	enabled;
	private Integer	reload_plan;
	
	private Integer state;
	private Long last_signal;
	
	private Integer forced_action;
	
	private List<Holiday> holidays;
}
