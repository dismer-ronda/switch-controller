package es.pryades.smartswitch.configuration.tabs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.BaseTable;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.GenericControlerVto;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.PagedContent;
import es.pryades.smartswitch.common.PagedTable;
import es.pryades.smartswitch.common.TaskAction;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.common.ModalWindowsCRUD.Operation;
import es.pryades.smartswitch.configuration.modals.ModalNewTask;
import es.pryades.smartswitch.dal.BaseManager;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;
import es.pryades.smartswitch.dto.Task;
import es.pryades.smartswitch.dto.query.TaskQuery;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.processors.SignalsProcessor;
import es.pryades.smartswitch.vto.TaskVto;
import es.pryades.smartswitch.vto.controlers.TaskControlerVto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public class TasksConfig extends PagedContent implements ModalParent
{
	private static final long serialVersionUID = -5428522530136369407L;
	private static final Logger LOG = Logger.getLogger( TasksConfig.class );

	private ComboBox comboClazz;
	
	private Button bttnApply;
	private Button btnDispatch;
	
	public TasksConfig( AppContext ctx )
	{
		super( ctx );
		
		setOrderby( "clazz" );
		setOrder( "desc" );
	}

	@Override
	public String getResourceKey()
	{
		return "tasksConfig";
	}

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }

	@Override
	public String[] getVisibleCols()
	{
		return new String[]{ "month", "day", "hour", "clazz", "description" };
	}

	public String[] getSortableCols()
	{
		return new String[]{ "clazz", "description" };
	}
	
	@Override
	public BaseTable createTable() throws BaseException
	{
		return new PagedTable( TaskVto.class, this, getContext(), getContext().getIntegerParameter( Parameter.PAR_DEFAULT_PAGE_SIZE ) );
	}

	public List<Component> getCustomOperations()
	{
		List<Component> ops = super.getCustomOperations();
		
		btnDispatch = new Button();
		btnDispatch.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		btnDispatch.setCaption( getContext().getString( "tasksConfig.dispatch" ) );
		btnDispatch.setEnabled( false );
		btnDispatch.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 982407465902279459L;

			public void buttonClick( ClickEvent event )
			{
				Long rowId = (Long)getTable().getTable().getValue();

				if ( rowId != null )
					onDispatchTask( rowId );
			}
		} );
		
		ops.add( btnDispatch );
		
		return ops;
	}

	@Override
	public Component getQueryComponent()
	{
		comboClazz = new ComboBox( getContext().getString( "tasksConfig.clazz" ) );
		comboClazz.setNullSelectionAllowed( true );
		comboClazz.setTextInputAllowed( false );
		comboClazz.setImmediate( true );
		fillComboClazzes();
		
		bttnApply = new Button();
		bttnApply.setStyleName( "borderless" );
		bttnApply.setDescription( getContext().getString( "words.apply" ) );
		bttnApply.setIcon( new ThemeResource( "images/accept.png" ) );
		addButtonApplyFilterClickListener();

		HorizontalLayout rowQuery = new HorizontalLayout();
		rowQuery.setSpacing( true );
		rowQuery.addComponent( comboClazz );
		rowQuery.addComponent( bttnApply );
		rowQuery.setComponentAlignment( bttnApply, Alignment.BOTTOM_LEFT );
		
		return rowQuery;
	}
	
	@Override
	public Query getQueryObject()
	{
		TaskQuery query = new TaskQuery();
		
		query.setClazz( (Integer)comboClazz.getValue() );
		query.setSystem( 0 );
		
		return query;
	}

	@Override
	public void onOperationNew()
	{
		new ModalNewTask( getContext(), Operation.OP_ADD, null, TasksConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationModify( BaseDto dto )
	{
		new ModalNewTask( getContext(), Operation.OP_MODIFY, (Task)dto, TasksConfig.this ).showModalWindow();
	}

	@Override
	public void onOperationDelete( BaseDto dto )
	{
		new ModalNewTask( getContext(), Operation.OP_DELETE, (Task)dto, TasksConfig.this ).showModalWindow();
	}

	@Override
	public GenericControlerVto getControlerVto( AppContext ctx )
	{
		return new TaskControlerVto(ctx);
	}

	@Override
	public BaseDto getFieldDto() 
	{ 
		return new Task();
	}
	
	@Override
	public BaseManager getFieldManagerImp() 
	{
		return IOCManager._TasksManager;
	}

	@Override
	public void preProcessRows( List<BaseDto> rows )
	{
		List<BaseDto> invalid = new ArrayList<BaseDto>();
		
		for ( BaseDto row : rows )
		{
			TaskAction action = IOCManager._TasksManager.getTaskAction( (Task)row );
			
			if ( !action.isUserEnabledForTask( context ) )
				invalid.add( row );
		}
		
		rows.removeAll( invalid );
	}
	
	@Override
	public boolean hasAddRight()
	{
		return getContext().hasRight( "configuration.tasks.add" );
	}

	@Override
	public boolean hasModifyRight()
	{
		return getContext().hasRight( "configuration.tasks.modify" );
	}

	@Override
	public boolean hasDeleteRight()
	{
		return getContext().hasRight( "configuration.tasks.delete" );
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

	public void addButtonApplyFilterClickListener()
	{
		bttnApply.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7406758301827706990L;

			@Override
			public void buttonClick( ClickEvent event )
			{
				refreshVisibleContent( true );
			}
		} );
	}

	private void fillComboClazzes()
	{
		for ( int i = Constants.TASK_FIRST; i < Constants.TASK_FIRST + Constants.TASK_CLAZZES; i++ )
		{
			comboClazz.addItem( i );
			comboClazz.setItemCaption( i, getContext().getString( "task.clazz." + i ) );
		}
	}

	public void onDispatchTask( Long id )
	{
		try
		{
			Task task = (Task)getTable().getRowValue( id );
			
			SignalsProcessor.dispatchSignal( task );
			
			Utils.showNotification( getContext(), getContext().getString( "tasksConfig.dispatched" ), Notification.Type.TRAY_NOTIFICATION );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	public void onSelectedRow()
	{
		super.onSelectedRow();
		
		btnDispatch.setEnabled( getTable().getTable().getValue() != null );
	}
}

