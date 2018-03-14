package es.pryades.smartswitch.dal.ibatis;

import es.pryades.smartswitch.common.BaseException;
import es.pryades.smartswitch.dto.Right;
import es.pryades.smartswitch.dto.User;
import es.pryades.smartswitch.dto.query.RightQuery;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

public interface UserMapper extends BaseMapper
{
    public void setPassword( User user );
    public void setRetries( User user );
    public void setStatus( User user );
    
    public Right getRight( RightQuery query ) throws BaseException;
}
