package es.pryades.smartswitch.resources;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.util.SVGGenerator;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Authorization;
import es.pryades.smartswitch.common.Settings;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.services.ReturnFactory;

public class ChartToPngResource extends ServerResource 
{
    private static final Logger LOG = Logger.getLogger( ChartToPngResource.class );

    public ChartToPngResource() 
	{
		super();
	}

    @Override
    protected void doInit() throws ResourceException 
    {
        setExisting( true ); 
    }
	
    @Put("text")
    public void doPut( Representation entity ) throws Exception 
    {
		String text = entity != null ? entity.getText() : "";
		
		if ( entity == null )
		{
			LOG.info( "empty message received" );

			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_NO_CONTENT ) );
			getResponse().setEntity( new StringRepresentation( "No content" ) );
		}
		else
		{
			try
			{
				AppContext ctx = new AppContext( "es" );
				IOCManager._ParametersManager.loadParameters( ctx );

				if ( Utils.getEnviroment( "INSIGHTS_PHANTOMJS" ) != null )
		    	{
		    		Configuration configuration = (Configuration)Utils.getDeserializedObject( text );
		    		
					String svg = SVGGenerator.getInstance().generate( configuration, ctx.getIntegerParameter( Parameter.PAR_CHARTS_WIDTH ), ctx.getIntegerParameter( Parameter.PAR_CHARTS_HEIGHT ) );
					
					String filePath = System.getProperty( "java.io.tmpdir" );
					String fileName = Utils.getUUID();
					
					Utils.writeFile( filePath + "/" + fileName + ".svg", svg, false );
					Utils.cmdExec( "rsvg " + filePath + "/" + fileName + ".svg " + filePath + "/" + fileName + ".png" );
					
					Long fileId = IOCManager._FilesManager.saveFile( ctx, filePath + "/" + fileName + ".png" );
					
					new File( filePath + "/" + fileName + ".svg" ).delete();
					new File( filePath + "/" + fileName + ".png" ).delete();
					
					if ( fileId != null )
					{
						getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
						getResponse().setEntity( new StringRepresentation( fileId.toString() ) );
					}
			    	else
			    	{
						getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
						getResponse().setEntity( new StringRepresentation( "file not saved" ) );
			    	}
		    	}
		    	else
		    	{
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
					getResponse().setEntity( new StringRepresentation( "INSIGHTS_PHANTOMJS not set" ) );
		    	}
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
				
				getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
				getResponse().setEntity( new StringRepresentation( e.getCause() != null ? e.getCause().toString() : Utils.getExceptionString( e ) ) );
			}
		}
    }
    
	/**
	 * GET
	 */
	@Get
    public Representation getImage() 
	{
		Representation rep;
		
		try
		{
			HashMap<String,String> params = new HashMap<String,String>();
			
			Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
			
			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );

			String token = params.get( "token" );
			String password = Settings.TRUST_KEY;
			String code = Authorization.decrypt( params.get( "code" ), password );
			
			params.clear();
			
	        Utils.getParameters( code, params );
	        
	        String ts = params.get( "ts" );
	        long timeout = Long.parseLong( params.get( "timeout" ) );
	        Long fileId = Long.parseLong( params.get( "fileId" ) );
	        
			if ( Authorization.isValidRequest( token, ts+timeout, ts, password, timeout ) ) 
			{
				try
				{
					rep = new ByteArrayRepresentation( IOCManager._FilesManager.readFile( ctx, fileId ), MediaType.IMAGE_PNG );
					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );

					LOG.info( "retried image " + fileId );
				}				
				catch( Throwable e )
				{
					Utils.logException( e, LOG );

					getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
					rep = new StringRepresentation( e.getMessage() ); 
				}	
			}
			else
			{
				getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_FORBIDDEN ) );
				rep = new StringRepresentation( "Access denied" ); 
			}
		}
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
			
			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
			rep = new StringRepresentation( e.getMessage() ); 
		}
		
		return rep;
    }
    
	@Delete
    public void deleteResource() throws Exception
    {
		try
		{
			HashMap<String,String> params = new HashMap<String,String>();
			
			Utils.getParameters( getRequest().getResourceRef().getQuery(), params );
			
			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );

			String token = params.get( "token" );
			String password = Settings.TRUST_KEY;
			String code = Authorization.decrypt( params.get( "code" ), password );
			
			params.clear();
			
	        Utils.getParameters( code, params );
	        
	        String ts = params.get( "ts" );
	        long timeout = Long.parseLong( params.get( "timeout" ) );
	        Long fileId = Long.parseLong( params.get( "fileId" ) );
	        
			if ( Authorization.isValidRequest( token, ts+timeout, ts, password, timeout ) ) 
			{
				IOCManager._FilesManager.deleteFile( ctx, fileId );
				
				LOG.info( "deleted image " + fileId );

				getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_2XX_OK ) );
				getResponse().setEntity( new StringRepresentation( "OK" ) );
			}
			else
			{
				getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_4XX_FORBIDDEN ) );
				getResponse().setEntity( new StringRepresentation( "Access denied" ) );
			}
		}
		catch( Throwable e )
		{
			Utils.logException( e, LOG );
			
			getResponse().setStatus( Status.valueOf( ReturnFactory.STATUS_5XX_INTERNAL_SERVER_ERROR ) );
			getResponse().setEntity( new StringRepresentation( e.getMessage() ) );
		}	
    }
}
