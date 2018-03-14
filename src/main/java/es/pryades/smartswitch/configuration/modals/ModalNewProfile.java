package es.pryades.smartswitch.configuration.modals;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.apache.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.DialogLabel;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.ModalWindowsCRUD;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Profile;
import es.pryades.smartswitch.dto.ProfileRight;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

public class ModalNewProfile extends ModalWindowsCRUD implements ModalParent
{
	private static final long serialVersionUID = -5487483550129077794L;

	private static final Logger LOG = Logger.getLogger( ModalNewProfile.class );

	@Getter
	protected Profile newProfile;

	private TextField editDescription;

	private ListSelect listProfileRights;
	
	@Getter
	private List<ProfileRight> profileRights;

	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewProfile( AppContext context, Operation modalOperation, Profile orgProfile, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgProfile );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newProfile = (Profile) Utils.clone( (Profile) orgDto );
		}
		catch ( Throwable e1 )
		{
			newProfile = new Profile();
		}

		bi = new BeanItem<BaseDto>( newProfile );

		loadProfileRights();
		
		editDescription = new TextField( bi.getItemProperty( "description" ) );
		editDescription.setWidth( "100%" );
		editDescription.setNullRepresentation( "" );
		editDescription.setRequired( true );
		editDescription.setRequiredError( getContext().getString( "words.required" ) );

		listProfileRights = new ListSelect();
		listProfileRights.setWidth( "100%" );
		listProfileRights.setImmediate( true );
		listProfileRights.setNullSelectionAllowed( false );
		fillProfileRights();

		VerticalLayout colProfileRights = new VerticalLayout();
		colProfileRights.setSpacing( true );
		colProfileRights.setMargin( new MarginInfo( false, false, false, true ) );
		colProfileRights.setWidth( "-1px" );
		
		Button button = new Button( getContext().getString( "words.add" ) );
		button.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		button.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -4109894702851130397L;

			public void buttonClick( ClickEvent event )
			{
				onAddProfileRight();
			}
		} );
		colProfileRights.addComponent( button );
		colProfileRights.setComponentAlignment( button, Alignment.MIDDLE_CENTER );

		button = new Button( getContext().getString( "words.delete" ) );
		button.setWidth( Constants.DEFAULT_BUTTON_WIDTH );
		button.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 2036341973726835707L;

			public void buttonClick( ClickEvent event )
			{
				onDeleteCylinderDevice();
			}
		} );
		colProfileRights.addComponent( button );
		colProfileRights.setComponentAlignment( button, Alignment.MIDDLE_CENTER );

		HorizontalLayout rowDescription = new HorizontalLayout();
		rowDescription.setWidth( "100%" );
		rowDescription.addComponent( new DialogLabel( getContext().getString( "modalNewProfile.description" ), "120px" ) );
		rowDescription.addComponent( editDescription );
		rowDescription.setExpandRatio( editDescription, 1.0f );

		HorizontalLayout rowProfileRights = new HorizontalLayout();
		rowProfileRights.setWidth( "100%" );
		rowProfileRights.addComponent( new DialogLabel( getContext().getString( "modalNewProfile.rights" ), "120px" ) );
		rowProfileRights.addComponent( listProfileRights );
		rowProfileRights.addComponent( colProfileRights );
		rowProfileRights.setExpandRatio( listProfileRights, 1.0f );
		
		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( rowDescription );

		componentsContainer.addComponent( row2 );
		
		if ( !getOperation().equals( Operation.OP_ADD ) )
			componentsContainer.addComponent( rowProfileRights );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewProfile";
	}

	@Override
	protected void defaultFocus()
	{
		editDescription.focus();
	}

	@Override
	protected boolean onAdd()
	{
		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			IOCManager._ProfilesManager.setRow( getContext(), (Profile) orgDto, newProfile );
	
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
		return false;
	}

	private void fillProfileRights()
	{
		listProfileRights.removeAllItems();
		
		try
		{
			for ( ProfileRight profileRight : profileRights )
			{
				listProfileRights.addItem( profileRight.getRef_right() );
				
				listProfileRights.setItemCaption( profileRight.getRef_right(), profileRight.getRight_description() );
			}
		}
		catch ( Throwable e )
		{
			if ( !( e instanceof BaseException ) )
				new BaseException( e, LOG, BaseException.UNKNOWN );
		}
	}

	private ProfileRight getSelectedProfileRight()
	{
		for ( ProfileRight profileRight : profileRights )
			if ( profileRight.getRef_right().equals( listProfileRights.getValue() ) )
				return profileRight;
		
		return null;
	}
	
	private void onAddProfileRight()
	{
		new ModalNewProfileRight( getContext(), Operation.OP_ADD, null, ModalNewProfile.this ).showModalWindow();
	}

	private void onDeleteCylinderDevice()
	{
		final ProfileRight profileRight = getSelectedProfileRight();
		
		ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( "modalNewProfile.confirm.delete.right" ),
        new ConfirmDialog.Listener() 
		{
			private static final long serialVersionUID = -5050987453454401043L;

			public void onClose(ConfirmDialog dialog) 
            {
                if (dialog.isConfirmed()) 
                {
                	try
					{
                		IOCManager._ProfilesRightsManager.delRow( getContext(), profileRight );
						
						refreshVisibleContent( false );
					}
					catch ( Throwable e )
					{
						Utils.logException( e, LOG );
					}
                } 
            }
        });
	}

	@SuppressWarnings("unchecked")
	private void loadProfileRights()
	{
		if ( newProfile.getId() != null )
		{
			try
			{
				ProfileRight query = new ProfileRight();
				query.setRef_profile( newProfile.getId() );
	
				profileRights = IOCManager._ProfilesRightsManager.getRows( getContext(), query );
				
				return;
			}
			catch ( Throwable e )
			{
				
			}
		}
		
		profileRights = new ArrayList<ProfileRight>();
	}

	@Override
	public void refreshVisibleContent( boolean repage )
	{
		loadProfileRights();
		fillProfileRights();
	}
}
