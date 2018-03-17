package es.pryades.smartswitch.dashboard.tabs;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.CalendarUtils;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Facility;
import es.pryades.smartswitch.dto.FacilityInterruptor;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.ioc.IOCManager;

public class InterruptorsTab extends DashboardTab implements VtoControllerFactory 
{
	private static final long serialVersionUID = 2187733480133720698L;

	private static final Logger LOG = Logger.getLogger( InterruptorsTab.class );

	private VerticalLayout mainLayout;

	private VerticalLayout logLayout;
	@Getter @Setter private Table table;
	@Getter @Setter private List<Interruptor> interruptors;
	private Refresher refresher;
	
	public InterruptorsTab( AppContext context )
	{
		super( context );
	}
	
	@Override
	public Component getTabContent()
	{
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		
		logLayout = new VerticalLayout();
		logLayout.setSizeUndefined();
		logLayout.setMargin( true );
		logLayout.setSpacing( true );

		mainLayout.addComponent( logLayout );
		mainLayout.setExpandRatio( logLayout, 1.0f );
		
		refresher = new Refresher();
		
		refresher.setRefreshInterval( (int)(Utils.ONE_MINUTE) );
		refresher.addListener( new RefreshListener()
		{
			private static final long serialVersionUID = -7798907363887838527L;

			@Override
			public void refresh( Refresher source )
			{
				refreshContent();
			}
		} );

		addExtension( refresher );

		refreshContent();
		
		setTitle( getContext().getString( "interruptorsTab.title" ) );

		return mainLayout;
	}
	
	@SuppressWarnings("unchecked")
	public void refreshContent()
	{
		try
		{
			Facility query = new Facility();
			List<Facility> facilities = IOCManager._FacilitiesManager.getRows( getContext(), query );
			
			logLayout.removeAllComponents();
			
			for ( Facility facility : facilities )
			{
				CssLayout layoutFacility = new CssLayout();

				Label labelFacility = new Label();
				labelFacility.setWidth( "100%" );
				labelFacility.setValue( facility.getName() );
				
				layoutFacility.addComponent( labelFacility );
				
				for ( FacilityInterruptor fi : facility.getInterruptors() )
				{
					Interruptor interruptor = fi.getInterruptor();
					
					HorizontalLayout row = new HorizontalLayout();
					row.setWidth( "360px" );
					row.setSpacing( true );
					row.setMargin( true );
					
					VerticalLayout column = new VerticalLayout();
					column.setWidth( "100%" );
					column.setSpacing( true );
					
					Label label1 = new Label();
					label1.setWidth( "100%" );
					label1.setValue( interruptor.getName() );
					
					Long lastAlive = interruptor.getLast_signal();
					
					Label label2 = new Label();
					label2.setWidth( "100%" );
					label2.setValue( lastAlive != null ? CalendarUtils.getFormatedDate( lastAlive, "yyyy-MM-dd HH:mm:ss" ) : getContext().getString( "interruptorsTab.not.yet.connected" ) );
					
					column.addComponent( label1 );
					column.setComponentAlignment( label1, Alignment.MIDDLE_CENTER );
					column.addComponent( label2 );
					column.setComponentAlignment( label2, Alignment.MIDDLE_CENTER );
					
					row.addComponent( column );
					row.setExpandRatio( column, 1.0f );
					
					Integer state = interruptor.getState();
					
					if ( state != null && lastAlive != null )
					{
						Image image = new Image( null, new ThemeResource( "images/" + (state == null ? "off.png" : ((state.equals( 1 ) ? "on.png" : "off.png") ) ) ) );
						image.setEnabled( state != null && lastAlive != null );
						row.addComponent( image );
					}
					else
					{
						Image image = new Image( null, new ThemeResource( "images/empty.png" ) );
						row.addComponent( image );
					}
					
					if ( state == null ) 
						row.addStyleName( "critical" );
					else if ( lastAlive == null )
						row.addStyleName( "critical" );
					else 
					{
						long delay = Utils.getDurationInSeconds( lastAlive, CalendarUtils.getServerDateAsLong() );
						
						if ( delay < 240 )
							row.addStyleName( "normal" );
						else if ( delay < 480 )
							row.addStyleName( "warning" );
						else
							row.addStyleName( "critical" );
					}

					row.setComponentAlignment( column, Alignment.MIDDLE_CENTER );
					
					layoutFacility.addComponent( row );
				}

				logLayout.addComponent( layoutFacility );
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
