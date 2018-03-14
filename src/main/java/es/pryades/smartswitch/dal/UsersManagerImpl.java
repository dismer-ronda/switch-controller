package es.pryades.smartswitch.dal;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.common.CalendarUtils;
import es.pryades.smartswitch.common.Utils;
import es.pryades.smartswitch.dal.ibatis.UserMapper;
import es.pryades.smartswitch.dto.Parameter;
import es.pryades.smartswitch.dto.User;
import es.pryades.smartswitch.dto.query.UserQuery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
@SuppressWarnings({"rawtypes"})
public class UsersManagerImpl extends BaseManagerImpl implements UsersManager
{
	private static final long serialVersionUID = -975004799858559075L;

	private static final Logger LOG = Logger.getLogger( UsersManagerImpl.class );

    @Inject
    ParametersManager parametersManager;
    
    public static BaseManager build()
	{
		return new UsersManagerImpl();
	}
	
	public UsersManagerImpl()
	{
		super( UserMapper.class, User.class, LOG );
	}

	public User createUser( AppContext ctx, User usuario ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try
		{
	    	usuario.setRetries(0);
			usuario.setStatus(User.PASS_OK);
			usuario.setChanged(CalendarUtils.getTodayAsInt());

			usuario.setPwd(Utils.MD5(usuario.getPwd()));

	    	setRow( ctx, null, usuario );

			if ( finish )
				session.commit();
			
			return usuario;
		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}

	/**
	 * @author Dismer Ronda
	 * @since  1.1.2
	 * @param ctx
	 * @param usuario
	 * @param newlistaCentros
	 * @throws BaseException
	 */
	public void updateUser( AppContext ctx, User usuario, User newUsuario ) throws BaseException
	{
		SqlSession session = ctx.getSession();
		
		boolean finish = session == null;		
		
		if ( finish )
			session = ctx.openSession();
		
		try
		{
			
			if ( usuario != null && newUsuario != null )
			{
		    	if ( !newUsuario.getPwd().equals( usuario.getPwd() ) )
		    	{
					newUsuario.setRetries(0);
					newUsuario.setStatus(User.PASS_OK);

					setPassword(ctx, newUsuario, null, null, false);
		    	}
		    	
		    	setRow(ctx, usuario, newUsuario);
			}
			
			if ( finish )
				session.commit();

		}
		catch ( Throwable e )
		{
			if ( finish )
				session.rollback();
			
			if ( e instanceof BaseException )
				throw (BaseException)e;

			throw new BaseException( e, LOG, 0 );
		}
		finally
		{
			if ( finish )
				ctx.closeSession();
		}
	}

	public void loadUsuario(AppContext ctx, String login) throws BaseException
	{
		SqlSession session = ctx.getSession();

		boolean finish = session == null;

		if (finish)
			session = ctx.openSession();

		try
		{
			UserQuery query = new UserQuery();

			query.setLogin(login);

			List rows = getRows(ctx, query);

			if (rows.size() != 1)
				throw new BaseException(new Exception("Null return"), LOG, BaseException.NULL_RETURN);

			User user = (User) rows.get(0);

			if (user.getStatus() == User.PASS_BLOCKED)
				throw new BaseException(new Exception("Account " + login + " blocked"), LOG, BaseException.LOGIN_BLOCKED);

			ctx.setUser(user);
		}
		catch (Throwable e)
		{
			if (e instanceof BaseException)
				throw (BaseException) e;

			throw new BaseException(e, LOG, 0);
		}
		finally
		{
			if (finish)
				ctx.closeSession();
		}
	}

	public void validateUser(AppContext ctx, String login, String password, String subject, String text, boolean mail ) throws BaseException
	{
		boolean rollback = true;

		SqlSession session = ctx.getSession();

		boolean finish = session == null;

		if (finish)
			session = ctx.openSession();

		try
		{
			loadUsuario(ctx, login);

			User user = ctx.getUser();

			if (Utils.MD5(password).equalsIgnoreCase(user.getPwd()))
			{
				user.setRetries(0);

				setIntentos(ctx, user);

				if (finish)
					session.commit();

				LOG.info( "user " + user.getLogin() + " logged" );
				
				return;
			}

			if (user.getRetries() == ctx.getIntegerParameter( Parameter.PAR_LOGIN_FAILS_NEW_PASS ) )
			{
				user.setRetries(user.getRetries() + 1);
				user.setStatus(User.PASS_CHANGED);
				user.setPwd((String) Utils.getRandomPassword( ctx.getIntegerParameter( Parameter.PAR_PASSWORD_MIN_SIZE ) ));

				setPassword(ctx, user, subject, text, mail);

				if (finish)
					session.commit();
				rollback = false;

				throw new BaseException(new Exception("Password for " + login + " changed"), LOG, BaseException.LOGIN_PASSWORD_CHANGED);
			}
			else if (user.getRetries() == ctx.getIntegerParameter( Parameter.PAR_LOGIN_FAILS_BLOCK ) )
			{
				user.setStatus(User.PASS_BLOCKED);

				setStatus(ctx, user);

				if (finish)
					session.commit();
				rollback = false;

				throw new BaseException(new Exception("Account " + login + " blocked"), LOG, BaseException.LOGIN_BLOCKED);
			}
			else
			{
				user.setRetries(user.getRetries() + 1);

				setIntentos(ctx, user);

				if (finish)
					session.commit();
			}

			rollback = false;

			if (user.getStatus() == User.PASS_CHANGED)
				throw new BaseException(new Exception("Password for " + login + " changed"), LOG, BaseException.LOGIN_PASSWORD_CHANGED);

			throw new BaseException(new Exception("Login as " + login + " failed "), LOG, BaseException.LOGIN_FAIL);
		}
		catch (Throwable e)
		{
			if (finish && rollback)
				session.rollback();

			if (e instanceof BaseException)
				throw (BaseException) e;

			throw new BaseException(e, LOG, 0);
		}
		finally
		{
			if (finish)
				ctx.closeSession();
		}
	}

	public void setPassword(AppContext ctx, User usuario, String subject, String text, boolean mail ) throws BaseException
	{
		SqlSession session = ctx.getSession();

		boolean finish = session == null;

		if (finish)
			session = ctx.openSession();

		try
		{
			UserMapper mapper = session.getMapper(UserMapper.class);

			if ( text != null )
				text = Utils.replaceWildcards(text, usuario);

			usuario.setPwd(Utils.MD5(usuario.getPwd()));
			usuario.setChanged(CalendarUtils.getTodayAsInt());

			mapper.setPassword(usuario);

			if ( mail )
			{
				String proxyHost = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_HOST );
				String proxyPort = ctx.getParameter( Parameter.PAR_SOCKS5_PROXY_PORT );
				
				Utils.sendMail( ctx.getParameter( Parameter.PAR_MAIL_SENDER_EMAIL ), usuario.getEmail(), subject, ctx.getParameter( Parameter.PAR_MAIL_HOST_ADDRESS ), ctx.getParameter( Parameter.PAR_MAIL_SENDER_USER ), ctx.getParameter( Parameter.PAR_MAIL_SENDER_PASSWORD ), text, null, proxyHost, proxyPort );
			}

			if (finish)
				session.commit();
		}
		catch (Throwable e)
		{
			if (finish)
				session.rollback();

			if (e instanceof BaseException)
				throw (BaseException) e;

			throw new BaseException(e, LOG, 0);
		}
		finally
		{
			if (finish)
				ctx.closeSession();
		}
	}

