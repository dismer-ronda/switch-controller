package es.pryades.smartswitch.configuration.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.BaseTable;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.ModalWindowsCRUD.Operation;
import es.pryades.smartswitch.common.PagedContent;
import es.pryades.smartswitch.common.PagedTable;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.configuration.modals.ModalNewFacility;
import es.pryades.smartswitch.configuration.modals.SetFacilityHolidaysDlg;
import es.pryades.smartswitch.configuration.modals.SetInterruptorsPlanDlg;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Facility;
import es.pryades.smartswitch.dto.FacilityInterruptor;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.vto.FacilityVto;
import es.pryades.smartswitch.vto.controlers.FacilityControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class FacilitiesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 8089533562917005805L;

	private static final Logger LOG = Logger.getLogger( FacilitiesConfig.class );

	private Button bttnPlanLabor;
	private Button bttnPlanFree;
	private Button bttnHolidays;
	
	public FacilitiesConfig( AppContext ctx )
	{
		super( ctx );
	}

	@Override
	public String getResourceKey()
	{
		return "facilitiesConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "list_order", "name", "description", "power", "enabled" };
	}

	public String[] getSortableCols()
	{
		return null;
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( FacilityVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Query getQueryObject()
	{
		return new Facility();
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewFacility( getContext(), Operation.OP_ADD, null, FacilitiesConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewFacility( getContext(), Operation.OP_MODIFY, (Facility)dto, FacilitiesConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationDelete( BaseDto dto )
	{
		new ModalNewFacility( getContext(), Operation.OP_DELETE, (Facility)dto, FacilitiesConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new FacilityControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Facility();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._FacilitiesManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}

	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.interruptors.add" );
	}

	@Override
	public boolean hasModifyRight()
	{
		return getContext().hasRight( "configuration.interruptors.modify" );
	}

	@Override
	public boolean hasDeleteRight()
	{
		return getContext().hasRight( "configuration.interruptors.delete" );
	}

	@Override
	public boolean hasDeleteAllRight()
	{
		return false;
	}

	@Override
	public void updateComponent()
	{
	}

	@Override
	public void onFieldEvent( Component component, String column )
	{
	}
	
	public List<Component> getCustomOperations()
	{
		List<Component> ops = super.getCustomOperations();
		
		bttnPlanLabor = new Button( getContext().getString( "facilitiesConfig.plan.labor" ) );
		bttnPlanLabor.setEnabled( false );
		bttnPlanLabor.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -1400538311186237425L;
	
			public void buttonClick( ClickEvent event )
			{
				Long rowId = (Long)getTable().getTable().getValue();

				if ( rowId != null )
					onSetPlan( rowId, true );
			}
		} );
	
		bttnPlanFree = new Button( getContext().getString( "facilitiesConfig.plan.free" ) );
		bttnPlanFree.setEnabled( false );
		bttnPlanFree.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 8435530029907876332L;
	
			public void buttonClick( ClickEvent event )
			{
				Long rowId = (Long)getTable().getTable().getValue();

				if ( rowId != null )
					onSetPlan( rowId, false );
			}
		} );

		bttnHolidays = new Button( getContext().getString( "facilitiesConfig.holidays" ) );
		bttnHolidays.setEnabled( false );
		bttnHolidays.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 5062404593595743332L;

			public void buttonClick( ClickEvent event )
			{
				Long rowId = (Long)getTable().getTable().getValue();
				
				if ( rowId != null)
					onHolidays( rowId );
			}
		} );
		
		ops.add( bttnPlanLabor );
		ops.add( bttnPlanFree );
		ops.add( bttnHolidays );
	
		return ops;
	}

	public void showErrorMessage( Throwable e )
	{
		String msg = "";

		if ( e instanceof BaseException )
		{
			msg = getContext().getString( "exception.code." + ((BaseException)e).getErrorCode() );

			Utils.showNotification( getContext(), msg, Notification.Type.ERROR_MESSAGE );
		}
		else
			Utils.showNotification( getContext(), getContext().getString( "error.unknown" ), Notification.Type.ERROR_MESSAGE );
	}
	
	private void onSetPlan( Long id, boolean labor )
	{
		try
		{
			Facility facility = (Facility)getTable().getRowValue( id );
			
			List<Interruptor> interruptors = new ArrayList<Interruptor>();
			for ( FacilityInterruptor gInterruptor : facility.getInterruptors() )
				interruptors.add( gInterruptor.getInterruptor() );
			
			SetInterruptorsPlanDlg dlg = new SetInterruptorsPlanDlg( labor, facility.getPower() );
			dlg.setContext( getContext() );
			dlg.setInterruptors( interruptors );
			dlg.createComponents();
			getUI().addWindow( dlg );
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

	}

	public void onSelectedRow()
	{
		super.onSelectedRow();

		boolean enabled = getTable().getTable().getValue() != null;
		
		bttnPlanLabor.setEnabled( enabled );
		bttnPlanFree.setEnabled( enabled );
		bttnHolidays.setEnabled( enabled );
	}

	protected void onHolidays( Long rowId )
	{
		try
		{
			Facility facility = (Facility)getTable().getRowValue( rowId );
		
			SetFacilityHolidaysDlg dlg = new SetFacilityHolidaysDlg( facility, FacilitiesConfig.this );
			dlg.setContext( getContext() );
			dlg.createComponents();
			getUI().addWindow( dlg );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
