package es.pryades.smartswitch.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;


/**
 * 
 * @author Dismer 
 * 
 */
public abstract class PagedContent extends VerticalLayout implements Property.ValueChangeListener, VtoControllerFactory
{
	private static final long serialVersionUID = 7605422764717302635L;

	private static final Logger LOG = Logger.getLogger( PagedContent.class ); 

	@Getter protected AppContext context;
	@Getter private String title;
	@Getter private BaseTable table;

	@Getter @Setter	private String orderby;
	@Getter @Setter	private String order;

	private Button buttonNew;
	private Button buttonModify;
	private Button buttonDelete;
	private Button buttonDeleteAll;
	
	private boolean isInitialized;
	private String[] sortable;

	public boolean hasNew() 		{ return true; }
	public boolean hasModify() 		{ return true; }
	public boolean hasDelete() 		{ return true; }
	public boolean hasDeleteAll()	{ return false; }
	public boolean hasExportExcel()	{ return false; }
	public boolean hasExportCsv()	{ return false; }

	public boolean isSortEnabled()	{ return true; }

	public abstract String getResourceKey();
	public abstract String[] getVisibleCols();
	public abstract BaseTable createTable() throws BaseException;
	public abstract Query getQueryObject();
	
	public abstract void onOperationNew();
	public abstract void onOperationModify( BaseDto dto );
	public abstract void onOperationDelete( BaseDto dto );

	public abstract boolean hasAddRight();
	public abstract boolean hasModifyRight();
	public abstract boolean hasDeleteRight();
	public abstract boolean hasDeleteAllRight();
	
	public abstract void updateComponent();

	public PagedContent( AppContext context )
	{
		this.context = context;
		title = context.getString( getResourceKey() + ".tabName" );
		isInitialized = false;

		setSizeFull();
	}

	public Component getQueryComponent() { return null; }

	public Component getOperationsComponent()
	{
		HorizontalLayout oppLayout = new HorizontalLayout();
		oppLayout.setSpacing( true );
		oppLayout.setMargin( new MarginInfo( false, true, true, true ) );
		oppLayout.setWidth( "100%" );
		
		HorizontalLayout rowLeft = new HorizontalLayout();
		rowLeft.setSpacing( true );
		
		if ( hasNew() && hasAddRight() )
		{
			buttonNew = new Button();
			buttonNew.setCaption( getContext().getString( "words.new" ) );
			bttnNewListener();
			rowLeft.addComponent( buttonNew );
		}

		if ( hasModify() && hasModifyRight() )
		{
			buttonModify = new Button();
			buttonModify.setCaption( getContext().getString( "words.modify" ) );
			buttonModify.setEnabled( false );
			bttnModifyListener();
			rowLeft.addComponent( buttonModify );
		}

		if ( hasDelete() && hasDeleteRight() )
		{
			buttonDelete = new Button();
			buttonDelete.setCaption( getContext().getString( "words.delete" ) );
			buttonDelete.setEnabled( false );
			bttnDeleteListener();
			rowLeft.addComponent( buttonDelete );
		}

		if ( hasDeleteAll() && hasDeleteAllRight() )
		{
			buttonDeleteAll = new Button();
			buttonDeleteAll.setCaption( getContext().getString( "words.delete.all" ) );
			buttonDeleteAll.setEnabled( true );
			bttnDeleteAllListener();
			rowLeft.addComponent( buttonDeleteAll );
		}


		List<Component> customOps = getCustomOperations();
		if ( customOps != null )
		{
			for ( Component component : customOps )
			{
				rowLeft.addComponent( component );
				rowLeft.setComponentAlignment( component, Alignment.MIDDLE_LEFT );
			}
		}

		if ( rowLeft.getComponentCount() > 0 )
		{
			oppLayout.addComponent( rowLeft );
			oppLayout.setComponentAlignment( rowLeft, Alignment.MIDDLE_LEFT );
		}
		
		if ( table.getTablePageOppContainer() != null )
		{
			oppLayout.addComponent( table.getTablePageOppContainer() );
			oppLayout.setComponentAlignment( table.getTablePageOppContainer(), Alignment.MIDDLE_RIGHT);
			oppLayout.setExpandRatio( table.getTablePageOppContainer(), 1.0f );
		}
		
		return oppLayout.getComponentCount() > 0 ? oppLayout : null;
	}

