package es.pryades.smartswitch.resources;

import java.io.IOException;
import java.util.HashMap;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Authorization;
import es.pryades.smartswitch.common.Settings;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.RemoteLogin;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.services.ReturnFactory;

public class LoginResource extends ServerResource 
{
    private static final Logger LOG = Logger.getLogger( LoginResource.class );

	public LoginResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true );
    }
	
    private void processRequest( Representation entity ) throws IOException
    {
		String text = entity != null ? entity.getText() : "";
		
		if ( entity == null )
		{
			LOG.info( "empty message received" );

			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_UNAUTHORIZED ) );
			getResponse().setEntity( new StringRepresentation( "failure" ) );
		}
		else
		{
			try
			{
				HashMap<String,String> params = new HashMap<String,String>();
				
				Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
				
				AppContext ctx = new AppContext( "es" );
				IOCManager._ParametersManager.loadParameters( ctx );

				String token = params.get( "token" );
				String password = Settings.TRUST_KEY;
		        String ts = params.get( "ts" );
		        long timeout = Utils.getLong( params.get( "timeout" ), 0 );
				
				if ( Authorization.isValidRequest( token, ts+timeout, ts, password, timeout ) ) 
				{
					RemoteLogin remoteLogin = (RemoteLogin)Utils.toPojo( text, RemoteLogin.class, true );
					IOCManager._UsersManager.remoteLogin( ctx, remoteLogin.getLogin(), remoteLogin.getPwd() );
	
					ctx.getUser().setPwd( null );
					ctx.getUser().setOrder( null );
					ctx.getUser().setOrderby( null );
					
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
					getResponse().setEntity( new StringRepresentation( Utils.toJson( ctx.getUser() ) ) );
				}
				else
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_FORBIDDEN ) );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
				getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_UNAUTHORIZED ) );
			}
		}
    }
    
    @Post("application/json")
    public void doPost( Representation entity ) throws Exception 
    {
    	processRequest( entity );
    }
}
