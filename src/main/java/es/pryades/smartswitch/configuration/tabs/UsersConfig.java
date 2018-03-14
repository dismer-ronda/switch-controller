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
import es.pryades.smartswitch.configuration.modals.ModalNewUser;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;
import es.pryades.smartswitch.dto.User;
import es.pryades.smartswitch.dto.query.UserQuery;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.vto.UserVto;
import es.pryades.smartswitch.vto.controlers.UserControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class UsersConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = 4942358294790571817L;

	public UsersConfig( AppContext ctx )
	{
		super( ctx );

		setOrderby( "name" );
		setOrder( "asc" );
	}

	@Override
	public String getResourceKey()
	{
		return "usersConfig";
	}

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{"name", "login", "email", "profile_name", "tester" };
	}

	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( UserVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	@Override
	public Component getQueryComponent()
	{
		return null;
	}
	
	@Override
	public Query getQueryObject()
	{
		return new UserQuery();
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewUser( getContext(), Operation.OP_ADD, null, UsersConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewUser( getContext(), Operation.OP_MODIFY, (User)dto, UsersConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationDelete( BaseDto dto )
	{
		new ModalNewUser( getContext(), Operation.OP_DELETE, (User)dto, UsersConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new UserControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new User();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._UsersManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		int i = 0;
		int count = rows.size();
		
		while ( i < count )
		{
			User user = (User)rows.get( i );
			
			if ( getContext().getUser().getRef_profile() > user.getRef_profile() )
			{
				rows.remove( i );
				count--;
			}
			else 
				i++;
		}
	}

	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.users.add" );
	}

	@Override
	public boolean hasModifyRight()
	{
		return getContext().hasRight( "configuration.users.modify" );
	}

	@Override
	public boolean hasDeleteRight()
	{
		return getContext().hasRight( "configuration.users.delete" );
	}

	@Override
	public boolean hasDeleteAllRight()
	{
		return getContext().hasRight( "configuration.users.deleteAll" );
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
