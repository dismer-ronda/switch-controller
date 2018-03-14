package es.pryades.smartswitch.vto.controlers;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.GenericVto;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.vto.InterruptorVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class InterruptorControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 5828357821436527517L;

	public InterruptorControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		InterruptorVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Interruptor.class) )
			{
				result = new InterruptorVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Interruptor) dtoObj).getId());
				result.setList_order( ((Interruptor) dtoObj).getList_order() );
				result.setName(((Interruptor) dtoObj).getName());
				result.setDescription( ( ((Interruptor) dtoObj).getDescription()) );
				result.setAddress( ( ((Interruptor) dtoObj).getAddress()) );
				result.setPower( ( ((Interruptor) dtoObj).getPower()) );

				String plan1 = "";	
				for ( byte b : ((Interruptor)dtoObj).getPlan_labor() )
					plan1 += (b == 0 ? '0' : '1');
				result.setPlan_labor( plan1 );
					
				String plan2 = "";	
				for ( byte b : ((Interruptor)dtoObj).getPlan_free() )
					plan2 += (b == 0 ? '0' : '1');
				result.setPlan_free( plan2 );

				result.setEnabled( getContext().getString( ((Interruptor) dtoObj).getEnabled() != 0 ? "words.yes" : "words.no" ) );

				Integer state = ((Interruptor) dtoObj).getState();
				if ( state == null )
					result.setState( getContext().getString( "words.unknown" ) );
				else
					result.setState( getContext().getString( state.equals( 0 ) ? "words.off" : "words.on" ) );
			}
			else
			{
			}
		}

		return result;
	}
}
