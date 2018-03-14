package es.pryades.smartswitch.common;

import lombok.Getter;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import es.pryades.smartswitch.dto.BaseDto;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public abstract class ModalWindowsCRUD extends Window
{
	private static final long serialVersionUID = -6161493988046489729L;
	
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( ModalWindowsCRUD.class );

	@Getter protected AppContext context;

	protected VerticalLayout componentsContainer;
	protected HorizontalLayout operacionesContainer;

	protected Button bttnOperacion;
	protected Button bttnCancelar;

	@Getter protected ModalParent modalParent;

	@Getter protected Operation operation;

	protected VerticalLayout layout;

	protected BaseDto orgDto;
	protected BeanItem<BaseDto> bi;

	public static enum Operation
	{
		OP_ADD("add"), OP_MODIFY("modify"), OP_DELETE("delete"), OP_VIEW("view");

		Operation( String op )
		{
			opName = op;
		}

		private String opName = "";

		public String getOpName()
		{
			return opName;
		}
	}

	/**
     * 
     */
	public ModalWindowsCRUD( AppContext context, ModalParent modalParent, Operation modalOperation, BaseDto orgDto )
	{
		super();

		this.context = context;
		this.modalParent = modalParent;
		this.operation = modalOperation;
		this.orgDto = orgDto;

		setStyleName( "enermetModalfieldSet" );
		//setCloseShortcut( KeyCode.ESCAPE );
		center();

		setWidth( "700px" );
		setHeight( "-1px" );

		setModal( true );
		setResizable( false );
		setClosable( true );

		initComponents();

		switch ( modalOperation )
		{
			case OP_ADD:
				bttnAddListener();
				break;
			case OP_MODIFY:
				bttnModifyListener();
				break;
			case OP_DELETE:
				bttnDeleteListener();
				bttnCancelar.focus();
				break;
			default:
				break;
		}

		bttnCancelarListener();

		if ( !modalOperation.equals( Operation.OP_DELETE ) )
			defaultFocus();

		setCaption( getContext().getString( getWindowResourceKey() + ".wndCaption." + modalOperation.getOpName() ) );
	}

	public void initComponents()
	{
		setContent( layout = new VerticalLayout() );
		
		layout.setSizeUndefined();
		layout.setWidth( "100%" );
		layout.setMargin( true );
		layout.setSpacing( true );

		componentsContainer = new VerticalLayout();
		componentsContainer.setMargin( false );
		componentsContainer.setSpacing( true );

		operacionesContainer = new HorizontalLayout();
		operacionesContainer.setSpacing( true );

		if ( !operation.equals( Operation.OP_VIEW ) )
		{
			bttnOperacion = new Button( getContext().getString( getWindowResourceKey() + ".operation." + operation.getOpName() ) );
			bttnOperacion.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		}
		
		bttnCancelar = new Button( getContext().getString( "words.cancel" ) );
		bttnCancelar.setWidth( Constants.DEFAULT_BUTTON_WIDTH );

		//bttnOperacion.setClickShortcut( KeyCode.ENTER );
		bttnCancelar.focus();

		if ( !operation.equals( Operation.OP_VIEW ) )
			operacionesContainer.addComponent( bttnOperacion );
		
		operacionesContainer.addComponent( bttnCancelar );
		if ( !operation.equals( Operation.OP_VIEW ) )
			operacionesContainer.setComponentAlignment( bttnOperacion, Alignment.BOTTOM_RIGHT );
		operacionesContainer.setComponentAlignment( bttnCancelar, Alignment.BOTTOM_RIGHT );

		layout.addComponent( componentsContainer );
		layout.addComponent( operacionesContainer );
		layout.setComponentAlignment( operacionesContainer, Alignment.BOTTOM_RIGHT );
		layout.setExpandRatio( operacionesContainer, 1.0f );
	}

	protected abstract boolean onAdd();
	protected abstract boolean onModify();
	protected abstract boolean onDelete();

	protected abstract void defaultFocus();

	protected boolean editAfterNew() 			{ return false; }
	protected void doEditAfterNew() 			{}

	protected String getWindowResourceKey()
	{
		return this.getClass().getSimpleName();
	}

	protected void onClose()
	{
		closeModalWindow( false, false );		
	}

	public void showModalWindow()
	{
		((UI)getContext().getData( "Application" )).addWindow( this );
	}

	public void closeModalWindow( boolean refresh, boolean repage )
	{
		((UI)getContext().getData( "Application" )).removeWindow( this );

		if ( modalParent != null && refresh )
			modalParent.refreshVisibleContent( repage );
	}

	private void bttnCancelarListener()
	{
		bttnCancelar.addClickListener( new Button.ClickListener()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 7591024099361245222L;

			public void buttonClick( ClickEvent event )
			{
				onClose();
			}
		} );
	}

	private void bttnAddListener()
	{
		bttnOperacion.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 5858612612327120717L;

			public void buttonClick( ClickEvent event )
			{
				if ( onAdd() )
				{
					closeModalWindow( true, true );
					
					if ( editAfterNew() )
						doEditAfterNew();
				}
			}
		} );
	}

	private void bttnModifyListener()
	{
		bttnOperacion.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 7587457903111881354L;

			public void buttonClick( ClickEvent event )
			{
				if ( onModify() )
					closeModalWindow( true, false );
			}
		} );
	}

	private void bttnDeleteListener()
	{
		bttnOperacion.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 581142912127960955L;

			public void buttonClick( ClickEvent event )
			{
				ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( getWindowResourceKey() + ".confirm.delete" ),
		        new ConfirmDialog.Listener() 
				{
					private static final long serialVersionUID = -3142429497962370163L;

					public void onClose(ConfirmDialog dialog) 
		            {
		                if ( dialog.isConfirmed() ) 
		                {
							if ( onDelete() )
								closeModalWindow( true, true );
		                } 
		            }
		        });
			}
		} );
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
}
