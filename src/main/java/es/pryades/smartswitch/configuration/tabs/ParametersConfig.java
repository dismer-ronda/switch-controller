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
import es.pryades.smartswitch.configuration.modals.ModalNewParameter;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.vto.ParameterVto;
import es.pryades.smartswitch.vto.controlers.ParameterControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ParametersConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -8161715876828351299L;

	public ParametersConfig( AppContext ctx )
	{
		super( ctx );
	}

	@Override
	public String getResourceKey()
	{
		return "parametersConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "description", "value" };
	}

	public String[] getSortableCols()
	{
		return null;
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( ParameterVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Query getQueryObject()
	{
		return new Parameter();
	}

	@Override
	public void onOperationNew()
	{
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewParameter( getContext(), Operation.OP_MODIFY, (Parameter)dto, ParametersConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationDelete( BaseDto dto )
	{
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new ParameterControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Parameter();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._ParametersManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}

	@Override
	public boolean hasAddRight()
	{
		return false;
	}

	@Override
	public boolean hasModifyRight()
	{
		return getContext().hasRight( "configuration.parameters.modify" );
	}

	@Override
	public boolean hasDeleteRight()
	{
		return false;
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
