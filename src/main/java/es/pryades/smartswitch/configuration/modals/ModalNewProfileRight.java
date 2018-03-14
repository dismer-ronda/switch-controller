package es.pryades.smartswitch.configuration.modals;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.ModalWindowsCRUD;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.ProfileRight;
import es.pryades.smartswitch.dto.Right;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */
public final class ModalNewProfileRight extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -5290772537886787995L;

	private static final Logger LOG = Logger.getLogger( ModalNewProfileRight.class );

	protected ProfileRight newProfileRight;

	private ComboBox comboRight;
	private List<Right> rights;
	
	/**
	 * 
	 * @param getContext()
	 * @param resource
	 * @param modalOperation
	 * @param orgDto
	 * @param parentWindow
	 */
	public ModalNewProfileRight( AppContext context, Operation modalOperation, ProfileRight orgDto, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgDto );

		setWidth( "350px" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		try
		{
			newProfileRight = (ProfileRight) Utils.clone( (ProfileRight) orgDto );
		}
		catch ( Throwable e1 )
		{
			newProfileRight = new ProfileRight();
		}

		bi = new BeanItem<BaseDto>( newProfileRight );

		comboRight = new ComboBox();
		comboRight.setWidth( "100%" );
		comboRight.setNullSelectionAllowed( false );
		comboRight.setTextInputAllowed( true );
		comboRight.setImmediate( true );
		comboRight.setPropertyDataSource( bi.getItemProperty( "ref_right" ) );
		fillComboRights();

		Label label = new Label( getContext().getString( "modalNewProfileRight.right" ) );
		label.setWidth( "-1px" );

		HorizontalLayout rowDevices = new HorizontalLayout();
		rowDevices.setWidth( "100%" );
		rowDevices.setSpacing( true );
		rowDevices.addComponent( label );
		rowDevices.addComponent( comboRight );
		rowDevices.setExpandRatio( comboRight, 1.0f );

		HorizontalLayout row2 = new HorizontalLayout();
		row2.setWidth( "100%" );
		row2.setSpacing( true );
		row2.addComponent( rowDevices );

		componentsContainer.addComponent( row2 );
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewProfileRight";
	}

	@Override
	protected void defaultFocus()
	{
		comboRight.focus();
	}

	protected boolean onAdd()
	{
		try
		{
			newProfileRight.setId( null );
			newProfileRight.setRef_profile( ((ModalNewProfile)getModalParent()).getNewProfile().getId() );
			
			IOCManager._ProfilesRightsManager.setRow( getContext(), null, newProfileRight );
			
			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	protected boolean onModify()
	{
		return false;
	}

	protected boolean onDelete()
	{
		return false;
	}
	
	private boolean hasRight( Right right )
	{
		List<ProfileRight> profileRights = ((ModalNewProfile)getModalParent()).getProfileRights();
		
		for ( ProfileRight profileRight : profileRights )
			if ( profileRight.getRef_right().equals( right.getId() ) )
				return true;
		
		return false;
	}

	@SuppressWarnings("unchecked")
	private void fillComboRights()
	{
		try
		{
			Right query = new Right();

			rights = IOCManager._RightsManager.getRows( getContext(), query );

			for ( Right right : rights )
			{
				if ( !hasRight( right ) )
				{
					comboRight.addItem( right.getId() );
					comboRight.setItemCaption( right.getId(), right.getDescription() );
					
					if ( right.getId().equals( newProfileRight.getRef_right() ) )
						comboRight.select( right.getId() );
				}
			}
		}
		catch ( Throwable e )
		{
			if ( !( e instanceof BaseException ) )
				new BaseException( e, LOG, BaseException.UNKNOWN );
		}
	}
}
