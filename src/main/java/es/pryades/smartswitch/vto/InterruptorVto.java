package es.pryades.smartswitch.vto;

import es.pryades.smartswitch.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class InterruptorVto extends GenericVto
{
	private static final long serialVersionUID = -6086104792462328721L;
	
	private Integer list_order;
	private String name;
	private String description;
	private String address;
	private Double power;

	private String plan_labor;
	private String plan_free;

	private String enabled;
	private String state;

	public InterruptorVto()
	{
	}
}