	/*public Component getOperationsComponent()
	{
		HorizontalLayout oppLayout = new HorizontalLayout();
		oppLayout.setSpacing( true );
		oppLayout.setMargin( new MarginInfo( false, true, true, true ) );
		oppLayout.setWidth( "100%" );
		
		HorizontalLayout rowLeft = new HorizontalLayout();
		rowLeft.setSpacing( true );
		
		if ( hasNew() )
		{
			buttonNew = new Button();
			buttonNew.setCaption( getContext().getString( "words.new" ) );
			bttnNewListener();
			rowLeft.addComponent( buttonNew );
		}

		if ( hasModify() )
		{
			buttonModify = new Button();
			buttonModify.setCaption( getContext().getString( "words.modify" ) );
			buttonModify.setEnabled( false );
			bttnModifyListener();
			rowLeft.addComponent( buttonModify );
		}

		if ( hasDelete() )
		{
			buttonDelete = new Button();
			buttonDelete.setCaption( getContext().getString( "words.delete" ) );
			buttonDelete.setEnabled( false );
			bttnDeleteListener();
			rowLeft.addComponent( buttonDelete );
		}

		List<Component> customOps = getCustomOperations();
		if ( customOps != null )
		{
			for ( Component component : customOps )
			{
				rowLeft.addComponent( component );
				rowLeft.setComponentAlignment( component, Alignment.MIDDLE_LEFT );
			}
		}

		if ( rowLeft.getComponentCount() > 0 )
		{
			oppLayout.addComponent( rowLeft );
			oppLayout.setComponentAlignment( rowLeft, Alignment.MIDDLE_LEFT );
		}
		
		if ( table.getTablePageOppContainer() != null )
		{
			oppLayout.addComponent( table.getTablePageOppContainer() );
			oppLayout.setComponentAlignment( table.getTablePageOppContainer(), Alignment.MIDDLE_RIGHT);
			oppLayout.setExpandRatio( table.getTablePageOppContainer(), 1.0f );
		}
		
		return oppLayout.getComponentCount() > 0 ? oppLayout : null;
	}*/
	
	public String[] getSortableCols()
	{
		return getVisibleCols();
	}
	
	public String[] getDownloadableCols()
	{
		return getVisibleCols();
	}
	
	private boolean isColumnSortable( String column )
	{
		if ( sortable == null )
			return false;
		
		for ( int i = 0; i < sortable.length; i++ )
			if ( column.equals( sortable[i] ) )
				return true;
		
		return false;
	}
	
	public void onSortByColumn( String column )
	{
		if ( isColumnSortable( column ) )
		{
	        if ( !column.equals( orderby ) )
	        {
	        	table.getTable().setColumnIcon( orderby, null );
	       		
	        	orderby = column;
	        }
	        else
	        {
	        	if ( order.equals( "asc" ) )
	        		order = "desc";
	        	else
	        		order = "asc";
	        }
	
	    	if ( order.equals( "asc" ) )
	    		table.getTable().setColumnIcon( orderby, new ThemeResource( "images/asc.png" ) );
	   		else
	   			table.getTable().setColumnIcon( orderby, new ThemeResource( "images/desc.png" ) );
	    	
	        refreshVisibleContent( true );
		}
	}

