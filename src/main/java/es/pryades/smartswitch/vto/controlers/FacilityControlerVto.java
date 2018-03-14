package es.pryades.smartswitch.vto.controlers;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.GenericVto;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dto.Facility;
import es.pryades.smartswitch.vto.FacilityVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class FacilityControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = -1161985962990915805L;

	public FacilityControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		FacilityVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Facility.class) )
			{
				result = new FacilityVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Facility) dtoObj).getId());
				result.setList_order( ((Facility) dtoObj).getList_order() );
				result.setName(((Facility) dtoObj).getName());
				result.setDescription( ( ((Facility) dtoObj).getDescription()) );
				result.setPower( ( ((Facility) dtoObj).getPower()) );

				result.setEnabled( getContext().getString( ((Facility) dtoObj).getEnabled() != 0 ? "words.yes" : "words.no" ) );
			}
		}

		return result;
	}
}
