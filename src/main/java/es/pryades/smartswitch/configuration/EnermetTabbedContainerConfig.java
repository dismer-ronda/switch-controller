package es.pryades.smartswitch.configuration;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.PagedContent;
import es.pryades.smartswitch.configuration.tabs.FacilitiesConfig;
import es.pryades.smartswitch.configuration.tabs.InterruptorsConfig;
import es.pryades.smartswitch.configuration.tabs.ParametersConfig;
import es.pryades.smartswitch.configuration.tabs.ProfilesConfig;
import es.pryades.smartswitch.configuration.tabs.TasksConfig;
import es.pryades.smartswitch.configuration.tabs.UsersConfig;

/**
 * @author Dismer Ronda
 * 
 */
public class EnermetTabbedContainerConfig extends VerticalLayout implements TabSheet.SelectedTabChangeListener
{
	private static final long serialVersionUID = 4707864569555868624L;

	private static final Logger LOG = Logger.getLogger( EnermetTabbedContainerConfig.class );

	@Getter @Setter private AppContext context;
	
	private VerticalLayout content;
	private TabSheet tabsheet;
	private List<PagedContent> tabContentList;

	public EnermetTabbedContainerConfig( AppContext context ) 
	{
		this.context = context;
	}
	
	public void selectedTabChange( SelectedTabChangeEvent event )
	{
		TabSheet tabsheet = event.getTabSheet();
		Tab tab = tabsheet.getTab( tabsheet.getSelectedTab() );
		PagedContent configuration = (PagedContent)tab.getComponent();
		configuration.initializeComponent();
	}

	private void initComponents() throws BaseException
	{
		try
		{
			AppContext ctx = getContext();

			if ( content == null )
				content = new VerticalLayout();

			if ( tabsheet == null )
				tabsheet = new TabSheet();

			if ( tabContentList == null )
			{
				tabContentList = new ArrayList<PagedContent>();

				if ( ctx.hasRight( "configuration.tasks" ) )
					tabContentList.add( new TasksConfig( ctx ) );

				if ( ctx.hasRight( "configuration.users" ) )
					tabContentList.add( new UsersConfig( ctx ) );

				if ( ctx.hasRight( "configuration.parameters" ) )
					tabContentList.add( new ParametersConfig( ctx ) );

				if ( ctx.hasRight( "configuration.profiles" ) )
					tabContentList.add( new ProfilesConfig( ctx ) );

				if ( ctx.hasRight( "configuration.interruptors" ) )
					tabContentList.add( new FacilitiesConfig( ctx ) );

				if ( ctx.hasRight( "configuration.interruptors" ) )
					tabContentList.add( new InterruptorsConfig( ctx ) );

				for ( PagedContent item : tabContentList )
				{
					item.setSizeFull();
					tabsheet.addTab( item );
					tabsheet.getTab( item ).setCaption( item.getTitle() );
				}

				PagedContent configuration = (PagedContent)tabContentList.get( 0 );
				configuration.initializeComponent();
				tabsheet.setSelectedTab( configuration );

				tabsheet.addSelectedTabChangeListener( this );
			}

		}
		catch ( Throwable e )
		{
			if ( !(e instanceof BaseException) )
				throw new BaseException( e, LOG, BaseException.UNKNOWN );
			
			throw (BaseException)e;
		}
	}

	public void render() throws BaseException
	{
		this.initComponents();
		
		content.setSizeFull();
		tabsheet.setSizeFull();

		content.addComponent( tabsheet );

		addComponent( content );
	}
}
