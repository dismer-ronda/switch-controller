package es.pryades.smartswitch.vto.controlers;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.GenericVto;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dto.Profile;
import es.pryades.smartswitch.vto.ProfileVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class ProfileControlerVto extends GenericControlerVto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5742118802757140261L;

	public ProfileControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		ProfileVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Profile.class) )
			{
				result = new ProfileVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Profile) dtoObj).getId());
				result.setDescription(((Profile) dtoObj).getDescription());
			}
			else
			{
			}
		}

		return result;
	}
}
