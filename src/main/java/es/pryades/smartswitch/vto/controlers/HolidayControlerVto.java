package es.pryades.smartswitch.vto.controlers;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.CalendarUtils;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.GenericVto;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dto.Holiday;
import es.pryades.smartswitch.vto.HolidayVto;

/**
 * 
 * 
 * @author Dismer Ronda
 *
 */

public class HolidayControlerVto extends GenericControlerVto
{
	private static final long serialVersionUID = 6907124476241065807L;

	public HolidayControlerVto(AppContext ctx)
	{
		super(ctx);
	}

	public GenericVto generateVtoFromDto(VtoControllerFactory factory, Object dtoObj) throws BaseException
	{
		HolidayVto result = null;

		if ( dtoObj != null )
		{
			if ( dtoObj.getClass().equals(Holiday.class) )
			{
				result = new HolidayVto();
				
				result.setFactory( factory );
				result.setDtoObj( dtoObj );

				result.setId(((Holiday) dtoObj).getId());
				result.setHoliday_name( ((Holiday) dtoObj).getHoliday_name());
				result.setHoliday_type( getContext().getString( "holiday.type." + ((Holiday) dtoObj).getHoliday_type() ) );
				
				int holiday_type = ((Holiday) dtoObj).getHoliday_type();
				
				switch ( holiday_type )
				{
					case 0:
						result.setHoliday_value( getContext().getString( "holiday.type.dow." + ((Holiday) dtoObj).getHoliday_value() ) );
					break;
					case 1:
						result.setHoliday_value( CalendarUtils.getFormatedDate( ((Holiday) dtoObj).getHoliday_value(), "MMdd", "MMMMM-dd" ) );
					break;
					case 2:
						result.setHoliday_value( CalendarUtils.getFormatedDate( ((Holiday) dtoObj).getHoliday_value(), "MM", "MMMMM" ) );
					break;
					case 3:
						result.setHoliday_value( CalendarUtils.getFormatedDate( Integer.parseInt( ((Holiday) dtoObj).getHoliday_value() ), "dd MMMMM yyyy" ) );
					break;
				}
			}
			else
			{
			}
		}

		return result;
	}
}
