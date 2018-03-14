package es.pryades.smartswitch.common;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;

import com.independentsoft.share.File;
import com.independentsoft.share.Folder;
import com.independentsoft.share.Service;

public class Sharepoint
{
	private static final Logger LOG = Logger.getLogger( Sharepoint.class );

	public static Service getSharepointService( AppContext context, String url, String user, String password )
	{
		Service service = new Service( url, user, password );
		
		HttpHost proxy = context.getHttpProxy();
		if ( proxy != null )
			service.setProxy( proxy );
		
		return service;
	}
	
	public static List<File> getFolderContents( Service service, String path )
	{
		try
		{
    		return service.getFiles( path );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			return new ArrayList<File>();
		}
	}

	public static InputStream downloadSharepointFile( Service service, String directory, String fileName )
    {
    	try
    	{
            return service.getFileStream( "/" + directory + "/" + fileName );

    	}
        catch ( Throwable e )
        {
        	Utils.logException( e, LOG );
        }
    	
    	return null;
    }
	
    public static void deleteSharepointFile( Service service, String directory, String fileName )
    {
    	try
    	{
    		service.deleteFile( "/" + directory + "/" + fileName );

            LOG.info( "sharepoint file " + fileName + " deleted from " + directory + " in server " + service.getSiteUrl() );
        } 
        catch ( Throwable e )
        {
        	Utils.logException( e, LOG );
        }
	}
	
    public static boolean createSharepointDirectory( Service service, String srcDir, String dstDir )
    {
		try
		{
	        List<Folder> folders = service.getFolders( srcDir );

	        for ( Folder folder : folders )
	        	if ( folder.getName().equals( dstDir ) )
	        		return true;
	        
        	service.createFolder( "/" + srcDir + "/" + dstDir );
        	
        	return true;
		}
        catch ( Throwable e )
        {
        	Utils.logException( e, LOG );
        }
        	
        return false;
    }

    public static void moveSharepointFile( Service service, String srcDir, String fileName, String dstDir )
    {
    	try
    	{
    		createSharepointDirectory( service, srcDir, dstDir );
    		
    		service.moveFile( "/" +  srcDir + "/" + fileName, "/" +  srcDir + "/" + dstDir + "/" + fileName );

            LOG.info( "sharepoint file " + fileName + " moved from " + srcDir + " to " + dstDir + "in server " + service.getSiteUrl() );
        } 
        catch ( Throwable e )
        {
        	Utils.logException( e, LOG );
        }
	}
}
