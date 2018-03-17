package es.pryades.smartswitch.configuration.modals;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.AppUtils;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.ModalWindowsCRUD;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Holiday;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewHoliday extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = 5254610903741737945L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewHoliday.class );

	@Getter
	protected Holiday newHoliday;

	private TextField editName;
	private ComboBox comboEnabled;
	private TextField editValue;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewHoliday( AppContext context, Operation modalOperation, Holiday orgUser, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgUser );
		setWidth( "480px" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newHoliday = (Holiday) Utils.clone( (Holiday) orgDto );
		}
		catch ( Throwable e1 )
		{
			newHoliday = new Holiday();
		}

		bi = new BeanItem<BaseDto>( newHoliday );

		editName = new TextField( bi.getItemProperty( "holiday_name" ) );
		editName.setWidth( "100%" );
		editName.setNullRepresentation( "" );
		editName.setRequired( true );
		editName.setRequiredError( getContext().getString( "words.required" ) );

		comboEnabled = new ComboBox();
		comboEnabled.setWidth( "100%" );
		comboEnabled.setNullSelectionAllowed( false );
		comboEnabled.setTextInputAllowed( false );
		comboEnabled.setImmediate( true );
		comboEnabled.setPropertyDataSource( bi.getItemProperty( "holiday_type" ) );
		comboEnabled.setReadOnly( false );
		fillComboEnabled();

		editValue = new TextField( bi.getItemProperty( "holiday_value" ) );
		editValue.setWidth( "100%" );
		editValue.setNullRepresentation( "" );
		editValue.setRequired( true );
		editValue.setRequiredError( getContext().getString( "words.required" ) );

		int tabIndex = 1;
		
		editName.setTabIndex( tabIndex++ );
		HorizontalLayout rowName = AppUtils.rowComponent( getContext(), editName, "modalNewFacility.name", null );
		
		comboEnabled.setTabIndex( tabIndex++ );
		HorizontalLayout rowType = AppUtils.rowComponent( getContext(), comboEnabled, "modalNewHoliday.holiday_type", null );

		editValue.setTabIndex( tabIndex++ );
		HorizontalLayout rowValue = AppUtils.rowComponent( getContext(), editValue, "modalNewHoliday.value", null );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( rowName );
		row1.addComponent( rowType );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( rowValue );
		row2.addComponent( new HorizontalLayout() );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewHoliday";
	}

	@Override
	protected void defaultFocus()
	{
		editName.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newHoliday.setId( null );
			
			IOCManager._HolidaysManager.setRow( getContext(), null, newHoliday );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			IOCManager._HolidaysManager.setRow( getContext(), (Holiday) orgDto, newHoliday );
	
			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onDelete()
	{
		try
		{
			IOCManager._HolidaysManager.delRow( getContext(), newHoliday );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	public void refreshVisibleContent( boolean repage )
	{
	}

	private void fillComboEnabled()
	{
		for ( int i = 0; i < 4; i++ )
		{
			comboEnabled.addItem( Integer.valueOf( i ) );
			comboEnabled.setItemCaption( Integer.valueOf( i ), getContext().getString( "holiday.type." + i ) );
		}
	}
}
