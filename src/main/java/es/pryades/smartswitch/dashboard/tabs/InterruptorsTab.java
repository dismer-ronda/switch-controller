package es.pryades.smartswitch.dashboard.tabs;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.google.gwt.dev.util.collect.HashMap;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Table.Align;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.CalendarUtils;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.common.VtoControllerFactory;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
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
		logLayout.setMargin( new MarginInfo( false, true, true, true ) );
		logLayout.setSpacing( true );

		mainLayout.addComponent( logLayout );
		mainLayout.setExpandRatio( logLayout, 1.0f );
		
		refresher = new Refresher();
		
		refresher.setRefreshInterval( (int)(3 * Utils.ONE_MINUTE) );
		refresher.addListener( new RefreshListener()
		{
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
	
	public void refreshContent()
	{
		try
		{
			Interruptor query = new Interruptor();
			interruptors = IOCManager._InterruptorsManager.getRows( getContext(), query );
			
			logLayout.removeAllComponents();
			
			for ( int i = 0; i < interruptors.size(); i++ )
			{
				Interruptor interruptor = interruptors.get( i );
				
				HorizontalLayout row = new HorizontalLayout();
				row.setSpacing( true );
				row.setMargin( true );
				
				VerticalLayout column = new VerticalLayout();
				column.setWidth( "240px" );
				column.setSpacing( true );
				
				Label label1 = new Label();
				label1.setWidth( "100%" );
				label1.setValue( interruptor.getName() );
				
				Long lastAlive = interruptor.getLast_signal();
				
				Label label2 = new Label();
				label2.setWidth( "100%" );
				label2.setValue( lastAlive != null ? CalendarUtils.getFormatedDate( lastAlive, "HH:mm:ss" ) : getContext().getString( "interruptorsTab.not.yet.connected" ) );
				
				column.addComponent( label1 );
				column.addComponent( label2 );
				
				row.addComponent( column );
				row.setComponentAlignment( column, Alignment.MIDDLE_CENTER );
				
				Integer state = interruptor.getState();
				
				if ( state != null && lastAlive != null )
				{
					Image image = new Image( null, new ThemeResource( "images/" + (state == null ? "off.png" : ((state.equals( 1 ) ? "on.png" : "off.png") ) ) ) );
					image.setEnabled( state != null && lastAlive != null );
					row.addComponent( image );
				}
				
				logLayout.addComponent( row );
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
