package es.pryades.smartswitch.processors;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.CalendarUtils;
import es.pryades.smartswitch.common.Settings;
import es.pryades.smartswitch.common.Status;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Holiday;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.transports.TcpServerTransport;

public class InterruptorsProcessor extends Thread
{
    private static final Logger LOG = Logger.getLogger( InterruptorsProcessor.class );
    
	Integer waiting;
    volatile boolean ended;

	public static InterruptorsProcessor instance = null;
	
	public InterruptorsProcessor()
	{
    	waiting = new Integer(0);
    	ended = false;
	}

	public static void startInstance()
	{		
		instance = new InterruptorsProcessor();
		
		instance.start();
	}
	
    public static void stopInstance() 
    {
    	LOG.info( "stop command received" );
    	
    	if ( instance != null )
    	{
    		instance.stopProcessor();

    		try 
			{
				instance.join();
			} 
			catch ( Throwable e ) 
			{
				Utils.logException( e, LOG );
			}
    	}
    }

	public static InterruptorsProcessor getInstance()
	{
		return instance;
	}
	
	public void waitIddle( long time )
	{
		synchronized ( waiting )
		{
			try 
			{
				waiting.wait( time );
			} 
			catch ( InterruptedException e ) 
			{
			}
		}
	}
	
	public synchronized void stopProcessor()
	{
		ended = true;

		synchronized( waiting )
		{
			waiting.notifyAll();
		}
	}
	
	public synchronized boolean isStopped()
	{
		return ended;
	}	
	
	@Override
	public void run()
	{
		LOG.info( "listening server started" );

		Status.incThreads();
		Status.setStarted( new Date() );

		while ( true  )
		{
			try
			{
				ServerSocket listener = new ServerSocket( Settings.INTERRUPTORS_PORT );
			      
				listener.setSoTimeout( 10000 );
				
				while ( true )
				{
					try
					{
						Socket bound = listener.accept();
					
						new IncomingConnection( new TcpServerTransport().bind( bound ) ).start();
					}
					catch ( Throwable e )
					{
					}
					
					if ( isStopped() )
						break;
			    }
				
				listener.close();
			} 
			catch ( Throwable e ) 
			{
				Utils.logException( e, LOG );

				Utils.Sleep( Utils.ONE_MINUTE );
			}

			if ( isStopped() )
				break;
		}
		
		LOG.info( "listening server finished" );

		Status.decThreads();

		LOG.info( "processor	 finished" );
	}
	
	class IncomingConnection extends Thread 
	{
		TcpServerTransport transport;

		IncomingConnection( TcpServerTransport transport ) 
	    {
    		this.transport = transport;
	    }

	    String readInterruptorId( int buffer[], int pos ) throws Throwable
	    {
	    	String ret = "";
	    	while ( buffer[pos] != 0 && pos < buffer.length )
	    		ret += (char)buffer[pos++];
	    	
	    	LOG.info( "interruptor " + ret + " request incoming. " );
	    	
	    	return ret;
	    }

	    private String readInterruptorAddress( int buffer[], int pos ) throws Throwable
	    {
	    	return Integer.toString( buffer[pos] ) + "." + Integer.toString( buffer[pos+1] ) + "." + Integer.toString( buffer[pos+2] ) + "." + Integer.toString( buffer[pos+3] ); 
	    }

	    private void sendCurrentTime( int buffer[] ) throws Throwable
	    {
	    	long date = System.currentTimeMillis();
	    	int offset = TimeZone.getDefault().getOffset(date);
	    	
			long seconds = (date+offset) / 1000;
			byte[] time = Utils.longToBytes( seconds );
			
			int length = 5;
			int[] temp = new int[length + 2];

			temp[0] = length;
			temp[1] = time[7];
			temp[2] = time[6];
			temp[3] = time[5];
			temp[4] = time[4];

			int crc = Utils.calculate_crc( temp, length );
			
			temp[5] = Utils.lobyte( crc );
			temp[6] = Utils.hibyte( crc );
			
			for ( int i = 0; i < temp.length; i++ )
				transport.write( temp[i] );
			transport.flushOutput();
	    }
	    
