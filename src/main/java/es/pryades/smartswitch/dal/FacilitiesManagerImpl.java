package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.dal.ibatis.FacilityMapper;
import es.pryades.smartswitch.dto.Facility;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class FacilitiesManagerImpl extends BaseManagerImpl implements FacilitiesManager
{
	private static final long serialVersionUID = -7185683593434658811L;
	
	private static final Logger LOG = Logger.getLogger( FacilitiesManagerImpl.class );

	public static BaseManager build()
	{
		return new FacilitiesManagerImpl();
	}

	public FacilitiesManagerImpl()
	{
		super( FacilityMapper.class, Facility.class, LOG );
	}
}
