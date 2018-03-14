package es.pryades.smartswitch.configuration.modals;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import es.pryades.smartswitch.application.ChangePasswordDlg;
import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.AppUtils;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.ModalParent;
import es.pryades.smartswitch.common.ModalWindowsCRUD;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Profile;
import es.pryades.smartswitch.dto.User;
import es.pryades.smartswitch.dto.UserDefault;
import es.pryades.smartswitch.ioc.IOCManager;

/**
 * 
 * @author Dismer Ronda
 * 
 */

@SuppressWarnings({ "unchecked" })
public class ModalNewUser extends ModalWindowsCRUD
{
	private static final long serialVersionUID = -5095261281038496825L;

	private static final Logger LOG = Logger.getLogger( ModalNewUser.class );

	protected User newUser;

	private TextField editUserLogin;
	private TextField editUserEmail;
	private TextField editUserName;
	private PasswordField editUserPassword;
	private ComboBox comboProfile;
	private ComboBox comboTester;
	private ComboBox comboStatus;

	private UserDefault lastPassword1;
	private UserDefault lastPassword2;
	
	/**
	 * 
	 * @param context
	 * @param resource
	 * @param modalOperation
	 * @param objOperacion
	 * @param parentWindow
	 */
	public ModalNewUser( AppContext context, Operation modalOperation, User orgUser, ModalParent parentWindow )
	{
		super( context, parentWindow, modalOperation, orgUser );
		setWidth( "750px" );
	}

	@Override
	public void initComponents()
	{
		super.initComponents();

		lastPassword1 = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LAST_PASSWORD1 ); 
		lastPassword2 = IOCManager._UserDefaultsManager.getUserDefault( getContext(), UserDefault.LAST_PASSWORD2 ); 

		try
		{
			newUser = (User) Utils.clone( (User) orgDto );
		}
		catch ( Throwable e1 )
		{
			newUser = new User();
			
			newUser.setStatus( User.PASS_NEW );
		}

		bi = new BeanItem<BaseDto>( newUser );

		editUserLogin = new TextField( bi.getItemProperty( "login" ) );
		editUserLogin.setWidth( "100%" );
		editUserLogin.setNullRepresentation( "" );
		editUserLogin.setRequired( true );
		editUserLogin.setRequiredError( getContext().getString( "words.required" ) );


		editUserEmail = new TextField( bi.getItemProperty( "email" ) );
		editUserEmail.setWidth( "100%" );
		editUserEmail.setNullRepresentation( "" );
		editUserEmail.setRequired( true );
		editUserEmail.setRequiredError( getContext().getString( "words.required" ) );
		
		editUserName = new TextField( bi.getItemProperty( "name" ) );
		editUserName.setWidth( "100%" );
		editUserName.setNullRepresentation( "" );
		editUserName.setRequired( true );
		editUserName.setRequiredError( getContext().getString( "words.required" ) );

		editUserPassword = new PasswordField( bi.getItemProperty( "pwd" ) );
		editUserPassword.setWidth( "100%" );
		editUserPassword.setNullRepresentation( "" );
		editUserPassword.setRequired( true );
		editUserPassword.setRequiredError( getContext().getString( "words.required" ) );

		comboProfile = new ComboBox();
		comboProfile.setWidth( "100%" );
		comboProfile.setNullSelectionAllowed( false );
		comboProfile.setTextInputAllowed( false );
		comboProfile.setImmediate( true );
		comboProfile.setPropertyDataSource( bi.getItemProperty( "ref_profile" ) );
		comboProfile.setRequired( true );
		comboProfile.setRequiredError( getContext().getString( "words.required" ) );
		fillComboProfiles();

		comboTester = new ComboBox();
		comboTester.setWidth( "100%" );
		comboTester.setNullSelectionAllowed( false );
		comboTester.setTextInputAllowed( false );
		comboTester.setImmediate( true );
		comboTester.setPropertyDataSource( bi.getItemProperty( "tester" ) );
		comboTester.setReadOnly( false );
		fillComboTester();

		comboStatus = new ComboBox();
		comboStatus.setWidth( "100%" );
		comboStatus.setNullSelectionAllowed( false );
		comboStatus.setTextInputAllowed( false );
		comboStatus.setImmediate( true );
		comboStatus.setPropertyDataSource( bi.getItemProperty( "status" ) );
		comboStatus.setReadOnly( false );
		comboStatus.setRequired( true );
		comboStatus.setRequiredError( getContext().getString( "words.required" ) );
		fillComboStatus();
		int tabIndex = 1;
		editUserName.setTabIndex( tabIndex++ );
		HorizontalLayout rowName = AppUtils.rowComponent( getContext(), editUserName, "words.name", null );
		editUserLogin.setTabIndex( tabIndex++ );
		HorizontalLayout rowLogin = AppUtils.rowComponent( getContext(), editUserLogin, "words.login.noun", null );
		editUserPassword.setTabIndex( tabIndex++ );
		HorizontalLayout rowPassword = AppUtils.rowComponent( getContext(), editUserPassword, "words.password", null );
		editUserEmail.setTabIndex( tabIndex++ );
		HorizontalLayout rowEmail = AppUtils.rowComponent( getContext(), editUserEmail, "words.email", null );
		comboProfile.setTabIndex( tabIndex++ );
		HorizontalLayout rowProfile = AppUtils.rowComponent( getContext(), comboProfile, "words.profile", null );
		comboTester.setTabIndex( tabIndex++ );
		HorizontalLayout rowTester = AppUtils.rowComponent( getContext(), comboTester, "words.tester", null );
		comboStatus.setTabIndex( tabIndex++ );
		HorizontalLayout rowStatus = AppUtils.rowComponent( getContext(), comboStatus, "modalNewUser.password.status", null );
		