	    boolean isDayOfWeek( Calendar calendar, String value )
	    {
			int dow = calendar.get( Calendar.DAY_OF_WEEK );
			
			switch ( dow )
			{
				case Calendar.MONDAY:
					return value.equals( "1" );

				case Calendar.TUESDAY:
					return value.equals( "2" );

				case Calendar.WEDNESDAY:
					return value.equals( "3" );

				case Calendar.THURSDAY:
					return value.equals( "4" );

				case Calendar.FRIDAY:
					return value.equals( "5" );
					
				case Calendar.SATURDAY:
					return value.equals( "6" );

				case Calendar.SUNDAY:
					return value.equals( "7" );
			}
			
			return false;
	    }
	    
	    boolean isDayOfMonth( Calendar calendar, String value )
	    {
			int dom = calendar.get( Calendar.DAY_OF_MONTH );
			int month = calendar.get( Calendar.MONTH ) + 1;
			
			return String.format( "%02d%02d", month, dom ).equals( value );
	    }
	    
	    boolean isMonth( Calendar calendar, String value )
	    {
			int month = calendar.get( Calendar.MONTH ) + 1;
			
			return String.format( "%d", month ).equals( value );
	    }
	    
	    boolean isDate( Calendar calendar, String value )
	    {
			int dom = calendar.get( Calendar.DAY_OF_MONTH );
			int month = calendar.get( Calendar.MONTH ) + 1;
			int year = calendar.get( Calendar.YEAR );
			
			return String.format( "%04d%02d%02d", year, month, dom ).equals( value );
	    }
	    
	    byte[] getTodayPlay( Interruptor interruptor )
	    {
			Calendar calendar = CalendarUtils.getServerCurrentCalendar();
			
			List<Holiday> holidays = interruptor.getHolidays();
			
			for ( Holiday holiday : holidays )
			{
				switch ( holiday.getHoliday_type() )
				{
					case 0:
						if ( isDayOfWeek( calendar, holiday.getHoliday_value() ) )
						{
							LOG.info( "holiday plan for " + holiday.getHoliday_name() );

							return interruptor.getPlan_free();
						}
					break;
					
					case 1:
						if ( isDayOfMonth( calendar, holiday.getHoliday_value() ) )
						{
							LOG.info( "holiday plan for " + holiday.getHoliday_name() );

							return interruptor.getPlan_free();
						}
					break;
					
					case 2:
						if ( isMonth( calendar, holiday.getHoliday_value() ) )
						{
							LOG.info( "holiday plan for " + holiday.getHoliday_name() );

							return interruptor.getPlan_free();
						}
					break;
					
					case 3:
						if ( isDate( calendar, holiday.getHoliday_value() ) )
						{
							LOG.info( "holiday plan for " + holiday.getHoliday_name() );

							return interruptor.getPlan_free();
						}
					break;
				}
			}
			
			return null;
	    }
	    
	    private void sendPlan( int[] buffer ) throws Throwable
	    {
			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );
			
			String name = readInterruptorId( buffer, 2 );
			
			Interruptor query = new Interruptor();
			query.setName( name );
			Interruptor interruptor = (Interruptor)IOCManager._InterruptorsManager.getRow( ctx, query );
			
			LOG.info(  "" + interruptor );
			
