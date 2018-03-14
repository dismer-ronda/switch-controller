package es.pryades.smartswitch.configuration.modals;

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
import es.pryades.smartswitch.dto.FacilityInterruptor;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewInterruptor extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -244177725004915696L;

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalNewInterruptor.class );

	protected Interruptor newInterruptor;

	private TextField editOrder;
	private TextField editName;
	private TextField editDescription;
	private TextField editAddress;
	private TextField editPower;
	private ComboBox comboEnabled;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewInterruptor( AppContext context, Operation modalOperation, Interruptor orgUser, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgUser );
		setWidth( "640px" );
	}

	private byte [] getNewPlan()
	{
		byte[] plan1 = new byte[96];
		for ( int i = 0; i < plan1.length; i++ )
			plan1[i] = 0;
		return plan1;
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newInterruptor = (Interruptor) Utils.clone( (Interruptor) orgDto );
		}
		catch ( Throwable e1 )
		{
			newInterruptor = new Interruptor();
			
			newInterruptor.setPlan_labor( getNewPlan() );
			newInterruptor.setPlan_free( getNewPlan() );
			newInterruptor.setEnabled(1);
		}

		bi = new BeanItem<BaseDto>( newInterruptor );

		editOrder = new TextField( bi.getItemProperty( "list_order" ) );
		editOrder.setWidth( "100%" );
		editOrder.setNullRepresentation( "" );
		editOrder.setRequired( true );
		editOrder.setRequiredError( getContext().getString( "words.required" ) );

		editName = new TextField( bi.getItemProperty( "name" ) );
		editName.setWidth( "100%" );
		editName.setNullRepresentation( "" );
		editName.setRequired( true );
		editName.setRequiredError( getContext().getString( "words.required" ) );

		editDescription = new TextField( bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		editDescription.setRequired( true );
		editDescription.setRequiredError( getContext().getString( "words.required" ) );
		
		editAddress = new TextField( bi.getItemProperty( "address" ) );
		editAddress.setWidth( "100%" );
		editAddress.setNullRepresentation( "" );
		editAddress.setRequired( true );

		editPower = new TextField( bi.getItemProperty( "power" ) );
		editPower.setWidth( "100%" );
		editPower.setNullRepresentation( "" );
		editPower.setRequired( true );
		editPower.setRequiredError( getContext().getString( "words.required" ) );

		comboEnabled = new ComboBox();
		comboEnabled.setWidth( "100%" );
		comboEnabled.setNullSelectionAllowed( false );
		comboEnabled.setTextInputAllowed( false );
		comboEnabled.setImmediate( true );
		comboEnabled.setPropertyDataSource( bi.getItemProperty( "enabled" ) );
		comboEnabled.setReadOnly( false );
		fillComboEnabled();

		int tabIndex = 1;
		
		editOrder.setTabIndex( tabIndex++ );
		HorizontalLayout rowOrder = AppUtils.rowComponent( getContext(), editOrder, "modalNewInterruptor.order", null );
		
		editName.setTabIndex( tabIndex++ );
		HorizontalLayout rowName = AppUtils.rowComponent( getContext(), editName, "modalNewInterruptor.name", null );
		
		editDescription.setTabIndex( tabIndex++ );
		HorizontalLayout rowDescription = AppUtils.rowComponent( getContext(), editDescription, "modalNewInterruptor.description", null );

		editAddress.setTabIndex( tabIndex++ );
		HorizontalLayout rowAddress = AppUtils.rowComponent( getContext(), editAddress, "modalNewInterruptor.address", null );
		
		editPower.setTabIndex( tabIndex++ );
		HorizontalLayout rowPower = AppUtils.rowComponent( getContext(), editPower, "modalNewInterruptor.power", null );
		
		comboEnabled.setTabIndex( tabIndex++ );
		HorizontalLayout rowEnabled = AppUtils.rowComponent( getContext(), comboEnabled, "modalNewInterruptor.enabled", null );

		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( rowOrder );
		row1.addComponent( rowName );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( rowDescription );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( rowAddress );
		row3.addComponent( rowPower );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( rowEnabled );
		row4.addComponent( new HorizontalLayout() );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		componentsContainer.addComponent( row4 );
		/*componentsContainer.setComponentAlignment( top, Alignment.TOP_RIGHT );
		componentsContainer.addComponent( editContainer );*/
		
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewInterruptor";
	}

	@Override
	protected void defaultFocus()
	{
		editAddress.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newInterruptor.setId( null );

			IOCManager._InterruptorsManager.setRow( getContext(), null, newInterruptor );

			FacilityInterruptor giInterruptor = new FacilityInterruptor();
			giInterruptor.setRef_facility( ((ModalNewFacility)getModalParent()).getNewFacility().getId() );
			giInterruptor.setRef_interruptor( newInterruptor.getId() );

			IOCManager._FacilityInterruptorsManager.setRow( getContext(), null, giInterruptor );

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
			IOCManager._InterruptorsManager.setRow( getContext(), (Interruptor) orgDto, newInterruptor );
	
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
			IOCManager._InterruptorsManager.delRow( getContext(), newInterruptor );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private void fillComboEnabled()
	{
		comboEnabled.addItem( 0 );
		comboEnabled.setItemCaption( 0, getContext().getString( "words.no" ) );

		comboEnabled.addItem( 1 );
		comboEnabled.setItemCaption( 1, getContext().getString( "words.yes" ) );
		
		comboEnabled.select( newInterruptor.getEnabled() != 0 ? 1 : 0  );
	}
}