	public Component getTableComponent()
	{
		try
		{
			table = createTable();
			table.setMargin( true );
	
			HashMap<String, String> tableHeadersName = new HashMap<String, String>();
	
			String[] visibleCols = getVisibleCols();
			sortable = getSortableCols();

			for ( String atribute : visibleCols )
				tableHeadersName.put( atribute, getContext().getString( getResourceKey() + ".table.headerName." + atribute ) );
	
			table.setTableHeadersNames( tableHeadersName );
			table.setVisibleCols( new ArrayList<String>( Arrays.asList( visibleCols ) ) );
			table.initializeTable( getResourceKey() );
			table.getTable().addValueChangeListener( this );
			
			if ( table.getTablePageOppContainer() != null )
				table.getTablePageOppContainer().setMargin( false );

			if ( isSortEnabled() )
			{
				table.getTable().addHeaderClickListener(new Table.HeaderClickListener() 
				{
					private static final long serialVersionUID = -9126938485322423212L;
	
					public void headerClick(HeaderClickEvent event) 
					{
						if ( event.getButton().equals( MouseButton.LEFT ) )
							onSortByColumn( (String) event.getPropertyId() );
				    }
				});
				
				table.getTable().setSortEnabled( false );
				
				if ( order != null && orderby != null )
				{
			    	if ( order.equals( "asc" ) )
			    		table.getTable().setColumnIcon( orderby, new ThemeResource( "images/asc.png" ) );
			   		else
			   			table.getTable().setColumnIcon( orderby, new ThemeResource( "images/desc.png" ) );
				}
			}
			
			/*table.getTable().setItemDescriptionGenerator( new ItemDescriptionGenerator() 
			{                             
				private static final long serialVersionUID = -3928434685042148266L;

				public String generateDescription(Component source, Object itemId, Object propertyId) 
			    {
					if ( propertyId == null )
						return null;
					
					String resourceName = getResourceKey() + ".table.headerHint." + propertyId;
					String resourceValue =  getContext().getString( resourceName );
					
					if ( resourceValue.equals( resourceName ) )
						return getContext().getString( getResourceKey() + ".table.headerName." + propertyId );
					
					return resourceValue;
			    }
			});*/

			return table;
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
			return null;
		}
	}
	
	public void initializeComponent()
	{
		if ( !isInitialized )
		{
			isInitialized = true;
			
			Component queryComponent = getQueryComponent();
			Component tableComponent = getTableComponent();
			Component operationsComponent = getOperationsComponent();

			if ( queryComponent != null )
			{
				HorizontalLayout queryContainer = new HorizontalLayout();
				queryContainer.setWidth( "100%" );
				queryContainer.setStyleName( "query_container" );
				
				Panel panelInformation = new Panel();
				panelInformation.setStyleName( "borderless light" );
				panelInformation.setSizeFull();
				
				panelInformation.setContent( queryComponent );
				
				queryComponent.setStyleName( "query" );
				//((AbstractOrderedLayout) queryComponent).setMargin( new MarginInfo( false, false, false, true ) );
				
				queryContainer.addComponent( panelInformation );
				
				addComponent( queryContainer );
			}
			
			if ( tableComponent != null )
				addComponent( tableComponent );

			if ( operationsComponent != null )
				addComponent( operationsComponent );

			if ( tableComponent != null )
				setExpandRatio( tableComponent, 1.0f );
			
			refreshVisibleContent( true );
		}
		else
			updateComponent();
	}

