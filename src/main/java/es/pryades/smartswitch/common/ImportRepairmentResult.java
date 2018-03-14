package es.pryades.smartswitch.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportRepairmentResult  implements Serializable
{
	private static final long serialVersionUID = 8771403641366972658L;
	
	private int candidates;
	private int created;
	private int updated;
	private int failed;

	public boolean success()
	{
		return created + updated == candidates;
	}
}
