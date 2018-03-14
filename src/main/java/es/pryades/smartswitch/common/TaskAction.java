package es.pryades.smartswitch.common;

import es.pryades.smartswitch.dto.Task;
import es.pryades.smartswitch.reports.CommonEditor;

public interface TaskAction 
{
	public void doTask( AppContext ctx, Task task ) throws BaseException;
	public CommonEditor getTaskDataEditor( AppContext context );
	public boolean isUserEnabledForTask( AppContext context );
}
