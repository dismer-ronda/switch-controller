package es.pryades.smartswitch.resources;

import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.CalendarUtils;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.services.ReturnFactory;

public class InterruptorStatusResource extends ServerResource 
{
    private static final Logger LOG = Logger.getLogger( InterruptorStatusResource.class );
    
    private String name;
    
	public InterruptorStatusResource() 
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
	@Get("text")
    public Representation toJson() throws Exception 
    {
		Representation rep;
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
		
		AppContext ctx = new AppContext( "es" );
		IOCManager._ParametersManager.loadParameters( ctx );

		String ret; 
	    
		try
		{
			LOG.info( "name " + URLDecoder.decode( name, "UTF-8" ) );
			
	    	Interruptor query = new Interruptor();
	    	query.setName( URLDecoder.decode( name, "UTF-8" ) );
			
	    	Interruptor interruptor = (Interruptor)IOCManager._InterruptorsManager.getRow( ctx, query );
			
			if ( interruptor != null )
			{
				Long lastAlive = interruptor.getLast_signal();
				Integer state = interruptor.getState();
				
				if ( state == null )
					ret = "CRITICAL State undefined";
				else if ( lastAlive == null )
					ret = "CRITICAL Alive undefined";
				else 
				{
					long delay = Utils.getDurationInSeconds( lastAlive, CalendarUtils.getServerDateAsLong() );
					
					if ( delay < 240 )
						ret = "OK";
					else if ( delay < 480 )
						ret = "WARNING " + delay/60 + " minutes disconnected";
					else
						ret = "CRITICAL " + delay/60 + " minutes disconnected";
				}
			}
			else
				ret = "CRITICAL Interruptor " + name + " not found";
		}
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
			
			ret = "CRITICAL Internal error";
		}	
		
		getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
		
		rep = new StringRepresentation( ret );

		return rep;
    }
}
