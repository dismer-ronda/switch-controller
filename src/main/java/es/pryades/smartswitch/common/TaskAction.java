package es.pryades.smartswitch.common;

import es.pryades.smartswitch.dto.Task;

public interface TaskAction 
{
	public void doTask( AppContext ctx, Task task ) throws BaseException;
	public CommonEditor getTaskDataEditor( AppContext context );
	public boolean isUserEnabledForTask( AppContext context );
}
