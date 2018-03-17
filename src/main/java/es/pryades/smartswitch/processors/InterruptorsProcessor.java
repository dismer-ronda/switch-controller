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

	    String readInterruptorId() throws Throwable
	    {
	    	String ret = "";
	    	boolean complete=false;
	    	int count = 0;
	    	while ( !complete )
	    	{
	    		int b = transport.read( 3 * Utils.ONE_SECOND );
	    		
	    		complete = b == 0;
	    		if ( !complete )
	    			ret += (char)b;
	    		
	    		count++;
	    	}
	    	
	    	LOG.info( "interruptor " + ret + " request incoming. " + count + " bytes read" );
	    	
	    	return ret;
	    }

	    private String readInterruptorAddress() throws Throwable
	    {
	    	int[] buffer = new int[4];
	    	for ( int i = 0; i < 4; i++)
	    		buffer[i] = transport.read( 3 * Utils.ONE_SECOND );
	    	
	    	return Integer.toString( buffer[0] ) + "." + Integer.toString( buffer[1] ) + "." + Integer.toString( buffer[2] ) + "." + Integer.toString( buffer[3] ); 
	    }

	    private void sendCurrentTime() throws Throwable
	    {
	    	long date = System.currentTimeMillis();
	    	int offset = TimeZone.getDefault().getOffset(date);
	    	
			long seconds = (date+offset) / 1000;
			byte[] time = Utils.longToBytes( seconds );
			
			transport.write( time[7] );
			transport.write( time[6] );
			transport.write( time[5] );
			transport.write( time[4] );
			transport.write( 1 );
			transport.flushOutput();
	    }
	    
	    private void updateAddress() throws Throwable
	    {
			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );
			
			String name = readInterruptorId();
			
			Interruptor query = new Interruptor();
			query.setName( name );
			Interruptor interruptor = (Interruptor)IOCManager._InterruptorsManager.getRow( ctx, query );
			
			if ( interruptor != null )
			{
				LOG.info( "interruptor found " + interruptor );
				
				Interruptor clone = (Interruptor)Utils.clone( interruptor );
				clone.setAddress( readInterruptorAddress() );
				IOCManager._InterruptorsManager.setRow( ctx, interruptor, clone );
				
				transport.write( 1 );
			}
			else
				LOG.info( "interruptor " +  name + "not found" );
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
	    
	    private void sendPlan() throws Throwable
	    {
			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );
			
			String name = readInterruptorId();
			
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
				
				transport.write( plan );
				transport.write( 1 );
				transport.flushOutput();
			}
			else
				LOG.info( "interruptor " +  name + "not found" );
	    }

	    private void aliveSignal() throws Throwable
	    {
			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );
			
			String name = readInterruptorId();
			
			Interruptor query = new Interruptor();
			query.setName( name );
			Interruptor interruptor = (Interruptor)IOCManager._InterruptorsManager.getRow( ctx, query );
			
			if ( interruptor != null )
			{
				LOG.info( "interruptor found " + interruptor );
				
				Interruptor clone = (Interruptor)Utils.clone( interruptor );
				clone.setState( Integer.valueOf( transport.read( 3 * Utils.ONE_SECOND ) ) );
				clone.setAddress( readInterruptorAddress() );
				clone.setLast_signal( CalendarUtils.getServerDateAsLong() );

				IOCManager._InterruptorsManager.setRow( ctx, interruptor, clone );
				
				transport.write( 1 );
			}
			else
				LOG.info( "interruptor " +  name + "not found" );
	    }

	    public void run () 
	    {
			Status.incThreads();

	    	try 
	    	{
		    	int command  = transport.read( 3 * Utils.ONE_SECOND );
		    	
		    	switch ( command )
		    	{
		    		case 1:
		    			sendPlan();
		    		break;
		    		
		    		case 2:
		    			sendCurrentTime();
		    		break;

		    		case 3:
		    			updateAddress();
		    		break;

		    		case 4:
		    			aliveSignal();
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
