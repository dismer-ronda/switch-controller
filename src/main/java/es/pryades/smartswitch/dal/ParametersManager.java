package es.pryades.smartswitch.dal;

import java.util.HashMap;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.dto.Parameter;


/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface ParametersManager extends BaseManager
{
	public HashMap<Long, Parameter> getParameters( AppContext ctx );
	public void loadParameters( AppContext ctx );
}
