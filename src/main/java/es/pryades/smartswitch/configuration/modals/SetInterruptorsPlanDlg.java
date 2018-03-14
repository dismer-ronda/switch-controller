package es.pryades.smartswitch.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.google.gwt.dev.util.collect.HashMap;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.ioc.IOCManager;

public class SetInterruptorsPlanDlg extends Window 
{
	private static final Logger LOG = Logger.getLogger( SetInterruptorsPlanDlg.class );

	private static final long serialVersionUID = 655166111009354902L;
	
	@Getter @Setter private AppContext context;
	@Getter private VerticalLayout mainLayout;
	@Getter private VerticalLayout workLayout;
	@Getter @Setter private Table table;
	@Getter @Setter private List<Interruptor> interruptors;
	private List<String> columns;
	private HashMap<Long, Object> mapRows;
	private boolean labor;
	private double limit;

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
		
		setWidth( "1600px" ); 
		setHeight( "800px" );
		
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
		
		table = new Table();
		table.setSizeFull();
		
		String column1 = getContext().getString( "SelectInterruptorsPlanDlg.interruptor" );
		table.addContainerProperty( column1, String.class, null  );
		table.setColumnAlignment( column1, Align.LEFT );
		columns = new ArrayList<String>();

		for ( int hour = 0; hour < 24; hour++ )
    	{
        	for ( int min = 0; min < 4; min++ )
        	{
        		String column = String.format( "%02d:%02d", hour, min * 15 );
        		columns.add( column );
        		
        		table.addContainerProperty( column, CheckBox.class, null  );
        		table.setColumnAlignment( column, Align.CENTER );
        	}
    	}

		mapRows = new HashMap<Long, Object>();
		
		for ( Interruptor interruptor : interruptors )
		{
			Object tableItem = table.addItem();
			mapRows.put( interruptor.getId(), tableItem );
			
			Item row1 = table.getItem( tableItem );
			
			byte[] plan = labor ? interruptor.getPlan_labor() : interruptor.getPlan_free();
			
			for ( int hour = 0; hour < 24; hour++ )
			{
        		row1.getItemProperty( column1 ).setValue( interruptor.getName() );
	        	
        		for ( int min = 0; min < 4; min++ )
        		{
    				String column = columns.get( hour * 4 + min  );
    				
    				CheckBox check = new CheckBox();
    				check.setData( new Integer( hour * 4 + min ) );
    				check.setValue( plan[hour * 4 + min] == 1 );
	        		
    				row1.getItemProperty( column ).setValue( check );

    				check.addValueChangeListener( new Property.ValueChangeListener() {
						private static final long serialVersionUID = 6680388383216397510L;

						public void valueChange(ValueChangeEvent event) {
							
    		                onChangedColumn( (Integer)((CheckBox)event.getProperty()).getData() );
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
			
		getWorkLayout().addComponent( table );

		center();
	}	
	
	private double getColumnTotal( int column )
	{
		double sum = 0;
		for ( Interruptor interruptor : interruptors )
		{
			Item row = table.getItem( mapRows.get( interruptor.getId() ) );
			
			if ( row != null )
			{
				CheckBox check = (CheckBox)row.getItemProperty( columns.get( column ) ).getValue();
				
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
			Item row = table.getItem( mapRows.get( interruptor.getId() ) );
			
			if ( row != null )
			{
				try
				{
					Interruptor clone = (Interruptor)Utils.clone( interruptor );
	
					byte [] plan = new byte[96];
					
					for ( int hour = 0; hour < 24; hour++ )
					{
		        		for ( int min = 0; min < 4; min++ )
		        		{
		    				String column = columns.get( hour * 4 + min );
			        		
		    				if ( ((CheckBox)row.getItemProperty( column ).getValue()).getValue().booleanValue() )
		    					plan[hour*4+min] = 1;
		    				else	
		    					plan[hour*4+min] = 0;
		        		}
					}

					if ( labor )
						interruptor.setPlan_labor( plan );
					else
						interruptor.setPlan_free( plan );
					
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
		double total = getColumnTotal( column );
		table.setColumnFooter( columns.get( column ), String.valueOf( total ) );
		if ( total > limit )
			Utils.showNotification( getContext(), getContext().getString( "words.exceded" ) + Math.abs( limit - total ), Notification.Type.WARNING_MESSAGE );
	}
}
