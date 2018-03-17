package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.dal.ibatis.FacilityHolidayMapper;
import es.pryades.smartswitch.dto.FacilityHoliday;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class FacilityHolidaysManagerImpl extends BaseManagerImpl implements FacilityHolidaysManager
{
	private static final long serialVersionUID = 3728562060966413436L;
	
	private static final Logger LOG = Logger.getLogger( FacilityHolidaysManagerImpl.class );

	public static BaseManager build()
	{
		return new FacilityHolidaysManagerImpl();
	}

	public FacilityHolidaysManagerImpl()
	{
		super( FacilityHolidayMapper.class, FacilityHoliday.class, LOG );
	}

	@Override
	public boolean hasUniqueId( AppContext ctx ) 
	{
		return false;
	}
}
