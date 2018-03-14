package es.pryades.smartswitch.application;

import java.util.HashMap;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.AppUtils;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.MessageDlg;
import es.pryades.smartswitch.common.MinimizerContainer;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.configuration.ConfigurationDlg;
import es.pryades.smartswitch.dashboard.Dashboard;

public class MainWnd extends VerticalLayout implements MinimizerContainer
{
	private static final Logger LOG = Logger.getLogger( MainWnd.class );

	private static final long serialVersionUID = 5722841685959892036L;

	private static final String AUTH_CONFIGURACION = "configuration";

	private HorizontalLayout logoBar;
	private HorizontalLayout buttonsBar;
	private HorizontalLayout topBar;
	private VerticalLayout workSpace;
	private HorizontalLayout minimizeSpace;
	
	@Getter
	private AppContext context;
	
	private HashMap<Component, Window> minimizeds;


	public MainWnd( AppContext context )
	{
		this.context = context;

		minimizeds = new HashMap<Component, Window>();
	}

	public void buildMainLayout()
	{
		setSizeFull();

		setMargin( false );
		setSpacing( false );

		addStyleName( "mainwnd" );

		addComponent( buildTopBar() );
		addComponent( buildWorkspace() );
		
		Dashboard dashboard = new Dashboard( getContext() );
		dashboard.setSizeFull();

		workSpace.addComponent( dashboard );
		workSpace.setExpandRatio( dashboard, 1.0f );
		
		try
		{
			dashboard.render();
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}

		setExpandRatio( workSpace, 1.0f );
	}

	private Component buildWorkspace()
	{
		workSpace = new VerticalLayout();
		workSpace.setMargin( true );
		workSpace.setSizeFull();

		return workSpace;
	}

	private Component buildTopBar()
	{
		topBar = new HorizontalLayout();
		topBar.setMargin( new MarginInfo( true, true, false, true ) );
		topBar.setWidth( "100%" );

		logoBar = new HorizontalLayout();
		logoBar.setSpacing( true );

		Image image = new Image( null, new ThemeResource( "images/main-logo.png" ) );
		logoBar.addComponent( image );

		/*Image image1 = new Image( null, new ThemeResource( "images/liv-iq-insights-text-small" + (Settings.getSetting( "beta", "false" ).equals( "true" ) ? "-beta" : "") + ".png" ) );
		logoBar.addComponent( image1 );
		logoBar.setComponentAlignment( image1, Alignment.MIDDLE_CENTER );*/

		buttonsBar = new HorizontalLayout();
		buttonsBar.setWidth( "100%" );

		addButtons();

		topBar.addComponent( logoBar );
		topBar.addComponent( buttonsBar );

		topBar.setExpandRatio( buttonsBar, 1.0f );

		return topBar;
	}

	private void addButtons()
	{
		buttonsBar.removeAllComponents();

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing( true );
		
		@SuppressWarnings("unused")
		String nombre = getContext().getUser().getName();

		Button btn;

		if ( getContext().hasRight( AUTH_CONFIGURACION ) )
		{
			btn = new Button( getContext().getString( "words.configuration" ) );
			btn.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
			//btn.addStyleName( "menu" );
			btn.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 3827413316030851767L;
	
				public void buttonClick( ClickEvent event )
				{
					doShowConfiguration();
				}
			} );
			buttons.addComponent( btn );
		}

		btn = new Button( getContext().getString( "words.my.account" ) );
		btn.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		//btn.addStyleName( "menu" );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				doShowUserProfile();
			}
		} );
		buttons.addComponent( btn );
		
		btn = new Button( getContext().getString( "words.logout" ) );
		btn.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		//btn.addStyleName( "menu" );
		btn.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 3827413316030851767L;

			public void buttonClick( ClickEvent event )
			{
				Logout();
			}
		} );
		buttons.addComponent( btn );
		
		buttonsBar.addComponent( buttons );
		buttonsBar.setComponentAlignment( buttons, Alignment.MIDDLE_RIGHT );
	}

	public void Logout()
	{
		IndigoApplication app = (IndigoApplication) getContext().getData( "Application" );
		app.showLoginWindow( null, null );
	}

	private void showMainApp()
	{
		try
		{
			//showCentersDlg();
		}
		catch ( Throwable e )
		{
			if ( !(e instanceof BaseException) )
				new BaseException( e, LOG, 0 );
		}
	}

	private void doShowUserProfile()
	{
		ProfileDlg dlg = new ProfileDlg( getContext().getString( "ProfileDlg.title" ) );

		dlg.setContext( getContext() );

		dlg.addComponents();

		getUI().addWindow( dlg );
	}

	private void doShowConfiguration()
	{
		ConfigurationDlg dlg = new ConfigurationDlg();

		dlg.setContext( getContext() );
		dlg.createComponents();

		getUI().addWindow( dlg );
	}

	public void messageAndExit( String title, String message )
	{
		MessageDlg dlg = new MessageDlg( getContext(), title, message );

		dlg.addCloseListener( new Window.CloseListener()
		{
			private static final long serialVersionUID = -5303587015039065226L;

			@Override
			public void windowClose( CloseEvent e )
			{
				Logout();
			}
		} );

		getUI().addWindow( dlg );
	}

	public void startMainWindow()
	{
		try
		{
			showMainApp();
		}
		catch ( Throwable e )
		{
			if ( e instanceof BaseException )
			{
				String error = AppUtils.getExceptionMessage( (BaseException)e, getContext() );

				if ( error.isEmpty() )
					error = getContext().getString( "error.unknown" );

				messageAndExit( getContext().getString( "words.error" ), error );
			}
			else
				messageAndExit( getContext().getString( "words.error" ), getContext().getString( "error.unknown" ) );
		}
	}
	
	@Override
	public void minimizeWindow( Window window, Component icon )
	{
		if ( minimizeSpace == null )
		{
			minimizeSpace = new HorizontalLayout();
			
			workSpace.addComponent( minimizeSpace );
		}
		
		minimizeSpace.addComponent( icon );
		
		minimizeds.put( icon, window );
		
		window.setVisible( false );
	}

	@Override
	public void restoreWindow( Component icon )
	{
		Window wnd = minimizeds.remove( icon );
		
		if ( wnd != null )
		{
			minimizeSpace.removeComponent( icon );
			
			wnd.setVisible( true );
			
			if ( minimizeds.isEmpty() )
			{
				workSpace.removeComponent( minimizeSpace );
				minimizeSpace = null;
			}
		}
	}
}
