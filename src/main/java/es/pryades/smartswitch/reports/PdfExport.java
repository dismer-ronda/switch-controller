package es.pryades.smartswitch.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

import lombok.Getter;
import lombok.Setter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.log4j.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.CalendarUtils;
import es.pryades.smartswitch.common.Utils;

/**
 * @author dismer.ronda
 *
 */

public class PdfExport extends ExportBase
{
	private static final long serialVersionUID = 633422593833984489L;

	private static final Logger LOG = Logger.getLogger( PdfExport.class );
	
	@Getter @Setter private String date;
	@Getter @Setter private String size;
	@Getter @Setter private String orientation;
	@Getter @Setter private String template;
	@Getter @Setter private String orderby;
	@Getter @Setter private String order;
	
	public VelocityContext createVelocityContext() throws BaseException
	{
		VelocityContext vcontext = super.createVelocityContext();
		
		vcontext.put( "pagesize", size ); 
		vcontext.put( "orientation", orientation ); 
		vcontext.put( "title", "Report title" );
		vcontext.put( "context", getContext() );
		vcontext.put( "date", date != null ? CalendarUtils.formatDate( date, "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm") : "" );
		vcontext.put( "orderby", orderby );
		vcontext.put( "order", order );
				
		return vcontext;
	}

	public void cleanupAfterGeneration() throws BaseException
	{
	}
	
	public boolean doExport( OutputStream resp ) throws BaseException
	{
		try 
		{
			InputStream in = createStreamTemplate( template );
			
			StringWriter stWriter = new StringWriter();
	        Reader templateReader = new BufferedReader( new InputStreamReader( in ) );	
	        
			VelocityContext vcontext = createVelocityContext();
			
			Velocity.evaluate( vcontext, stWriter, getClass().getSimpleName(), templateReader );

	        ITextRenderer renderer = new ITextRenderer();
	        
	        renderer.setDocumentFromString( stWriter.toString() );
	        renderer.layout();
	        renderer.createPDF( resp );
		} 
		catch ( Throwable e ) 
		{
			Utils.logException( e, LOG );
			
			if ( e instanceof IOException )
				throw new BaseException( e, LOG, BaseException.IO_ERROR );
			
			if ( e instanceof InterruptedException )
				throw new BaseException( e, LOG, BaseException.INTERRUPTED_ERROR );
			
			throw new BaseException( e, LOG, BaseException.UNKNOWN );
		}
		finally
		{
	        cleanupAfterGeneration();
		}
		
		return true;
	}
}
