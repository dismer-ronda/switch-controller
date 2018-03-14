package es.pryades.smartswitch.services;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.Status;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.processors.InterruptorsProcessor;
import es.pryades.smartswitch.processors.SignalsProcessor;
import es.pryades.smartswitch.processors.TasksProcessor;

public class BootLoader extends Thread
{
    private static final Logger LOG = Logger.getLogger( BootLoader.class );

    static BootLoader instance;
    
	public static void bootup()
	{
		instance = new BootLoader();
		
		instance.start();
	}
 
	public BootLoader()
	{
	}
	
	@Override
	public void run()
	{
		try
		{
	    	while ( true )
			{
				try
				{
					Status.initInstance();
			    	
					TasksProcessor.startProcessor();
			    	SignalsProcessor.startProcessor();
			    	InterruptorsProcessor.startInstance();
			    	
			    	break;
				}
				catch ( Throwable e1 )
				{
					Utils.logException( e1, LOG );
				}
				
				LOG.info( "not ready, waiting to retry ..." );
				
				Utils.Sleep( Utils.ONE_SECOND * 30 );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}

		LOG.info( "completed" );
	}
}
