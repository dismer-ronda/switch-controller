package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.dal.ibatis.InterruptorMapper;
import es.pryades.smartswitch.dto.Interruptor;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class InterruptorsManagerImpl extends BaseManagerImpl implements InterruptorsManager
{
	private static final long serialVersionUID = 1692255668674020978L;
	
	private static final Logger LOG = Logger.getLogger( InterruptorsManagerImpl.class );

	public static BaseManager build()
	{
		return new InterruptorsManagerImpl();
	}

	public InterruptorsManagerImpl()
	{
		super( InterruptorMapper.class, Interruptor.class, LOG );
	}
}
