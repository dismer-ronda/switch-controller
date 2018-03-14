package es.pryades.smartswitch.tasks;

import java.io.Serializable;

import lombok.Data;

@Data
public class DatabaseUpdateTaskData implements Serializable
{
	private static final long serialVersionUID = -7460651698096459685L;
	
	private String driver;
	private String url;
	private String user;
	private String password;
	private String sql;
	private Long ref_user;
}
