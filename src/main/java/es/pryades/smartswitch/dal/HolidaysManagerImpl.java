package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.dal.ibatis.HolidayMapper;
import es.pryades.smartswitch.dto.Holiday;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class HolidaysManagerImpl extends BaseManagerImpl implements HolidaysManager
{
	private static final long serialVersionUID = -5219272596579799921L;
	
	private static final Logger LOG = Logger.getLogger( HolidaysManagerImpl.class );

	public static BaseManager build()
	{
		return new HolidaysManagerImpl();
	}

	public HolidaysManagerImpl()
	{
		super( HolidayMapper.class, Holiday.class, LOG );
	}
}
