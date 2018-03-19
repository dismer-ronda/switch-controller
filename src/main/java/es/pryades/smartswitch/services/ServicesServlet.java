package es.pryades.smartswitch.services;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import es.pryades.smartswitch.resources.FacilityResource;
import es.pryades.smartswitch.resources.InterruptorStatusResource;
import es.pryades.smartswitch.resources.LoginResource;

public class ServicesServlet extends Application
{
	private static final Logger LOG = Logger.getLogger( ServicesServlet.class );
	
    public ServicesServlet() 
    {
        super();
	}
    
    @Override
    public Restlet createInboundRoot() 
    {  
    	Router router = new Router( getContext() );
	       
    	router.attach( "/login", LoginResource.class );
    	router.attach( "/facility/{name}", FacilityResource.class );
    	router.attach( "/interruptor-status/{name}", InterruptorStatusResource.class );

    	LOG.info( "started" );
	    
	    return router;  
    }
}
