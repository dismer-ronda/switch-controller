package es.pryades.smartswitch.configuration.modals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.ioc.IOCManager;

public class SetInterruptorsPlanDlg extends Window 
{
	private static final Logger LOG = Logger.getLogger( SetInterruptorsPlanDlg.class );

	private static final long serialVersionUID = 655166111009354902L;
	
	@Getter @Setter private AppContext context;
	@Getter private VerticalLayout mainLayout;
	@Getter private VerticalLayout workLayout;
	@Getter @Setter private List<Interruptor> interruptors;
	private List<String> columns;
	private boolean labor;
	private double limit;
	private TabSheet tabsheet;
	private HashMap<Long, List<CheckBox>> mapChecks;
	private List<Table> tables;

	public SetInterruptorsPlanDlg( boolean labor, double limit )
	{
		this.labor = labor;
		this.limit = limit;
	}
	
	public boolean hasMinimizeIcon()
	{
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void createComponents()
	{
		setCaption( getContext().getString( "SelectInterruptorsPlanDlg.title" ) );

		setModal( true );
		setResizable( false );
		setClosable( false );

		//setCloseShortcut( KeyCode.ESCAPE );
		
		setWidth( "1200px" ); 
		setHeight( "640px" );
		
		setContent( mainLayout = new VerticalLayout() );
		
		mainLayout.setSizeFull();
		
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		Panel panel = new Panel();

		panel.setSizeFull();
		panel.setStyleName( "borderless light" );
		
		workLayout = new VerticalLayout();
		workLayout.setSizeFull();
		
		panel.setContent( workLayout );
		
		HorizontalLayout rowButtons = new HorizontalLayout();
		rowButtons.setSpacing( true );
		
		Button btnExport = new Button();
		btnExport.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		btnExport.setCaption( getContext().getString( "words.ok" ) );
		btnExport.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -2068205560012385993L;

			public void buttonClick( ClickEvent event )
			{
				onOk();
			}
		} );
		
		Button btnUpdate = new Button();
		btnUpdate.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		btnUpdate.setCaption( getContext().getString( "words.cancel" ) );
		btnUpdate.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 8355161094089454796L;

