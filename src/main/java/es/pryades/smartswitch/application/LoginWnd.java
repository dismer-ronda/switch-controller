package es.pryades.smartswitch.application;

import org.apache.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;

public class LoginWnd extends VerticalLayout 
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( LoginWnd.class );

	private static final long serialVersionUID = 5722841682959892036L;

	private LoginPanel loginPanel;
	
	public LoginWnd( AppContext ctx, String user, String password )
	{
		super();
	    
		setMargin( false );
		setSpacing( false );
		
		int height = UI.getCurrent().getPage().getBrowserWindowHeight();

		/*int width = UI.getCurrent().getPage().getBrowserWindowWidth();
		
		String res;
		
		if ( width <= 1024 )
			res = "lo";
		else if ( width <= 1600 )
			res = "med";
		else
			res = "hi";*/
		
		setSizeFull();
		//setStyleName( "indigo_mainlogin-" + ((System.currentTimeMillis() / 60000) % 5) + "-" + res );
		setStyleName( "indigo_mainlogin" );
		
		HorizontalLayout rowLogos = new HorizontalLayout();
		rowLogos.setMargin( true );
		rowLogos.setHeight( "" + height/3 + "px" );
		
		HorizontalLayout rowImages = new HorizontalLayout();
		rowImages.setMargin( true );
		rowImages.setSpacing( true );

		Image image = new Image( null, new ThemeResource( "images/login-logo.png" ) );
		rowImages.addComponent( image );
		rowImages.setComponentAlignment( image, Alignment.MIDDLE_CENTER );

		/*Image image1 = new Image( null, new ThemeResource( "images/liv-iq-insights-text" + (Settings.getSetting( "beta", "false" ).equals( "true" ) ? "-beta" : "") + ".png" ) );
		rowImages.addComponent( image1 );
		rowImages.setComponentAlignment( image1, Alignment.MIDDLE_CENTER );*/
 
		rowLogos.addComponent( rowImages );
		
		HorizontalLayout row = new HorizontalLayout();
		row.setSpacing( true );
		row.setHeight( "" + height/3 + "px" );
		row.setWidth( "100%" );
		
		VerticalLayout loginCol = new VerticalLayout();
	    loginPanel = new LoginPanel( ctx, this, user, password );
	    loginCol.addComponent( loginPanel );
	    loginCol.setComponentAlignment( loginPanel, Alignment.MIDDLE_CENTER );
	    row.addComponent( loginCol );

		HorizontalLayout dummyRow = new HorizontalLayout();
		dummyRow.setHeight( "" + height/3 + "px" );
	    
	    addComponent( rowLogos );
	    addComponent( row );
	    addComponent( dummyRow );
	}
}
