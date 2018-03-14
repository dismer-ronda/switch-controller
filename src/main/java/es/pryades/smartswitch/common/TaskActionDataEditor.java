package es.pryades.smartswitch.common;

public interface TaskActionDataEditor
{
	public Object getComponent( String data, boolean readOnly );
	public String getTaskData() throws BaseException;
	public String isValidInput(); 
}
