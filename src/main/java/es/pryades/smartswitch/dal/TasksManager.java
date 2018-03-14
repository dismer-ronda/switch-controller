package es.pryades.smartswitch.dal;

import es.pryades.smartswitch.common.AppContext;
import es.pryades.smartswitch.common.TaskAction;
import es.pryades.smartswitch.dto.Task;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/
public interface TasksManager extends BaseManager
{
	public TaskAction getTaskAction( Task task );
	public TaskAction getTaskAction( int task );
	public void doTask( AppContext ctx, Task task, boolean forced );
}
