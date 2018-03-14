package es.pryades.smartswitch.common;

import lombok.Getter;

import org.apache.log4j.Logger;

import com.vaadin.ui.Window;

@SuppressWarnings("unused")
public class BaseWindow extends Window 
{
	private static final Logger LOG = Logger.getLogger( BaseWindow.class );
	
	private static final long serialVersionUID = -3120890599731227926L;
	
	@Getter
	private AppContext context;

	public BaseWindow( AppContext ctx, String title )
	{
		super( title );
		
		context = ctx; 
	}

	public void messageAndExit( AppContext ctx, String title, String message )
	{
		MessageDlg dlg = new MessageDlg( ctx, title, message );
		
		dlg.addCloseListener 
		( 
			new Window.CloseListener() 
			{
				private static final long serialVersionUID = -5303587015039065226L;

				@Override
			    public void windowClose( CloseEvent e ) 
			    {
					Logout();
			    }
			}
		);
		
		getUI().addWindow( dlg );
	}
	
	public void Logout()
	{
        getUI().close();
	}

	public void showSubWindow( Window parent, Window child )
	{
		getUI().addWindow( child );
	}
	
}
