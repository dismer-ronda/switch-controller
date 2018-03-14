package es.pryades.smartswitch.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import lombok.Data;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.Utils;

@Data
public abstract class DeviceTransport implements Serializable
{ 
	private static final long serialVersionUID = 6979826424353523056L;

	public Logger logger;

    public InputStream inputStream;
    public OutputStream outputStream;
    
    private boolean broken = false;

    public abstract void connect( Address address ) throws Throwable;
    public abstract int available();
    
    public DeviceTransport()
    {
    	inputStream = null;
    	outputStream = null;
    }
    
    public abstract void checkConnected() throws Throwable;
    
	public int read( long timeout ) throws Throwable
	{
		long start = System.nanoTime();
		
		while ( System.nanoTime() - start < timeout * 1000000 )
		{
			try
			{
				if ( inputStream.available() > 0 )
					return inputStream.read();

				Utils.Sleep( 10 );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, logger );

				throw e;
			}
		}
		
		checkConnected();
		
		return -1;
	}

	public void write( int c ) throws Throwable
	{
		try
		{
			outputStream.write( c );
		}
		catch ( IOException e )
		{
			Utils.logException( e, logger );

			throw e;
		}
	}
	
	public void write( byte b[] ) throws Throwable 
	{
		try
		{
			outputStream.write( b );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, logger );

			throw e;
		}
	}

	public void closeStreams()
    {
		try
		{
			if ( getOutputStream() != null )
				getOutputStream().close();
			if ( getInputStream() != null )
				getInputStream().close();
		}
		catch ( Throwable e )
		{
			new BaseException( e, logger, BaseException.UNKNOWN );
		}
    }

	public synchronized void disconnect() 
    {
		try
		{
			closeStreams();
		}
		catch ( Throwable e )
		{
			new BaseException( e, logger, BaseException.IO_ERROR );
		}
    }
	
    public void flushOutput()
    {
    	if ( getOutputStream() != null )
    	{
    		try 
    		{
				getOutputStream().flush();
			} 
    		catch ( Throwable e ) 
    		{
			}
    	}
    }

}