package es.pryades.smartswitch.dashboard.tabs;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;

public class LogTab extends DashboardTab implements VtoControllerFactory 
{
	private static final long serialVersionUID = 2475160423063824074L;

	private static final Logger LOG = Logger.getLogger( LogTab.class );

	private VerticalLayout mainLayout;

	private HorizontalLayout queryLayout;
	private VerticalLayout logLayout;
	private TextField textLines;
	private TextField textGrep;
	private TextArea textLog;
	
	public LogTab( AppContext context )
	{
		super( context );
	}
	
	@Override
	public Component getTabContent()
	{
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		queryLayout = new HorizontalLayout();
		queryLayout.setMargin( true );
		queryLayout.setSpacing( true );
		
		logLayout = new VerticalLayout();
		logLayout.setSizeFull();
		logLayout.setMargin( new MarginInfo( false, true, true, true ) );
		logLayout.setSpacing( true );
		
		textLines = new TextField( getContext().getString( "words.lines" ) );
		textLines.setValue( "100" );
		
		textGrep = new TextField( getContext().getString( "words.search" ) );
		textGrep.setValue( "" );
		
		Button bttnApply = new Button();
		bttnApply.setSizeUndefined();
		bttnApply.setStyleName( "borderless left_margin" );
		bttnApply.setDescription( getContext().getString( "words.search" ) );
		bttnApply.setIcon( new ThemeResource( "images/search-24.png" ) );
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7406758301827706990L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshContent();
			}
		});
		
		queryLayout.addComponent( textLines );
		queryLayout.addComponent( textGrep );
		queryLayout.addComponent( bttnApply );
		queryLayout.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		textLog = new TextArea();
		textLog.setSizeFull();
		textLog.setReadOnly( true );

		logLayout.addComponent( textLog );
		
		mainLayout.addComponent( queryLayout );
		mainLayout.addComponent( logLayout );
		mainLayout.setExpandRatio( logLayout, 1.0f );
		
		refreshContent();
		
		setTitle( getContext().getString( "logWidget.title" ) );

		return mainLayout;
	}
	
	public void refreshContent()
	{
		try
		{
			String logFile = Utils.getEnviroment( "LOGFILE" );
			
			if ( logFile != null )
			{
				int lines = Utils.getInt( textLines.getValue(), 100 );
				String find = textGrep.getValue().isEmpty() ? "" : (" | grep \"" + textGrep.getValue() + "\"");
				String command = "tail -" + lines + " " + logFile + find;
				
				String log = Utils.cmdExec( command );
				
				textLog.setReadOnly( false );
				textLog.setValue( log );
				textLog.setWordwrap( false );
				textLog.setReadOnly( true );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return null;
	}

	@Override
	public BaseDto getFieldDto()
	{
		return null;
	}

	@Override
	public BaseManager getFieldManagerImp()
	{
		return null;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}

	public String[] getVisibleCols()
	{
		return null;
	}

	@Override
	public void onDownloadReport()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}

	@Override
	public void defaultFocus()
	{
	}
}
