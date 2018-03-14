package es.pryades.smartswitch.dal;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dal.ibatis.ParameterMapper;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class ParametersManagerImpl extends BaseManagerImpl implements ParametersManager
{
	private static final long serialVersionUID = -5028886986098594934L;

	private static final Logger LOG = Logger.getLogger( ParametersManagerImpl.class );

	public static BaseManager build()
	{
		return new ParametersManagerImpl();
	}

	public ParametersManagerImpl()
	{
		super( ParameterMapper.class, Parameter.class, LOG );
	}
	
	public void loadParameters( AppContext ctx )
	{
		ctx.setParameters( getParameters( ctx ) );
	}

	@Override
	public HashMap<Long, Parameter> getParameters(AppContext ctx) 
	{
		HashMap<Long, Parameter> ret = new HashMap<Long, Parameter>();
		
		try 
		{
			@SuppressWarnings("unchecked")
			List<Parameter> parameters = getRows(ctx, new Query() );
			
			for ( Parameter parameter : parameters )
				ret.put( parameter.getId(), parameter );
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
		}
		
		return ret;
	}

	@Override
	public boolean isLogEnabled( AppContext ctx, String action )
	{
		return action.equals( "U" );
	}
}