			if ( interruptor != null )
			{
				LOG.info( "interruptor found " + interruptor );
				
				byte[] plan = getTodayPlay( interruptor );
				
				if ( plan == null )
				{
					LOG.info( "labor plan" );
					
					plan = interruptor.getPlan_labor();
				}
				
				int length = 2 + plan.length;
				int[] temp = new int[length+2];

				temp[0] = length;
				temp[1] = interruptor.getForced_action();
				for ( int i = 0; i < plan.length; i++ )
					temp[i+2] = plan[i];

				int crc = Utils.calculate_crc( temp, length );
				
				LOG.info( "plan CRC = " + String.format( "%X", crc ) );
				
				temp[length] = Utils.lobyte( crc );
				temp[length+1] = Utils.hibyte( crc );
				
				for ( int i = 0; i < temp.length; i++ )
					transport.write( temp[i] );
				transport.flushOutput();
			}
			else
				LOG.info( "interruptor " +  name + "not found" );
	    }

	    private void aliveSignal( int[] buffer ) throws Throwable
	    {
			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );
			
			String name = readInterruptorId( buffer, 2 );
			int state = (byte)buffer[name.length() + 3];
			String address = readInterruptorAddress( buffer, name.length() + 4 );
			
			Interruptor query = new Interruptor();
			query.setName( name );
			Interruptor interruptor = (Interruptor)IOCManager._InterruptorsManager.getRow( ctx, query );
			
			if ( interruptor != null )
			{
				LOG.info( "interruptor found " + interruptor );
				
				int length = 1 + 2;
				int[] temp = new int[length+2];

				temp[0] = length;
				temp[1] = interruptor.getReload_plan();
				temp[2] = interruptor.getForced_action();

				int crc = Utils.calculate_crc( temp, length );
				
				temp[3] = Utils.lobyte( crc );
				temp[4] = Utils.hibyte( crc );
				
				for ( int i = 0; i < temp.length; i++ )
					transport.write( temp[i] );
				transport.flushOutput();

				Interruptor clone = (Interruptor)Utils.clone( interruptor );
				clone.setState( state );
				clone.setAddress( address );
				clone.setLast_signal( CalendarUtils.getServerDateAsLong() );
				clone.setReload_plan( 0 );

				IOCManager._InterruptorsManager.setRow( ctx, interruptor, clone );
			}
			else
				LOG.info( "interruptor " +  name + "not found" );
	    }

	    private int[] readCommandBuffer()
	    {
	    	try
	    	{
		    	byte size = (byte)transport.read( 3 * Utils.ONE_SECOND );
	    	
		    	int[] buffer = new int[size];
		    	
		    	buffer[0] = size;
		    	for ( int i = 1; i < size; i++ )
		    		buffer[i] = transport.read( 3 * Utils.ONE_SECOND );
		    	
		    	byte crc_lo = (byte)transport.read( 3 * Utils.ONE_SECOND );
		    	byte crc_hi = (byte)transport.read( 3 * Utils.ONE_SECOND );
		    	
		    	int crc = Utils.calculate_crc( buffer, size );
		    	
		    	if ( Utils.lobyte( (short)crc ) == crc_lo && Utils.hibyte( (short)crc ) == crc_hi )
		    		return buffer;
		    	
		    	LOG.info( "FAILURE ---> CRC received=" + Utils.convertToHex( crc_hi ) + Utils.convertToHex( crc_lo ) + " CRC calculated=" + String.format( "%X", crc ) );
	    	}
	    	catch ( Throwable e )
	    	{
				Utils.logException( e, LOG );
	    	}
	    	
	    	return null;
	    }
	    
	    public void run () 
	    {
			Status.incThreads();

	    	try 
	    	{
		    	int[] buffer = readCommandBuffer();

		    	int command  = buffer[1];
		    	
		    	switch ( command )
		    	{
		    		case 1:
		    			sendPlan( buffer );
		    		break;
		    		
		    		case 2:
		    			sendCurrentTime( buffer );
		    		break;

		    		case 3:
		    			aliveSignal( buffer );
		    		break;
		    	}
	    	}
	    	catch ( Throwable e ) 
	      	{
				Utils.logException( e, LOG );
	      	}
	    	finally
	    	{
		    	LOG.info( "connection closed" );

		    	transport.disconnect();
	    	}

	    	Status.decThreads();
	    }
	}
}
