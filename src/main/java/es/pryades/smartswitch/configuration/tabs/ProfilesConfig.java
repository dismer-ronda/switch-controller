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
import es.pryades.smartswitch.configuration.modals.ModalNewProfile;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Profile;
import es.pryades.smartswitch.dto.Query;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.vto.ProfileVto;
import es.pryades.smartswitch.vto.controlers.ProfileControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class ProfilesConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -5428522530136369407L;

	public ProfilesConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "id" );
		setOrder( "asc" );
	}

	@Override
	public String getResourceKey()
	{
		return "profilesConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "id", "description" };
	}

	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( ProfileVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}
	
	@Override
	public Query getQueryObject()
	{
		return new Profile();
	}

	@Override
	public void onOperationNew()
	{
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewProfile( getContext(), Operation.OP_MODIFY, (Profile)dto, ProfilesConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationDelete( BaseDto dto )
	{
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new ProfileControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Profile();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._ProfilesManager;
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
		return getContext().hasRight( "configuration.profiles.modify" );
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
