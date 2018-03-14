package es.pryades.smartswitch.transports;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.Utils;

public class TcpServerTransport extends DeviceTransport
{
	private static final long serialVersionUID = -2428029085841581079L;

	private static final Logger LOG = Logger.getLogger( TcpServerTransport.class );

    Socket socket;

	public TcpServerTransport()
	{
		setLogger( LOG );
		
		socket = null;
	}
	
	public TcpServerTransport bind( Socket connection ) throws Throwable
	{
		this.socket = connection;
		
		connect( null );
		
		return this;
	}
	
    public void connect( Address address ) throws Throwable 
    {
    	try 
    	{
    		setOutputStream( socket.getOutputStream() );
    		getOutputStream().flush();
    		
    		setInputStream( socket.getInputStream() ); 
    	}
    	catch ( Throwable e )
    	{
    		Utils.logException( e, LOG );
    	}
    }

    public synchronized void disconnect() 
    {
    	super.disconnect();

    	try
		{
			if ( socket != null )
				socket.close();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
    }

    @Override
	public int available() 
	{
		try 
		{
			return socket.getInputStream().available();
		} 
		catch (IOException e) 
		{
		}
		
		return 0;
	}

    @Override
	public void checkConnected() throws Throwable
	{
    	try
		{
			socket.setSoTimeout( 100 );
			int r = getInputStream().read();
			socket.setSoTimeout( 0 );
	    	
	    	if ( r == -1 )
	    	{
	    		setBroken( true );
				throw new Exception( "connection is broken" );
	    	}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			throw e;
		}
	}

}
