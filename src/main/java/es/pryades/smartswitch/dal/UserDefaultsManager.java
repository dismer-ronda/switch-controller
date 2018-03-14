package es.pryades.smartswitch.dal;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.dto.UserDefault;



/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface UserDefaultsManager extends BaseManager
{
	UserDefault getUserDefault( AppContext context, String key );
	void setUserDefault( AppContext context, UserDefault def, String value );
}
