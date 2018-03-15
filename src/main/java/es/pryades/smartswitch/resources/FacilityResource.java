package es.pryades.smartswitch.resources;

import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Authorization;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.Settings;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Facility;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.services.Return;
import es.pryades.smartswitch.services.ReturnFactory;

public class FacilityResource extends ServerResource 
{
    private static final Logger LOG = Logger.getLogger( FacilityResource.class );
    
    private String name;
    
	public FacilityResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
    	name = (String)getRequest().getAttributes().get( "name" );
    	
        setExisting( name != null );
    }
	
	/**
	 * GET
	 */
	@Get("json")
    public Representation toJson() throws Exception 
    {
		Representation rep;
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
		
		AppContext ctx = new AppContext( "es" );
		IOCManager._ParametersManager.loadParameters( ctx );

		String token = params.get( "token" );
		String user = params.get( "user" );
		String password = Settings.TRUST_KEY;
		String code = params.get( "code" );
		
		if ( code != null )
		{
			code = Authorization.decrypt( code, password );
		
			params.clear();
	        Utils.getParameters( code, params );
		}
        
        String ts = params.get( "ts" );
        long id = Utils.getLong( params.get( "id" ), 0 );
        long timeout = Utils.getLong( params.get( "timeout" ), 0 );

		Return ret = new Return();
	    
		try
		{
			//if ( Authorization.isValidRequest( token, ts+timeout, ts, password, timeout ) ) 
			{
				LOG.info( "name " + URLDecoder.decode( name, "UTF-8" ) );
				
		    	Facility query = new Facility();
		    	query.setName( URLDecoder.decode( name, "UTF-8" ) );
				
		    	Facility facility = (Facility)IOCManager._FacilitiesManager.getRow( ctx, query );
				
				if ( facility != null )
				{
					rep = new StringRepresentation( Utils.toJson( facility ), MediaType.APPLICATION_JSON );
				}
				else
				{
					ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
					ret.setDesc( "Hospital not found" );

					rep = new StringRepresentation( ret.getDesc() ); 
				}
			}
			/*else
			{
				ret.setCode( ReturnFactory.STATUS_4XX_FORBIDDEN );
				ret.setDesc( "Access denied" );
	
				rep = new StringRepresentation( ret.getDesc() );
			}*/
		}
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
			
			ret.setCode( ReturnFactory.STATUS_4XX_NOT_FOUND );
			ret.setDesc( "Facility not found" );

			rep = new StringRepresentation( ret.getDesc() ); 
			
			if ( !(e instanceof BaseException) )
				new BaseException( e, LOG, 0 );
		}	
		
		getResponse().setStatus( Status.valueOf( ret.getCode() ) );
		
		return rep;
    }
}