		FormLayout top = new FormLayout(rowName);
		top.setMargin( new MarginInfo( true, false, false, false ) );
		top.setWidth( "692px" );
		
		FormLayout left = new FormLayout(rowLogin, rowEmail, rowTester);
		FormLayout right = new FormLayout(rowPassword, rowProfile, rowStatus);
		left.setMargin( new MarginInfo( false, false, true, false ) );
		right.setMargin( new MarginInfo( false, false, true, false ) );
		HorizontalLayout editContainer = new HorizontalLayout( left, right );
		editContainer.setWidth( "100%" );
		editContainer.setSpacing( true );

		componentsContainer.addComponent( top );
		componentsContainer.setComponentAlignment( top, Alignment.TOP_RIGHT );
		componentsContainer.addComponent( editContainer );
		
	}

	@Override
	protected String getWindowResourceKey()
	{
		return "modalNewUser";
	}

	@Override
	protected void defaultFocus()
	{
		editUserName.focus();
	}

	@Override
	protected boolean onAdd()
	{
		try
		{
			newUser.setId( null );

			IOCManager._UsersManager.createUser( getContext(), newUser );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	@Override
	protected boolean onModify()
	{
		try
		{
			String lastPassword = ((User)orgDto).getPwd();
			
			if ( !newUser.getPwd().equals( lastPassword ) )
			{
				String pwd1 = newUser.getPwd();
				AppContext ctx = getContext();
				
	       		if ( !ChangePasswordDlg.assertNotLogin( ctx, pwd1 ) ||
	       				!ChangePasswordDlg.assertCurrentPassword( ctx, pwd1 ) || 
	       				!ChangePasswordDlg.assertLastPasswords( ctx, lastPassword1, lastPassword2, pwd1 ) || 
	       				!ChangePasswordDlg.assertMinSize( ctx, pwd1 ) ||
	       				!ChangePasswordDlg.assertUpperCase( ctx, pwd1 ) || 
	       				!ChangePasswordDlg.assertDigit( ctx, pwd1 ) || 
	       				!ChangePasswordDlg.assertSymbol( ctx, pwd1 ) )
	       			return false;
	       		
	    		String last1 = lastPassword1.getData_value().isEmpty() ? lastPassword : lastPassword1.getData_value(); 

	    		IOCManager._UserDefaultsManager.setUserDefault( ctx, lastPassword2, last1 ); 
	    		IOCManager._UserDefaultsManager.setUserDefault( ctx, lastPassword1, lastPassword ); 
			}

			IOCManager._UsersManager.updateUser( getContext(), (User) orgDto, newUser );
	
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
		try
		{
			IOCManager._UsersManager.delRow( getContext(), newUser );

			return true;
		}
		catch ( Throwable e )
		{
			showErrorMessage( e );
		}

		return false;
	}

	private void fillComboProfiles()
	{
		try
		{
			Profile query = new Profile();

			List<Profile> profiles = IOCManager._ProfilesManager.getRows( getContext(), query );

			for ( Profile profile : profiles )
			{
				boolean add = getContext().getUser().getRef_profile() <= profile.getId();
				
				if ( add )
				{
					comboProfile.addItem( profile.getId() );
					comboProfile.setItemCaption( profile.getId(), profile.getDescription() );
				}
			}
		}
		catch ( Throwable e )
		{
			if ( !( e instanceof BaseException ) )
				new BaseException( e, LOG, BaseException.UNKNOWN );
		}
	}

	private void fillComboTester()
	{
		comboTester.addItem( 0 );
		comboTester.setItemCaption( 0, getContext().getString( "words.no" ) );

		comboTester.addItem( 1 );
		comboTester.setItemCaption( 1, getContext().getString( "words.yes" ) );
		
		comboTester.select( newUser.isAppTester() ? 1 : 0  );
	}

	private void fillComboStatus()
	{
		for ( int i = User.PASS_OK; i <= User.PASS_BLOCKED; i++ )
		{
			comboStatus.addItem( i );
			comboStatus.setItemCaption( i, getContext().getString( "modalNewUser.password." + i ) );
		}
	}
}
