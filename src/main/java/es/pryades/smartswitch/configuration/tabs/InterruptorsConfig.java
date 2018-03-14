package es.pryades.smartswitch.configuration.tabs;

import java.util.List;

import com.vaadin.ui.Component;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.BaseTable;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.PagedContent;
import es.pryades.smartswitch.common.PagedTable;
import es.pryades.smartswitch.common.ModalWindowsCRUD.Operation;
import es.pryades.smartswitch.configuration.modals.ModalNewInterruptor;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Interruptor;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.vto.InterruptorVto;
import es.pryades.smartswitch.vto.controlers.InterruptorControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class InterruptorsConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 7423665264675283701L;

	public InterruptorsConfig( AppContext ctx )
	{
		super( ctx );
	}

	@Override
	public String getResourceKey()
	{
		return "interruptorsConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "list_order", "name", "description", "address", "power", "plan_labor", "plan_free", "enabled", "state" };
	}

	public String[] getSortableCols()
	{
		return null;
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( InterruptorVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Query getQueryObject()
	{
		return new Interruptor();
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewInterruptor( getContext(), Operation.OP_ADD, null, InterruptorsConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewInterruptor( getContext(), Operation.OP_MODIFY, (Interruptor)dto, InterruptorsConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationDelete( BaseDto dto )
	{
		new ModalNewInterruptor( getContext(), Operation.OP_DELETE, (Interruptor)dto, InterruptorsConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new InterruptorControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Interruptor();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._InterruptorsManager;
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
}
