package es.pryades.smartswitch.tasks;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.Attachment;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.Constants;
import es.pryades.smartswitch.common.TaskAction;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Task;
import es.pryades.smartswitch.dto.User;
import es.pryades.smartswitch.ioc.IOCManager;
import es.pryades.smartswitch.reports.CommonEditor;

public class DatabaseQueryTaskAction implements TaskAction, Serializable
{
	private static final long serialVersionUID = 9189254427286604511L;
	
	private static final Logger LOG = Logger.getLogger( DatabaseQueryTaskAction.class );

    public DatabaseQueryTaskAction()
	{
	}

	private void notifyUser( AppContext ctx, User user, String body, String format, ByteArrayOutputStream bos )
	{
		try
		{
			String from = ctx.getParameter( Parameter.PAR_MAIL_SENDER_EMAIL );
			String to = user.getEmail();
			String host = ctx.getParameter( Parameter.PAR_MAIL_HOST_ADDRESS );
			String sender = ctx.getParameter( Parameter.PAR_MAIL_SENDER_USER );
			String password = ctx.getParameter( Parameter.PAR_MAIL_SENDER_PASSWORD ); 

			String text = ctx.getString( "tasks.database.query.message.text" ).
					replaceAll( "%user%", user.getName() );

			String proxyHost = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_HOST );
			String proxyPort = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_PORT );

			List<Attachment> attachments = new ArrayList<Attachment>();
			
			if ( bos != null )
				attachments.add(  new Attachment( Utils.getUUID() + "." + format, "application/" + format, bos.toByteArray() ) );

			Utils.sendMail( from, to, ctx.getString( "tasks.database.query.message.subject" ), host, sender, password, text + "\n" + body, attachments, proxyHost, proxyPort );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}
	
	private ByteArrayOutputStream executeSelect( Connection conn, String queryString )
	{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
    	try
		{
			Statement statement = conn.createStatement();
    		
			ResultSet rs = statement.executeQuery( queryString );
			
			ResultSetMetaData rsmd = rs.getMetaData();
			
			@SuppressWarnings("resource")
			Workbook workbook = new XSSFWorkbook();
			
			Sheet sheet = workbook.createSheet();
			
			int i = 0;
			int j = 0;
			
			int columnCount = rsmd.getColumnCount();

			Row sheetRow = sheet.createRow( i++ );
			for ( int k = 1; k <= columnCount; k++ )
			{
				Cell cell = sheetRow.createCell( j++ );
				cell.setCellValue( rsmd.getColumnName(k) );
			}

	        while ( rs.next() ) 
			{
				sheetRow = sheet.createRow( i++ );
				j = 0;
				
				for ( int k = 1; k <= columnCount; k++ )
				{
					Cell cell = sheetRow.createCell( j++ );
					cell.setCellValue( rs.getString( rsmd.getColumnName(k) ) );
				}
			}
			
			for ( int k = 1; k <= columnCount; k++ )
				sheet.autoSizeColumn( k-1 );
			
			workbook.write( bos );
		}
    	catch ( Throwable e )
    	{
    		Utils.logException( e, LOG );
    	}
    	
    	return bos;
	}

	@Override
	public void doTask( AppContext ctx, Task task ) throws BaseException
	{
		LOG.info( "-------- started" );
		
		DatabaseQueryTaskData data = (DatabaseQueryTaskData) Utils.toPojo( task.getDetails(), DatabaseQueryTaskData.class, false );
		
		User queryUser = new User();
		queryUser.setId( data.getRef_user() );
		User user = (User) IOCManager._UsersManager.getRow( ctx, queryUser );
		
		ByteArrayOutputStream bos = null;
        
		try 
		{
			Class.forName( data.getDriver() );
			Connection conn = DriverManager.getConnection( data.getUrl(), data.getUser(), data.getPassword() );

			bos = executeSelect( conn, data.getSql() );
		       
	        conn.close();
		} 
		catch ( Throwable e) 
		{
			Utils.logException( e, LOG );
		}
		   
		notifyUser( ctx, user, "", "xls", bos );

		LOG.info( "-------- finished" );
	}

	@Override
	public CommonEditor getTaskDataEditor( AppContext context )
	{
		return new DatabaseQueryTaskDataEditor( context );
	}

	@Override
	public boolean isUserEnabledForTask( AppContext context )
	{
		return context.getUser().getRef_profile().equals( Constants.ID_PROFILE_DEVELOPER );
	}
}
