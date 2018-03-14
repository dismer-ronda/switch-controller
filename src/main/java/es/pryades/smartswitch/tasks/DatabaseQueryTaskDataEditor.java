package es.pryades.smartswitch.tasks;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.DialogLabel;
import es.pryades.smartswitch.common.TaskActionDataEditor;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.User;
import es.pryades.smartswitch.dto.query.UserQuery;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.reports.CommonEditor;

public class DatabaseQueryTaskDataEditor extends CommonEditor implements TaskActionDataEditor 
{
	private static final long serialVersionUID = -9187149361325987049L;

	private static final Logger LOG = Logger.getLogger( DatabaseQueryTaskDataEditor.class );
	
	private TextField editDriver;
	private TextField editUrl;
	private TextField editUser;
	private PasswordField editPassword;
	private TextArea editSql;
	private ComboBox comboUsers;
	
	private DatabaseQueryTaskData data;
	
	public DatabaseQueryTaskDataEditor( AppContext ctx ) 
	{
		super( ctx );
	}
	
	@Override
	public Object getComponent( String details, boolean readOnly )
	{
		data = null;
		
		try
		{
			data = (DatabaseQueryTaskData) Utils.toPojo( details, DatabaseQueryTaskData.class, false );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
		
		if ( data == null )
		{
			data = new DatabaseQueryTaskData();
			
			data.setRef_user( getContext().getUser().getId() );
		}
		
		bi = new BeanItem<DatabaseQueryTaskData>( data );
		
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing( true );
		
		editDriver = new TextField( bi.getItemProperty( "driver" ) );
		editDriver.setWidth( "100%" );
		editDriver.setNullRepresentation( "" );
        
		editUrl = new TextField( bi.getItemProperty( "url" ) );
		editUrl.setWidth( "100%" );
		editUrl.setNullRepresentation( "" );
        
		editUser = new TextField( bi.getItemProperty( "user" ) );
		editUser.setWidth( "100%" );
		editUser.setNullRepresentation( "" );
        
		editPassword = new PasswordField( bi.getItemProperty( "password" ) );
		editPassword.setWidth( "100%" );
		editPassword.setNullRepresentation( "" );
        
		editSql = new TextArea( bi.getItemProperty( "sql" ) );
		editSql.setWidth( "100%" );
		editSql.setNullRepresentation( "" );
                
		comboUsers = new ComboBox();
		comboUsers.setWidth( "100%" );
		comboUsers.setNullSelectionAllowed( false );
		comboUsers.setTextInputAllowed( true );
		comboUsers.setImmediate( true );
		fillComboUsers();
		comboUsers.setPropertyDataSource( bi.getItemProperty( "ref_user" ) );
		
		HorizontalLayout rowDriver = new HorizontalLayout();
		rowDriver.setWidth( "100%" );
		rowDriver.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.driver" ), "120px" ) );
		rowDriver.addComponent( editDriver );
		rowDriver.setExpandRatio( editDriver, 1.0f );
			
		HorizontalLayout rowSpUrl = new HorizontalLayout();
		rowSpUrl.setWidth( "100%" );
		rowSpUrl.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.url" ), "120px" ) );
		rowSpUrl.addComponent( editUrl );
		rowSpUrl.setExpandRatio( editUrl, 1.0f );
			
		HorizontalLayout rowDbUser = new HorizontalLayout();
		rowDbUser.setWidth( "100%" );
		rowDbUser.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.dbuser" ), "120px" ) );
		rowDbUser.addComponent( editUser );
		rowDbUser.setExpandRatio( editUser, 1.0f );
			
		HorizontalLayout rowPassword = new HorizontalLayout();
		rowPassword.setWidth( "100%" );
		rowPassword.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.password" ), "120px" ) );
		rowPassword.addComponent( editPassword );
		rowPassword.setExpandRatio( editPassword, 1.0f );
			
		HorizontalLayout rowSql = new HorizontalLayout();
		rowSql.setWidth( "100%" );
		rowSql.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.sql" ), "120px" ) );
		rowSql.addComponent( editSql );
		rowSql.setExpandRatio( editSql, 1.0f );
			
		HorizontalLayout rowUser = new HorizontalLayout();
		rowUser.setWidth( "100%" );
		rowUser.addComponent( new DialogLabel( getContext().getString( "databaseQueryTaskDataEditor.user" ), "120px" ) );
		rowUser.addComponent( comboUsers );
		rowUser.setExpandRatio( comboUsers, 1.0f );
		
		HorizontalLayout row1 = new HorizontalLayout();
		row1.setWidth( "100%" );
		row1.setSpacing( true );
		row1.addComponent( rowDriver );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( rowSpUrl );

		HorizontalLayout row3 = new HorizontalLayout();
		row3.setWidth( "100%" );
		row3.setSpacing( true );
		row3.addComponent( rowDbUser );
		row3.addComponent( rowPassword );

		HorizontalLayout row4 = new HorizontalLayout();
		row4.setWidth( "100%" );
		row4.setSpacing( true );
		row4.addComponent( rowSql );

		HorizontalLayout row5 = new HorizontalLayout();
		row5.setWidth( "100%" );
		row5.setSpacing( true );
		row5.addComponent( rowUser );

		layout.addComponent( row1 );
		layout.addComponent( row2 );
		layout.addComponent( row3 );
		layout.addComponent( row4 );
		layout.addComponent( row5 );
		
		return layout;
	}

	@Override
	public String getTaskData() throws BaseException
	{
		return Utils.toJson( data );
	}

	@Override
	public String isValidInput()
	{
		if ( editDriver.isEmpty() )
			return getContext().getString( "databaseQueryTaskDataEditor.missing.driver" );
			
		if ( editUrl.isEmpty() )
			return getContext().getString( "databaseQueryTaskDataEditor.missing.url" );
			
		if ( editUser.isEmpty() )
			return getContext().getString( "databaseQueryTaskDataEditor.missing.dbuser" );
			
		if ( editPassword.isEmpty() )
			return getContext().getString( "databaseQueryTaskDataEditor.missing.password" );
			
		if ( editSql.isEmpty() )
			return getContext().getString( "databaseQueryTaskDataEditor.missing.sql" );
			
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void fillComboUsers()
	{
		comboUsers.removeAllItems();
		
		try
		{
			UserQuery query = new UserQuery();
			
			List<User> users = IOCManager._UsersManager.getRows( getContext(), query );

			for ( User user : users )
			{
				comboUsers.addItem( user.getId() );
				comboUsers.setItemCaption( user.getId(), user.getName() );
			}
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
}