	private void bttnNewListener()
	{
		buttonNew.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = -7615009300045509378L;

			public void buttonClick( ClickEvent event )
			{
				onOperationNew();
			}
		} );
	}

	private void bttnModifyListener()
	{
		buttonModify.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 2159410851787504226L;

			public void buttonClick( ClickEvent event )
			{
				Long rowId = (Long)table.getTable().getValue();

				if ( rowId != null )
				{
					try
					{
						onOperationModify( (BaseDto)table.getRowValue( rowId ) );
					}
					catch ( Throwable e )
					{
						Utils.logException( e, LOG );
					}
				}
				else
				{
					if ( buttonModify != null )
						buttonModify.setEnabled( false );
					if ( buttonDelete != null )
						buttonDelete.setEnabled( false );
				}
			}
		} );
	}

	private void bttnDeleteListener()
	{
		buttonDelete.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 5133275471094201227L;

			public void buttonClick( ClickEvent event )
			{
				Long rowId = (Long)table.getTable().getValue();

				if ( rowId != null )
				{
					try
					{
						onOperationDelete( (BaseDto)table.getRowValue( rowId ) );
					}
					catch ( Throwable e )
					{
						Utils.logException( e, LOG );
					}
				}
				else
				{
					if ( buttonModify != null )
						buttonModify.setEnabled( false );
					if ( buttonDelete != null )
						buttonDelete.setEnabled( false );
				}
			}
		} );
	}

	private void bttnDeleteAllListener()
	{
		buttonDeleteAll.addClickListener( new Button.ClickListener()
		{
			private static final long serialVersionUID = 605196593292020161L;

			public void buttonClick( ClickEvent event )
			{
				ConfirmDialog.show( (UI)getContext().getData( "Application" ), getContext().getString( getResourceKey() + ".confirm.deleteAll" ),
		        new ConfirmDialog.Listener() 
				{
					private static final long serialVersionUID = -722581380085822048L;

					public void onClose(ConfirmDialog dialog) 
		            {
		                if (dialog.isConfirmed()) 
		                {
							onOperationDeleteAll();
		                } 
		            }
		        });
			}
		} );
	}

	public void onSelectedRow()
	{
		boolean enabled = table.getTable().getValue() != null;
		
		if ( buttonDelete != null )
			buttonDelete.setEnabled( enabled );
		if ( buttonModify != null )
			buttonModify.setEnabled( enabled );
	}
	
	@Override
	public void valueChange( ValueChangeEvent event )
	{
		onSelectedRow();
	}

	public void refreshVisibleContent( boolean repage )
	{
		try
		{			
			Query query = getQueryObject();
		
			query.setOrderby( getOrderby() );
			query.setOrder( getOrder() );
			
			table.refreshVisibleContent( query, repage );
		}
		finally
		{
			if ( buttonModify != null )
				buttonModify.setEnabled( false );
			if ( buttonDelete != null )
				buttonDelete.setEnabled( false );
		}
	}

	public void onOperationDeleteAll()
	{
		try
		{
			getFieldManagerImp().delAllRows( getContext(), getQueryObject() );
			
			refreshVisibleContent( true );
		}
		catch ( Throwable e )
		{
			Utils.logException( e, LOG );
		}
	}

	public List<Component> getCustomOperations() 
	{ 
		List<Component> ops = new ArrayList<Component>();
		
		if ( hasExportCsv() )
		{
			Button btnCsv = new Button();
			btnCsv.setCaption( getContext().getString( "words.download.csv" ) );
			btnCsv.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 7016168454309584677L;
	
				public void buttonClick( ClickEvent event )
				{
					if ( table.getNumberOfRows() > getContext().getIntegerParameter( Parameter.PAR_MAX_ROWS_EXPORTED ) )
					{
						Utils.showNotification( getContext(), getContext().getString( "PagedContent.too.many.rows" ), Notification.Type.ERROR_MESSAGE );
						
						AsyncExportThread thread = new AsyncExportThread( getContext(), "csv" );
						thread.start();
					}
				}
			});
			ops.add( btnCsv );
			
	        FileDownloader fileDownloaderCsv = new FileDownloader( createResource( "csv" ) );
	        fileDownloaderCsv.extend( btnCsv );
		}
		
		if ( hasExportExcel() )
		{
			Button btnExcel = new Button();
			btnExcel.setCaption( getContext().getString( "words.download.xls" ) );
			btnExcel.addClickListener( new Button.ClickListener()
			{
				private static final long serialVersionUID = 8080872723898039919L;
	
				public void buttonClick( ClickEvent event )
				{
					if ( table.getNumberOfRows() > getContext().getIntegerParameter( Parameter.PAR_MAX_ROWS_EXPORTED ) )
					{
						Utils.showNotification( getContext(), getContext().getString( "PagedContent.too.many.rows" ), Notification.Type.ERROR_MESSAGE );
						
						AsyncExportThread thread = new AsyncExportThread( getContext(), "xls" );
						thread.start();
					}
				}
			});
			
			ops.add( btnExcel );
			
	        FileDownloader fileDownloaderXls = new FileDownloader( createResource( "xls" ) );
	        fileDownloaderXls.extend( btnExcel );
		}
        
		return ops;
	}
	
	private StreamResource createResource( final String format ) 
	{
		return new StreamResource( 
			new StreamSource() 
			{
				private static final long serialVersionUID = 2499947626966563145L;

				@Override
	            public InputStream getStream() 
	            {
					if ( table.getNumberOfRows() > getContext().getIntegerParameter( Parameter.PAR_MAX_ROWS_EXPORTED ) )
					{
						LOG.info( "Too many rows to export. Async dispatched" );
						
						return null;
					}
					else
					{
						return getExportedList( format, null, -1L );
					}
	            }
	        }, 
	        Utils.getUUID() + "." + format );
    }
	
	private void exportRowsXls( OutputStream os, Integer pageSize, Long pageNumber ) throws Exception
	{
		@SuppressWarnings("resource")
		Workbook workbook = new XSSFWorkbook();
		
		Sheet sheet = workbook.createSheet();
		
		int i = 0;
		int j = 0;
		
		Row sheetRow = sheet.createRow( i++ );
		
		String visibleCols[] = getDownloadableCols();
		
		for ( String col : visibleCols )
		{
			Cell cell = sheetRow.createCell( j++ );
			cell.setCellValue( getContext().getString( getResourceKey() + ".table.headerName." + col ) );
		}
		
		Query query = getQueryObject();
		
		query.setOrderby( getOrderby() );
		query.setOrder( getOrder() );
		query.setPageNumber( pageNumber );
		query.setPageSize( pageSize );

		@SuppressWarnings("unchecked")
		List<BaseDto> rows = getFieldManagerImp().getRows( getContext(), query );
		
		if ( rows.size() == 0 )
			throw new Exception( "no more rows to export" );

		preProcessRows( rows );

		for ( BaseDto row : rows )
		{
			GenericControlerVto controller = getControlerVto( getContext() );
			
			GenericVto vto = controller.generateVtoFromDto( this, row );
			
			sheetRow = sheet.createRow( i++ );
			j = 0;
			
			for ( String col : visibleCols )
			{
				Object value = Utils.getFieldObject( vto, col );;

				Cell cell = sheetRow.createCell( j++ );
				
				String text = "";
				if ( value != null  )
				{
					if ( value.getClass().equals( Button.class ) )
					{
						if ( ((Button)value).getCaption() != null )
							text = ((Button)value).getCaption().replaceAll( "\\(", "" ).replaceAll( "\\)", "" ).trim();
						else
							text = "";
					}
					else
						text = value.toString();
				}
				
				cell.setCellValue( text );
			}
		}
		
		for ( int col = 0; col < visibleCols.length; col++ )
			sheet.autoSizeColumn( col );
		
		workbook.write( os );
	}

	private void exportRowsCsv( OutputStream os, Integer pageSize, Long pageNumber ) throws Exception
	{
		String visibleCols[] = getDownloadableCols();

		String line = "";
		
		for ( String col : visibleCols )
		{
			if ( !line.isEmpty() )
				line += ",";
			
			line += "\"" + getContext().getString( getResourceKey() + ".table.headerName." + col ) + "\"";
		}
		
		line += "\n";
		
		os.write( line.getBytes() );
		
		Query query = getQueryObject();
		
		query.setOrderby( getOrderby() );
		query.setOrder( getOrder() );
		query.setPageNumber( pageNumber );
		query.setPageSize( pageSize );

		@SuppressWarnings("unchecked")
		List<BaseDto> rows = getFieldManagerImp().getRows( getContext(), query );

		if ( rows.size() == 0 )
			throw new Exception( "no more rows to export" );

		preProcessRows( rows );

		for ( BaseDto row : rows )
		{
			GenericControlerVto controller = getControlerVto( getContext() );
			
			GenericVto vto = controller.generateVtoFromDto( this, row );
			
			line = "";
			
			for ( String col : visibleCols )
			{
				Object value = Utils.getFieldObject( vto, col );;
				
				if ( !line.isEmpty() )
					line += ",";
				
				String text = "";
				if ( value != null  )
				{
					if ( value.getClass().equals( Button.class ) )
						text = ((Button)value).getCaption().replaceAll( "\\(", "" ).replaceAll( "\\)", "" ).trim();
					else
						text = value.toString();
				}

				line += text;
			}
			
			line += "\n";
			
			os.write( line.getBytes() );
		}
	}

	public ByteArrayOutputStream getExportedOutputStream( String format, Integer pageSize, Long pageNumber )
	{
		try 
	    {
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        
	        if ( "xls".equals( format ) )
	        	exportRowsXls( bos, pageSize, pageNumber );
	        else if ( "csv".equals( format ) )
	        	exportRowsCsv( bos, pageSize, pageNumber );
	        
	        bos.close();
	
	        return bos;
	    }
	    catch ( Throwable e ) 
	    {
	        Utils.logException( e, LOG );
	        return null;
	    }
	}
	
	public InputStream getExportedList( String format, Integer pageSize, Long pageNumber )
	{
        ByteArrayOutputStream bos = getExportedOutputStream( format, pageSize, pageNumber );
        
        return bos != null ? new ByteArrayInputStream( bos.toByteArray() ) : null;
	}

	public class AsyncExportThread extends Thread 
	{
		@Getter
		private AppContext context;

		@Getter
		private String format;
		
		public AsyncExportThread( AppContext context, String format ) 
		{
			this.context = context;
			this.format = format;
		}

	    public void run () 
	    {
	    	LOG.info( "async export started" );

	    	long totalPages = 1L;
	    	Integer pageSize = getContext().getIntegerParameter( Parameter.PAR_MAX_ROWS_EXPORTED );
	    	
	    	Query query = getQueryObject();
			
			try
			{
				long rows = getFieldManagerImp().getNumberOfRows( getContext(), query );
				
				totalPages = (long)Math.ceil( rows / (double)pageSize );
			}
			catch ( Throwable e )
			{
				Utils.logException( e, LOG );
			}

	    	for ( int i = 0; i < totalPages; i++ )
	    	{
	    		LOG.info( "exporting " + (i+1) + " of " + totalPages );
	    		
		    	ByteArrayOutputStream bos = getExportedOutputStream( format, pageSize, (long)(i+1) );
		    	
		    	if ( bos != null )
		    	{
					String from = getContext().getParameter( Parameter.PAR_MAIL_SENDER_EMAIL );
					String to = getContext().getUser().getEmail();
					String host = getContext().getParameter( Parameter.PAR_MAIL_HOST_ADDRESS );
					String sender = getContext().getParameter( Parameter.PAR_MAIL_SENDER_USER );
					String password = getContext().getParameter( Parameter.PAR_MAIL_SENDER_PASSWORD ); 
		
					String text = getContext().getString( "PagedContent.message.text" ).
							replaceAll( "%user%", getContext().getUser().getName() );
					String subject = getContext().getString( "PagedContent.message.subject" ).
							replaceAll( "%page%", Long.toString( i+1 ) ).
					        replaceAll( "%total%", Long.toString( totalPages ) );
		
					String proxyHost = getContext().getParameter( Parameter.PAR_SOCKS5_PROXY_HOST );
					String proxyPort = getContext().getParameter( Parameter.PAR_SOCKS5_PROXY_PORT );
					
					List<Attachment> attachments = new ArrayList<Attachment>();
					attachments.add(  new Attachment( Utils.getUUID() + "." + format, "application/" + format, bos.toByteArray() ) );
					
					try
					{
						Utils.sendMail( from, to, subject, host, sender, password, text, attachments, proxyHost, proxyPort );
					}
					catch ( Throwable e )
					{
						Utils.logException( e, LOG );
					}
		    	}
	    	}
			
	    	LOG.info( "async export finished" );
	    }
	}
}
