package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dal.ibatis.UserDefaultMapper;
import es.pryades.smartswitch.dto.UserDefault;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class UserDefaultsManagerImpl extends BaseManagerImpl implements UserDefaultsManager
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6038371787056779456L;
	private static final Logger LOG = Logger.getLogger( UserDefaultsManagerImpl.class );

	public static BaseManager build()
	{
		return new UserDefaultsManagerImpl();
	}

	public UserDefaultsManagerImpl()
	{
		super( UserDefaultMapper.class, UserDefault.class, LOG );
	}
	
	@Override
	public UserDefault getUserDefault( AppContext ctx, String key )
	{
		UserDefault def = null;
		
		try
		{
			UserDefault query = new UserDefault();
			query.setData_key( key );
			query.setRef_user( ctx.getUser().getId() );
		
			def = (UserDefault)getRow( ctx, query );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}

		if ( def == null )
		{
			def = new UserDefault();
		
			def.setData_key( key );
			def.setData_value( "" );
			def.setRef_user( ctx.getUser().getId() );
		}
		
		return def;
	}

	@Override
	public void setUserDefault( AppContext ctx, UserDefault def, String value )
	{
		try
		{
			UserDefault clone = null;
			if ( def.getId() != null )
				clone = (UserDefault)Utils.clone( def );
			
			def.setData_value( value );
			
			setRow( ctx, clone, def );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
