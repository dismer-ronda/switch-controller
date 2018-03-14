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
public class ParameterVto extends GenericVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8960886008978503843L;
	private String description;
	private String value;

	public ParameterVto()
	{
	}
}
