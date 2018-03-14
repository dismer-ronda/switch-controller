package es.pryades.smartswitch.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Status 
{
    public static Status instance;

	private String started = new String();
	private String last = new String();

	private Integer errors = new Integer( 0 );
	private Integer success = new Integer( 0 );
	
	private Integer threads = new Integer( 0 );

	public static void initInstance()
	{
		instance = new Status();
	}
 
	public Status()
	{
	}

	static public String getStarted()
	{
		synchronized( instance )
		{
			return instance.started;
		}
	}
	
	static public void setStarted( Date started )
	{
		synchronized( instance )
		{
			instance.started = new SimpleDateFormat( "dd/MM/yyyy HH:mm").format( started );
		}
	}

	static public void clear()
	{
		synchronized ( instance )
		{
			instance.errors = 0;
			instance.success = 0;
		}
	}
	
	static public void setLast( Date last )
	{
		synchronized( instance )
		{
			instance.last = new SimpleDateFormat( "dd/MM/yyyy HH:mm").format( last );
		}
	}

	static public String getLast()
	{
		synchronized( instance )
		{
			return instance.last;
		}
	}

	static public Integer getErrors()
	{
		synchronized( instance )
		{
			return instance.errors;
		}
	}

	static public void incErrors()
	{
		synchronized( instance )
		{
			instance.errors++;
		}
	}

	static public Integer getSuccess()
	{
		synchronized( instance )
		{
			return instance.success;
		}
	}

	static public void incSuccess( int n )
	{
		synchronized( instance )
		{
			instance.success += n;
		}
	}

	static public Integer getThreads()
	{
		synchronized( instance )
		{
			return instance.threads;
		}
	}

	static public void incThreads()
	{
		synchronized( instance )
		{
			instance.threads++;
		}
	}

	static public void decThreads()
	{
		synchronized( instance )
		{
			instance.threads--;
		}
	}
}
