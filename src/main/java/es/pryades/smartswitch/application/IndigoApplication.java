package es.pryades.smartswitch.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.AppUtils;
import es.pryades.smartswitch.common.MessageDlg;
import es.pryades.smartswitch.common.Settings;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * The Application's "main" class
 */
@Theme(value = "smartswitch")
@PreserveOnRefresh
public class IndigoApplication extends UI 
{
	private static final long serialVersionUID = 683154667075459739L;

	private static final Logger LOG = Logger.getLogger( IndigoApplication.class );
	
	private MainWnd window;

	private AppContext ctx;
	
	private boolean logged;

	public IndigoApplication()
	{
	}

	private Locale getLanguage()
    {
        Locale locale;

        try 
        {
	        locale = AppUtils.getLocaleFromBrowser( this,  Settings.LANGUAGES );
	        if ( locale != null )
	        	return locale;
		} 
        catch ( Throwable e ) 
        {
        	Utils.logException( e, LOG );
        	
        	LOG.warn( "Browswer language is not supported. Default language will be used" );
		}
        
        return AppUtils.getDefaultLocale();
    }
	
	@Override
    public void init( VaadinRequest request )
    {
    	try
		{
	    	ctx = new AppContext( getLanguage().getLanguage() );
	    	
	    	ctx.addData( "Application", this );
	    	ctx.addData( "Theme", "VAADIN/themes/indigo/" );
	    	ctx.addData( "Url", Page.getCurrent().getLocation().toString() );
	    		    	
			String user = request.getParameter( "user" );
			String password = request.getParameter( "password" );
			
	    	showLoginWindow( user, password );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
    }
    
	public void closeAllWindows()
	{
   		Collection<Window> windows = getWindows();

   		if ( windows.size() > 0 )
   		{
   			ArrayList<Window> toClose = new ArrayList<Window>();

   			toClose.addAll( windows );
   			
   	    	for ( Window window : toClose )
   	    		removeWindow( window );
    	}	
	}

	public void showLoginWindow( String user, String password )
    {
    	logged = false;
		closeAllWindows();
		
    	IOCManager._ParametersManager.loadParameters( ctx );
		
		LoginWnd wnd = new LoginWnd( ctx, user, password );
		
		if ( !logged )
			setContent( wnd );
    }
    
    public void showMainWindow()
    {
    	logged = true;
    	
    	window = new MainWnd( ctx );
    	
    	setContent( window );
    	
    	ctx.addData( "MainWnd", window );
    	
    	window.buildMainLayout();
    	window.startMainWindow();
    }
    	
	void messageAndExit( String title, ResourceBundle resources, String message )
	{
		MessageDlg dlg = new MessageDlg( ctx, title, message );
		
		dlg.addCloseListener
		( 
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = -7921174154171992358L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					onClose( e );
			    }
			}
		);
		
		getUI().addWindow( dlg );
	}
	
	private void onClose( CloseEvent event )
	{
		showLoginWindow( null, null );
	}
}
