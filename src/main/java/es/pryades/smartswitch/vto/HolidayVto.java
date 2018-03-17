package es.pryades.smartswitch.vto;

import es.pryades.smartswitch.common.GenericVto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Dismer Ronda
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class HolidayVto extends GenericVto
{
	private static final long serialVersionUID = -4312525548242840886L;

	private String holiday_name;

	private String holiday_type;
	private String holiday_value;

	public HolidayVto()
	{
	}
}
