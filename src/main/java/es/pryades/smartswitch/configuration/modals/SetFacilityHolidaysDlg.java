package es.pryades.smartswitch.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Facility;
import es.pryades.smartswitch.dto.FacilityHoliday;
import es.pryades.smartswitch.dto.Holiday;
import es.pryades.smartswitch.ioc.IOCManager;

public class SetFacilityHolidaysDlg extends Window 
{
	private static final long serialVersionUID = -4429606894598148870L;

	private static final Logger LOG = Logger.getLogger( SetFacilityHolidaysDlg.class );

	@Getter @Setter private AppContext context;
	@Getter private VerticalLayout mainLayout;
	private Facility facility;
	private List<CheckBox> checks;
	private ModalParent parent;
	private boolean reload;

	public SetFacilityHolidaysDlg( Facility facility, ModalParent parent )
	{
		this.facility = facility;
		this.parent = parent;
		
		reload = false;
	}
	
	public boolean hasMinimizeIcon()
	{
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void createComponents()
	{
		setCaption( getContext().getString( "selectFacilityHolidaysDlg.title" ) );

		setModal( true );
		setResizable( false );
		setClosable( false );

		//setCloseShortcut( KeyCode.ESCAPE );
		
		setWidth( "720px" ); 
		setHeight( "480px" );
		
		setContent( mainLayout = new VerticalLayout() );
		
		mainLayout.setSizeFull();
		
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		Panel panel = new Panel();

		panel.setSizeFull();
		panel.setStyleName( "borderless light" );
		
		HorizontalLayout rowButtons = new HorizontalLayout();
		rowButtons.setSpacing( true );
		
		Button btnClose = new Button();
		btnClose.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		btnClose.setCaption( getContext().getString( "words.close" ) );
		btnClose.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 8355161094089454796L;

			public void buttonClick( ClickEvent event )
			{
				onCancel();
			}
		} );

		rowButtons.addComponent( btnClose );
		
		mainLayout.addComponent( panel );
		mainLayout.addComponent( rowButtons );
		mainLayout.setComponentAlignment( rowButtons, Alignment.BOTTOM_RIGHT );
		mainLayout.setExpandRatio( panel, 1.0f );
		
		CssLayout holidaysLayout = new CssLayout();
		
		panel.setContent( holidaysLayout );

		try
		{
			Holiday query = new Holiday();
			List<Holiday> holidays = IOCManager._HolidaysManager.getRows( getContext(), query );
			
			checks = new ArrayList<CheckBox>();
			
			for ( Holiday holiday : holidays )
			{
				boolean checked = currentlyChecked( holiday );
				
				CheckBox cb = new CheckBox();
				cb.setCaption( holiday.getHoliday_name() );
				cb.setStyleName( checked ? "holiday-checked" : "holiday" );
				cb.setValue( checked );
				cb.setData( holiday );
				cb.addValueChangeListener( new Property.ValueChangeListener() {
					private static final long serialVersionUID = -7851085707155697875L;

					public void valueChange(ValueChangeEvent event) {
		                onCheckedHoliday( (CheckBox)event.getProperty() );
		            }
		        });
		        cb.setImmediate(true);

				holidaysLayout.addComponent( cb );
				
				checks.add( cb );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
			
		center();
	}	
	
	private boolean currentlyChecked( Holiday holiday )
	{
		for ( FacilityHoliday fh : facility.getHolidays() )
			if ( fh.getHoliday().getHoliday_type().equals( holiday.getHoliday_type() ) && fh.getHoliday().getHoliday_value().equals( holiday.getHoliday_value() ) )
				return true;
		
		return false;
	}
	
	private void onCheckedHoliday( CheckBox check )
	{
		Holiday holiday = (Holiday)check.getData();
		
		try
		{
			if ( check.getValue().booleanValue() )
			{
				check.setStyleName( "holiday-checked" );

				FacilityHoliday fh = new FacilityHoliday();
				fh.setRef_facility( facility.getId() );
				fh.setRef_holiday( holiday.getId() );
				
				IOCManager._FacilityHolidaysManager.setRow( getContext(), null, fh );
				
				facility.getHolidays().add( fh );

				reload = true;
			}
			else
			{
				check.setStyleName( "holiday" );

				FacilityHoliday fh = new FacilityHoliday();
				fh.setRef_facility( facility.getId() );
				fh.setRef_holiday( holiday.getId() );
				
				IOCManager._FacilityHolidaysManager.delRow( getContext(), fh );
				
				reload = true;
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	void onCancel()
	{
		if ( reload )
			parent.refreshVisibleContent( false );
		
       	getUI().removeWindow( this );
	}
}