	public void setIntentos(AppContext ctx, User usuario) throws BaseException
	{
		SqlSession session = ctx.getSession();

		boolean finish = session == null;

		if (finish)
			session = ctx.openSession();

		try
		{
			UserMapper mapper = session.getMapper(UserMapper.class);

			mapper.setRetries(usuario);

			if (finish)
				session.commit();
		}
		catch (Throwable e)
		{
			if (finish)
				session.rollback();

			if (e instanceof BaseException)
				throw (BaseException) e;

			throw new BaseException(e, LOG, 0);
		}
		finally
		{
			if (finish)
				ctx.closeSession();
		}
	}

	public void setStatus(AppContext ctx, User usuario) throws BaseException
	{
		SqlSession session = ctx.getSession();

		boolean finish = session == null;

		if (finish)
			session = ctx.openSession();

		try
		{
			UserMapper mapper = session.getMapper(UserMapper.class);

			mapper.setStatus(usuario);

			if (finish)
				session.commit();
		}
		catch (Throwable e)
		{
			if (finish)
				session.rollback();

			if (e instanceof BaseException)
				throw (BaseException) e;

			throw new BaseException(e, LOG, 0);
		}
		finally
		{
			if (finish)
				ctx.closeSession();
		}
	}

	public void sendNewPassword(AppContext ctx, String email, String subject, String text, boolean mail ) throws BaseException
	{
		SqlSession session = ctx.getSession();

		boolean finish = session == null;

		if (finish)
			session = ctx.openSession();

		try
		{
			loadUsuario(ctx, email);

			User user = ctx.getUser();

			String password = (String) Utils.getRandomPassword( ctx.getIntegerParameter( Parameter.PAR_PASSWORD_MIN_SIZE ) );

			user.setPwd(password);
			user.setStatus(User.PASS_FORGET);
			user.setRetries(0);

			setPassword(ctx, user, subject, text, mail );

			if (finish)
				session.commit();
		}
		catch (Throwable e)
		{
			if (finish)
				session.rollback();

			if (e instanceof BaseException)
				throw (BaseException) e;

			throw new BaseException(e, LOG, 0);
		}
		finally
		{
			if (finish)
				ctx.closeSession();
		}
	}

	public void remoteLogin(AppContext ctx, String login, String password) throws BaseException
	{
		boolean rollback = true;

		SqlSession session = ctx.getSession();

		boolean finish = session == null;

		if (finish)
			session = ctx.openSession();

		try
		{
			loadUsuario(ctx, login);

			User user = ctx.getUser();

			if (Utils.MD5(password).equalsIgnoreCase(user.getPwd()))
			{
				user.setRetries(0);

				setIntentos(ctx, user);

				if (finish)
					session.commit();

				LOG.info( "remote user " + user.getLogin() + " logged" );
				
				return;
			}

			if (user.getRetries() == ctx.getIntegerParameter( Parameter.PAR_LOGIN_FAILS_BLOCK ))
			{
				user.setStatus(User.PASS_BLOCKED);

				setStatus(ctx, user);

				if (finish)
					session.commit();
				rollback = false;

				throw new BaseException(new Exception("Account " + login + " blocked"), LOG, BaseException.LOGIN_BLOCKED);
			}
			else
			{
				user.setRetries(user.getRetries() + 1);

				setIntentos(ctx, user);

				if (finish)
					session.commit();
			}

			rollback = false;

			throw new BaseException(new Exception("Login as " + login + " failed "), LOG, BaseException.LOGIN_FAIL);
		}
		catch (Throwable e)
		{
			if (finish && rollback)
				session.rollback();

			if (e instanceof BaseException)
				throw (BaseException) e;

			throw new BaseException(e, LOG, 0);
		}
		finally
		{
			if (finish)
				ctx.closeSession();
		}
	}
}