			public void buttonClick( ClickEvent event )
			{
				onCancel();
			}
		} );

		rowButtons.addComponent( btnExport );
		rowButtons.addComponent( btnUpdate );
		
		mainLayout.addComponent( panel );
		mainLayout.addComponent( rowButtons );
		mainLayout.setComponentAlignment( rowButtons, Alignment.BOTTOM_RIGHT );
		mainLayout.setExpandRatio( panel, 1.0f );
		
		tabsheet = new TabSheet();
		tabsheet.setSizeFull();
		
		columns = new ArrayList<String>();
		
		mapChecks = new HashMap<Long, List<CheckBox>>();
		tables = new ArrayList<Table>();
		
		for ( Interruptor interruptor : interruptors )
			mapChecks.put( interruptor.getId(), new ArrayList<CheckBox>() );
		
		try
		{
			int hours = getContext().getIntegerParameter( Parameter.PAR_HOURS_PER_PERIOD );
			int periods = 24 / hours;
			
			for ( int period = 0; period < periods; period++ )
			{
				Table table = new Table();
				table.setSizeFull();
				
				tables.add( table );
				
				String column1 = getContext().getString( "SelectInterruptorsPlanDlg.interruptor" );
				table.addContainerProperty( column1, String.class, null  );
				table.setColumnAlignment( column1, Align.LEFT );
		
				for ( int hour = 0; hour < hours; hour++ )
		    	{
		        	for ( int min = 0; min < 4; min++ )
		        	{
		        		String column = String.format( "%02d:%02d", period * hours + hour, min * 15 );
		        		columns.add( column );
		        		
		        		table.addContainerProperty( column, CheckBox.class, null  );
		        		table.setColumnAlignment( column, Align.CENTER );
		        		table.setColumnWidth( column, 48 );
		        	}
		    	}
		
				for ( Interruptor interruptor : interruptors )
				{
					Object tableItem = table.addItem();

					List<CheckBox> listChecks = mapChecks.get( interruptor.getId() );
					
					Item row1 = table.getItem( tableItem );
					
					byte[] plan = labor ? interruptor.getPlan_labor() : interruptor.getPlan_free();
					
					for ( int hour = 0; hour < hours; hour++ )
					{
		        		row1.getItemProperty( column1 ).setValue( interruptor.getName() );
			        	
		        		for ( int min = 0; min < 4; min++ )
		        		{
		        			int index = period * (4*hours) + (hour * 4) + min;

		    				String column = columns.get(index);
		    				
		    				boolean checked = (plan[index] == 1);
		    				
		    				CheckBox check = new CheckBox();
		    				check.setData( new Integer(index) );
		    				check.setValue( checked );
							check.setStyleName( checked ? "hour-checked" : "hour" );
		    				
		    				row1.getItemProperty( column ).setValue( check );
		    				
		    				listChecks.add( check );
		
		    				check.addValueChangeListener( new Property.ValueChangeListener() {
								private static final long serialVersionUID = 6680388383216397510L;
		
								public void valueChange(ValueChangeEvent event) 
								{
									CheckBox cb = (CheckBox)event.getProperty();
									cb.setStyleName( cb.getValue().booleanValue() ? "hour-checked" : "hour" );
									
		    		                onChangedColumn( (Integer)cb.getData() );
		    		            }
		    		        });
			        		check.setImmediate(true);
		        		}
					}
				}
		
				table.setFooterVisible(true);
		
				table.setColumnFooter( column1, getContext().getString( "words.total.kw" ) );
				for ( int i = 0; i < columns.size(); i++ )
					table.setColumnFooter( columns.get( i ), String.valueOf( getColumnTotal( i ) ) );
					
				tabsheet.addTab( table, period );
				tabsheet.getTab( period ).setCaption( String.format( "%02d:00 - %02d:45", period * hours, (period+1) * hours - 1 ) );
			}
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
			
		tabsheet.setSelectedTab( 0 );
		
		getWorkLayout().addComponent( tabsheet );

		center();
	}	
	
	private double getColumnTotal( int column )
	{
		double sum = 0;
		for ( Interruptor interruptor : interruptors )
		{
			List<CheckBox> checksInterruptor = mapChecks.get( interruptor.getId() );
			
			if ( checksInterruptor != null )
			{
				CheckBox check = (CheckBox)checksInterruptor.get( column );
				
				if ( check != null )
					if ( check.getValue().booleanValue() )
						sum += interruptor.getPower();
			}
		}
		
		return sum;
	}
	
	void onOk()
	{
		for ( Interruptor interruptor : interruptors )
		{
			List<CheckBox> checksInterruptor = mapChecks.get( interruptor.getId() );
			
			if ( checksInterruptor != null )
			{
				try
				{
					Interruptor clone = (Interruptor)Utils.clone( interruptor );
	
					byte [] plan = new byte[96];
					for ( int i = 0; i < 96; i++ )
						plan[i] = checksInterruptor.get(i).getValue().booleanValue() ? (byte)1 : 0;

					if ( labor )
						interruptor.setPlan_labor( plan );
					else
						interruptor.setPlan_free( plan );
					
					interruptor.setReload_plan( 1 );
					
					IOCManager._InterruptorsManager.setRow( getContext(), clone, interruptor );	
				}
				catch ( Throwable e ) 
				{
					Utils.logException( e, LOG );
				}
			}
		}

		getUI().removeWindow( this );
	}
	
	void onCancel()
	{
       	getUI().removeWindow( this );
	}
	
	private void onChangedColumn( Integer column )
	{
		int hours = getContext().getIntegerParameter( Parameter.PAR_HOURS_PER_PERIOD );
		
		double total = getColumnTotal( column );
		
		int tableIndex = column / (hours *4);
		
		Table table = tables.get( tableIndex );
		table.setColumnFooter( columns.get( column ), String.valueOf( total ) );
		
		if ( total > limit )
			Utils.showNotification( getContext(), getContext().getString( "words.exceded" ) + Utils.roundDouble( Math.abs( limit - total ), 2 ), Notification.Type.WARNING_MESSAGE );
	}
}
