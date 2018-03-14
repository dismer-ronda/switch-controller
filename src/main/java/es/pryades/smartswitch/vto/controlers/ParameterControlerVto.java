package es.pryades.smartswitch.vto.controlers;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.GenericVto;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.vto.ParameterVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class ParameterControlerVto extends GenericControlerVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6580234350347649495L;

	public ParameterControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		ParameterVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Parameter.class) )
			{
				result = new ParameterVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Parameter) dtoObj).getId());
				result.setDescription( ((Parameter) dtoObj).getDescription() );
				result.setValue( ((Parameter) dtoObj).getValue() );
			}
			else
			{
			}
		}

		return result;
	}
}
