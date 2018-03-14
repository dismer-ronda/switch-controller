package es.pryades.smartswitch.dal;

import org.apache.log4j.Logger;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.dal.ibatis.FacilityInterruptorMapper;
import es.pryades.smartswitch.dto.FacilityInterruptor;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public class FacilityInterruptorsManagerImpl extends BaseManagerImpl implements FacilityInterruptorsManager
{
	private static final long serialVersionUID = -6572548173028040266L;
	
	private static final Logger LOG = Logger.getLogger( FacilityInterruptorsManagerImpl.class );

	public static BaseManager build()
	{
		return new FacilityInterruptorsManagerImpl();
	}

	public FacilityInterruptorsManagerImpl()
	{
		super( FacilityInterruptorMapper.class, FacilityInterruptor.class, LOG );
	}

	@Override
	public boolean hasUniqueId( AppContext ctx ) 
	{
		return false;
	}
}
