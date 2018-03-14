package es.pryades.smartswitch.dal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.apache.ibatis.session.SqlSession;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.Settings;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dal.ibatis.BaseMapper;
import es.pryades.smartswitch.dto.BaseDto;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.Query;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@SuppressWarnings( {"unchecked","rawtypes", "unused"} )
@Data
public abstract class BaseManagerImpl implements BaseManager, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4196236496172454400L;
	Class mapperClass;
	Class dtoClass;
	Logger logger;
	
	public BaseManagerImpl( Class mapperClass, Class dtoClass, Logger logger )
	{
		setMapperClass( mapperClass );
		setLogger( logger );
		setDtoClass( dtoClass );
	}
	
	@Override
	public boolean hasUniqueId( AppContext ctx ) 
	{
		return true;
	}
	
	@Override
	public boolean hasBlob() 
	{
		return false;
	}
	
	public void setRow( AppContext ctx, BaseDto lastRow, BaseDto newRow ) throws BaseException
	{
		/* this is not working well when updating a parameter from null to some not null value
		if ( lastRow != null && newRow.equals( lastRow ) )
		/
		{
			getLogger().info( "nothing to update in the row" );
			
			return;
		}*/
		
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();

		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );

			Utils.emptyToNull( newRow, newRow.getClass() );
			
			if ( !hasUniqueId( ctx ) || newRow.getId() == null )
			{
				if ( !hasBlob() && isLogEnabled( ctx, "I" ) ) 
					getLogger().info( "inserting row " + newRow );
				
				mapper.addRow( newRow );
				
				if ( hasUniqueId( ctx ) && newRow.getId() == null )
					throw new BaseException( new Exception( "Row id was not set. Check SQL script" ), getLogger(), BaseException.INVALID_ROW_ID );
			}
			else
			{
				if ( !hasBlob() && isLogEnabled( ctx, "U" ) )
					getLogger().info( "updating row " + newRow );

				if ( lastRow == null )
					throw new BaseException( new Exception( "Update row without the last row value" ), getLogger(), BaseException.UPDATE_WITHOUT_LAST );
				
				if ( !lastRow.getClass().equals( newRow.getClass() ) )
					throw new BaseException( new Exception( "Update mismatch classes" ), getLogger(), BaseException.UPDATE_CLASSES_MISMATCH );
				
				mapper.setRow( newRow );
			}

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish ) 
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );
			
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}

	public void delRow( AppContext ctx, BaseDto row ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			if ( !hasBlob() && isLogEnabled( ctx, "D" ) )
				getLogger().info( "deleting row " + row );

			mapper.delRow( row );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}

	public long getNumberOfRows( AppContext ctx, Query query ) throws BaseException
    {
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		long count = 0;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			count = mapper.getNumberOfRows( query ); 
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return count;
    }
    
	public List getRows( AppContext ctx, Query query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		ArrayList<BaseDto> rows = new ArrayList<BaseDto>();
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			boolean paged = (query != null) && (query.getPageSize() != null) && (query.getPageSize() != -1);  
			
			if ( isLogEnabled( ctx, "S" ) )
				getLogger().info( "retrieving rows " + query );

			ArrayList<BaseDto> temp = paged ? mapper.getPage( query ) : mapper.getRows( query );
			
			for ( BaseDto dto : temp )
			{
				if ( dto != null )
				{
					Utils.nullToEmpty( dto, dto.getClass() );
					
					rows.add( dto );
				}
			}
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}

		if ( rows == null )
			throw new BaseException( new Exception( "Null return" ), getLogger(), BaseException.NULL_RETURN );
		
		return rows;
	}
	
	public BaseDto newDto() throws BaseException
	{
		try 
		{
			return (BaseDto) dtoClass.newInstance();
		} 
		catch ( Throwable e ) 
		{
			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		} 
	}

	public BaseDto getLastRow( AppContext ctx, BaseDto query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		BaseDto row = null;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			row = mapper.getLastRow( query ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			if ( e instanceof BaseException )
				throw (BaseException)e;
			
			throw new BaseException( e, getLogger(), 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return row;
	}
	
	public BaseDto getRow( AppContext ctx, BaseDto dto ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		BaseDto row = null;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			row = mapper.getRow( dto ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			if ( e instanceof BaseException )
				throw (BaseException)e;
			
			throw new BaseException( e, getLogger(), 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return row;
	}
	
	public BaseDto getNextRow( AppContext ctx, BaseDto query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		BaseDto row = null;
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			row = mapper.getNextRow( query ); 
			
			if ( row != null )
				Utils.nullToEmpty( row, row.getClass() );
		}
		catch ( Throwable e )
		{
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			if ( e instanceof BaseException )
				throw (BaseException)e;
			
			throw new BaseException( e, getLogger(), 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
		
		return row;
	}

	public void delAllRows( AppContext ctx, Query query ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try 
		{
			BaseMapper mapper = (BaseMapper)session.getMapper( mapperClass );
			
			if ( !hasBlob() && isLogEnabled( ctx, "D" ) )
				getLogger().info( "deleting rows " + query );

			mapper.delAllRows( query );

			if ( finish )
				session.commit();
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( isLogEnabled( ctx, "E" ) )			
				getLogger().info( e.getCause() != null ? e.getCause().toString() : e.toString() );

			throw new BaseException( e, getLogger(), BaseException.UNKNOWN );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}
	
	public boolean isLogEnabled( AppContext ctx, String action )
	{
		return ctx.isLogEnabled( getLogSetting(), action );
	}

	@Override
	public long getLogSetting() 
	{
		return Parameter.PAR_LOG_DEFAULT;
	}
}
