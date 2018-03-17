package es.pryades.smartswitch.configuration.tabs;

import java.util.List;

import com.vaadin.ui.Component;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.BaseTable;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.ModalWindowsCRUD.Operation;
import es.pryades.smartswitch.common.PagedContent;
import es.pryades.smartswitch.common.PagedTable;
import es.pryades.smartswitch.configuration.modals.ModalNewHoliday;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Holiday;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.vto.HolidayVto;
import es.pryades.smartswitch.vto.controlers.HolidayControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class HolidaysConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -7581436027513515466L;
	
	public HolidaysConfig( AppContext ctx )
	{
		super( ctx );
	}

	@Override
	public String getResourceKey()
	{
		return "holidaysConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "holiday_name", "holiday_type", "holiday_value" };
	}

	public String[] getSortableCols()
	{
		return null;
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( HolidayVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Query getQueryObject()
	{
		return new Holiday();
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewHoliday( getContext(), Operation.OP_ADD, null, HolidaysConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewHoliday( getContext(), Operation.OP_MODIFY, (Holiday)dto, HolidaysConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationDelete( BaseDto dto )
	{
		new ModalNewHoliday( getContext(), Operation.OP_DELETE, (Holiday)dto, HolidaysConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new HolidayControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Holiday();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._HolidaysManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
	}

	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.holidays.add" );
	}

	@Override
	public boolean hasModifyRight()
	{
		return getContext().hasRight( "configuration.holidays.modify" );
	}

	@Override
	public boolean hasDeleteRight()
	{
		return getContext().hasRight( "configuration.holidays.delete" );
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
		return null;
	}
}
