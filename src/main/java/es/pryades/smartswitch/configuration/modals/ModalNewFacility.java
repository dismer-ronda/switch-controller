package es.pryades.smartswitch.configuration.modals;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.AppUtils;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.ModalWindowsCRUD;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Facility;
import es.pryades.smartswitch.dto.FacilityInterruptor;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewFacility extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = 3469252701667823616L;

	private static final Logger LOG = Logger.getLogger( ModalNewFacility.class );

	@Getter
	protected Facility newFacility;

	private TextField editOrder;
	private TextField editName;
	private TextField editDescription;
	private TextField editPower;
	private ComboBox comboEnabled;

	private ListSelect listInterruptors;
	
	private Button bttnAddInterruptor;
	private Button bttnModifyInterruptor;
	private Button bttnDeleteInterruptor;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewFacility( AppContext context, Operation modalOperation, Facility orgUser, ModalParent parentWindow )
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
			newFacility = (Facility) Utils.clone( (Facility) orgDto );
			
			reloadFacility();
		}
		catch ( Throwable e1 )
		{
			newFacility = new Facility();
			
			newFacility.setEnabled(1);
		}

		bi = new BeanItem<BaseDto>( newFacility );

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

		listInterruptors = new ListSelect();
		listInterruptors.setWidth( "100%" );
		listInterruptors.setHeight( "100%" );
		listInterruptors.setImmediate( true );
		listInterruptors.setNullSelectionAllowed( false );
		listInterruptors.addValueChangeListener( new Property.ValueChangeListener() 
		{
			private static final long serialVersionUID = -6888228667270652719L;

			@Override
	        public void valueChange( ValueChangeEvent event ) 
	        {
	            Object value = listInterruptors.getValue();
	            
            	bttnModifyInterruptor.setEnabled( value != null );
            	bttnDeleteInterruptor.setEnabled( value != null );
	        }
	    });
		fillInterruptors();
		
		VerticalLayout colStationsModems = new VerticalLayout();
		colStationsModems.setSpacing( true );
		colStationsModems.setWidth( "-1px" );
		colStationsModems.setHeight( "-1px" );
		
		if ( !getOperation().equals( Operation.OP_VIEW ) )
		{		
			bttnAddInterruptor = new Button( getContext().getString( "modalNewInterruptor.operation.add" ) );
			bttnAddInterruptor.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
			bttnAddInterruptor.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -7611550961986694084L;

				public void buttonClick( ClickEvent event )
				{
					onAddInterruptor();
				}
			} );
			colStationsModems.addComponent( bttnAddInterruptor );
			colStationsModems.setComponentAlignment( bttnAddInterruptor, Alignment.MIDDLE_CENTER );
	
			bttnModifyInterruptor = new Button( getContext().getString( "modalNewInterruptor.operation.modify" ) );
			bttnModifyInterruptor.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
			bttnModifyInterruptor.setEnabled( false );
			bttnModifyInterruptor.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 8435530029907876332L;

				public void buttonClick( ClickEvent event )
				{
					onModifyInterruptor();
				}
			} );
			colStationsModems.addComponent( bttnModifyInterruptor );
			colStationsModems.setComponentAlignment( bttnModifyInterruptor, Alignment.MIDDLE_CENTER );

			bttnDeleteInterruptor = new Button( getContext().getString( "modalNewInterruptor.operation.delete" ) );
			bttnDeleteInterruptor.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
			bttnDeleteInterruptor.setEnabled( false );
			bttnDeleteInterruptor.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = -3509822961613111566L;

				public void buttonClick( ClickEvent event )
				{
					onDeleteInterruptor();
				}
			} );
			colStationsModems.addComponent( bttnDeleteInterruptor );
			colStationsModems.setComponentAlignment( bttnDeleteInterruptor, Alignment.MIDDLE_CENTER );
		}
		
		int tabIndex = 1;
		
		editOrder.setTabIndex( tabIndex++ );
		HorizontalLayout rowOrder = AppUtils.rowComponent( getContext(), editOrder, "modalNewFacility.order", null );
		
		editName.setTabIndex( tabIndex++ );
		HorizontalLayout rowName = AppUtils.rowComponent( getContext(), editName, "modalNewFacility.name", null );
		
		editDescription.setTabIndex( tabIndex++ );
		HorizontalLayout rowDescription = AppUtils.rowComponent( getContext(), editDescription, "modalNewFacility.description", null );

		editPower.setTabIndex( tabIndex++ );
		HorizontalLayout rowPower = AppUtils.rowComponent( getContext(), editPower, "modalNewFacility.power", null );
		
		comboEnabled.setTabIndex( tabIndex++ );
		HorizontalLayout rowEnabled = AppUtils.rowComponent( getContext(), comboEnabled, "modalNewFacility.enabled", null );

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
		row3.addComponent( rowPower );
		row3.addComponent( rowEnabled );
		
		HorizontalLayout row4 = new HorizontalLayout(listInterruptors, colStationsModems);
		row4.setSpacing( true );
		row4.setWidth( "100%" );
		row4.setExpandRatio( listInterruptors, 1.0f );

		componentsContainer.addComponent( row1 );
		componentsContainer.addComponent( row2 );
		componentsContainer.addComponent( row3 );
		
		if ( !getOperation().equals( Operation.OP_ADD ) )
			componentsContainer.addComponent( row4 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewFacility";
	}

	@Override
	protected void defaultFocus()
	{
		editOrder.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newFacility.setId( null );

			IOCManager._FacilitiesManager.setRow( getContext(), null, newFacility );

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
			IOCManager._FacilitiesManager.setRow( getContext(), (Facility) orgDto, newFacility );
	
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
			IOCManager._FacilitiesManager.delRow( getContext(), newFacility );

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
		
		comboEnabled.select( newFacility.getEnabled() != 0 ? 1 : 0  );
	}
	
	private void fillInterruptors()
	{
		listInterruptors.removeAllItems();
		
		try
		{
			for ( FacilityInterruptor gInterruptorInterruptor : newFacility.getInterruptors() )
			{
				listInterruptors.addItem( gInterruptorInterruptor.getInterruptor().getId() );
				listInterruptors.setItemCaption( gInterruptorInterruptor.getInterruptor().getId(), gInterruptorInterruptor.getInterruptor().getName() );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	private FacilityInterruptor getSelectedFacilityInterruptor()
	{
		for ( FacilityInterruptor gInterruptor : newFacility.getInterruptors() )
		{
			if ( gInterruptor.getInterruptor().getId().equals( listInterruptors.getValue() ) )
				return gInterruptor;
		}
		
		return null;
	}

	private void onAddInterruptor()
	{
		new ModalNewInterruptor( getContext(), Operation.OP_ADD, null, ModalNewFacility.this ).showModalWindow();
	}

	private void onModifyInterruptor()
	{
		FacilityInterruptor gInterruptor = getSelectedFacilityInterruptor();
		
		if ( gInterruptor != null )
			new ModalNewInterruptor( getContext(), Operation.OP_MODIFY, gInterruptor.getInterruptor(), ModalNewFacility.this ).showModalWindow();
	}

	private void onDeleteInterruptor()
	{
		FacilityInterruptor gInterruptor = getSelectedFacilityInterruptor();
		
		if ( gInterruptor != null )
			new ModalNewInterruptor( getContext(), Operation.OP_DELETE, gInterruptor.getInterruptor(), ModalNewFacility.this ).showModalWindow();
	}

	private void reloadFacility()
	{
		try
		{
			Facility query = new Facility();
			query.setId( newFacility.getId() );
			
			Facility temp = (Facility)IOCManager._FacilitiesManager.getRow( getContext(), query );
			
			newFacility.setInterruptors( temp.getInterruptors() );
			newFacility.setHolidays( temp.getHolidays() );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	@Override
	public void refreshVisibleContent( boolean repage )
	{
		reloadFacility();
		
		fillInterruptors();
	}

	@Override
	protected boolean editAfterNew()
	{
		return true;
	}

	@Override
	protected void doEditAfterNew()
	{
		new ModalNewFacility( getContext(), Operation.OP_MODIFY, (Facility)newFacility, getModalParent() ).showModalWindow();
	}
}