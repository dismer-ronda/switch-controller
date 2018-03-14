package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.dal.ibatis.ProfileMapper;
import es.pryades.smartswitch.dto.Profile;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ProfilesManagerImpl extends BaseManagerImpl implements ProfilesManager
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7938904254730827479L;
	private static final Logger LOG = Logger.getLogger( ProfilesManagerImpl.class );

	public static BaseManager build()
	{
		return new ProfilesManagerImpl();
	}

	public ProfilesManagerImpl()
	{
		super( ProfileMapper.class, Profile.class, LOG );
	}
}
