package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.dal.ibatis.ProfileRightMapper;
import es.pryades.smartswitch.dto.ProfileRight;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ProfilesRightsManagerImpl extends BaseManagerImpl implements ProfilesRightsManager
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4407417331885108347L;
	private static final Logger LOG = Logger.getLogger( ProfilesRightsManagerImpl.class );

	public static BaseManager build()
	{
		return new ProfilesRightsManagerImpl();
	}

	public ProfilesRightsManagerImpl()
	{
		super( ProfileRightMapper.class, ProfileRight.class, LOG );
	}

	@Override
	public boolean hasUniqueId( AppContext ctx ) 
	{
		return false;
	}
}
