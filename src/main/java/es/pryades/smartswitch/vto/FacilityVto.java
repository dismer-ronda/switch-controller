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
public class FacilityVto extends GenericVto
{
	private static final long serialVersionUID = -72982558774353472L;
	
	private Integer list_order;
	private String name;
	private String description;
	private Double power;

	private String enabled;

	public FacilityVto()
	{
	}
}
