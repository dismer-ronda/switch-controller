package uk.linde.indigo.tests;

import java.io.IOException;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;

import es.pryades.smartswitch.application.I18N;
import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Settings;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dal.ibatis.DalManager;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * Unit test for simple App.
 */
public class DevicesTest extends TestCase
{
    private static final Logger LOG = Logger.getLogger( DevicesTest.class );
    
	public void setUp() 
	{
		
	}

	public void tearDown() 
	{
	}
	
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DevicesTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     * @throws IOException 
     */
    public static Test suite() throws IOException
    {
        return new TestSuite( DevicesTest.class );
    }
    
    /**
     * Rigourous Test :-)
     * @throws Exception 
     */
    public void testApp() throws Exception
    {
    	try 
    	{
			Settings.Init();
	    	IOCManager.Init();
	    	
	    	I18N.Init();

	    	DalManager.Init( Settings.DB_engine, 
	    			Settings.DB_driver, 
	    			Settings.DB_url, 
	    			Settings.DB_user, 
	    			Settings.DB_password );

	    	System.setProperty("java.net.preferIPv4Stack" , "true");
	    	
	    	String phantomjs = Utils.getEnviroment( "INSIGHTS_PHANTOMJS" );
	    	if ( phantomjs != null )
	    		System.setProperty( "phantom.exec", phantomjs );

	    	Properties p = new Properties();
	    	p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
	    	Velocity.init( p );

			AppContext ctx = new AppContext( "es" );
			IOCManager._ParametersManager.loadParameters( ctx );

	    	Settings.Init();
	    	
	    	// Inicialización de IOC
	    	IOCManager.Init();
	
	    	DalManager.Init( Settings.DB_engine, 
	    			Settings.DB_driver, 
	    			Settings.DB_url, 
	    			Settings.DB_user, 
	    			Settings.DB_password );
	    	
	    	/*DeviceQuery query = new DeviceQuery();
	    	query.setMac_address( "201608IA05712");
	    	query.setRepaired( "R-201608IA05712%" );
	    	
	    	List<Device> rows = IOCManager._DevicesManager.getRows( ctx, query );
	    	
	    	for ( Device device : rows )
	    		LOG.info( device.getSerial() );
	    	*/
	    	
			/*Device lastRepaired = IOCManager._DevicesManager.getRows( ctx, "R-201608IA05712%" );
			
			String suffix = "";
			
			if ( lastRepaired != null )
			{
				String serial1 = lastRepaired.getSerial();
				
				suffix = String.format( "%02d", Utils.getInt( serial1.substring( serial1.lastIndexOf( '-' ) + 1 ), 0 ) + 1 );
			}


			LOG.info( suffix );*/
		} 
    	catch ( Throwable e ) 
    	{
    		Utils.logException( e, LOG );
		}
    }
}

