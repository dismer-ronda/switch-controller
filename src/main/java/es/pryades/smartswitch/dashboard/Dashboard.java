package es.pryades.smartswitch.dashboard;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dashboard.tabs.DashboardTab;
import es.pryades.smartswitch.dashboard.tabs.LogTab;

/**
 * @author Dismer Ronda
 * 
 */
public class Dashboard extends VerticalLayout implements SelectedTabChangeListener
{
	private static final long serialVersionUID = -6455560039333566825L;

	private static final Logger LOG = Logger.getLogger( Dashboard.class );

	@Getter @Setter private AppContext context;
	
	private Button bttnApply;
	
	private TabSheet tabsheet;

	private List<DashboardTab> tabs;

	private boolean refresh;

	public Dashboard( AppContext context ) 
	{
		this.context = context;
		
		tabs = new ArrayList<DashboardTab>();
		
		context.addData( "dashboard", this );
		
		refresh = false;
	}
	
	private void initComponents() throws BaseException
	{
		try
		{
			setSpacing( true );
			setSizeFull();
			
			addStyleName( "dashboard" );
			
			Component queryComponent = getQueryComponent();
			
			if ( queryComponent != null )
				addComponent( queryComponent );
			
			tabsheet = new TabSheet();
			
			tabsheet.setSizeFull();
			
			addComponent( tabsheet );

			setExpandRatio( tabsheet, 1.0f );

			createTabs();
			
			for ( DashboardTab tab : tabs )
			{
				tab.setSizeFull();
				
				tabsheet.addTab( tab );
				tabsheet.getTab( tab ).setCaption( tab.getTitle() );
			}
			
			tabsheet.addSelectedTabChangeListener( this );
			
			DashboardTab tab = (DashboardTab)tabsheet.getSelectedTab();
			if ( tab != null )
				selectTab( tab );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			
			if ( !(e instanceof BaseException) )
				throw new BaseException( e, LOG, BaseException.UNKNOWN );
			
			throw (BaseException)e;
		}
	}
	
	private boolean isQueryActive()
	{
		return false;
	}

	public Component getQueryComponent()
	{
		if ( isQueryActive() )
		{
			bttnApply = new Button();
			bttnApply.setSizeUndefined();
			bttnApply.setStyleName( "borderless left_margin" );
			bttnApply.setDescription( getContext().getString( "words.apply" ) );
			bttnApply.setIcon( new ThemeResource( "images/accept.png" ) );
			bttnApply.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 7406758301827706990L;

				@Override
				public void buttonClick( ClickEvent event )
				{
					onSearch();
				}
			});
	
			HorizontalLayout rowContainer = new HorizontalLayout();
			rowContainer.setWidth( "100%" );
			rowContainer.setStyleName( "query_container" );
			
			Panel panelInformation = new Panel();
			panelInformation.setStyleName( "borderless light" );
			panelInformation.setSizeFull();
			
			HorizontalLayout rowQuery = new HorizontalLayout();
			rowQuery.setSpacing( true );
			
			panelInformation.setContent( rowQuery );

			//rowQuery.addComponent( comboDate);
			//rowQuery.addComponent( fromRow );
			
			rowContainer.addComponent( panelInformation );
			
			return rowContainer;
		}
		
		return null;
	}
	
	private void onSearch()
	{
		boolean reload = true;
		
		if ( reload )
		{
			refreshVisibleContent();
			refresh = false;
		}
	}
	
	public void render() throws BaseException
	{
		this.initComponents();
	}
	
	public void refreshVisibleContent()
	{
		for ( DashboardTab tab : tabs )
		{
			tab.refreshContent();
			
			tabsheet.getTab( tab ).setCaption( tab.getTitle() );
		}
	}
	
	private void createLogTab()
	{
		LogTab tab = new LogTab( context );
		
		tab.initComponents();
	
		tabs.add( tab );
	}
	
	private void createTabs()
	{
		if ( getContext().hasRight( "main.log" ) && Utils.getEnviroment( "LOGFILE" ) != null )
			createLogTab();
	}

	private void selectTab( DashboardTab tab )
	{
		tab.defaultFocus();

		if ( refresh )
		{
			refreshVisibleContent();
			refresh = false;
		}
	}
	
	public void selectedTabChange( SelectedTabChangeEvent event )
	{
		Tab tab = tabsheet.getTab( event.getTabSheet().getSelectedTab() );
		
		if ( tab != null )
			selectTab( (DashboardTab)tab.getComponent() );
	}

	public void onDownloadReport()
	{
		DashboardTab tab = (DashboardTab)tabsheet.getSelectedTab();

		if ( tab != null )
			tab.onDownloadReport();
	}
	
	public void updateComponent()
	{
	}
}
