package es.pryades.smartswitch.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
*
* @author dismer.ronda 
* @since 1.0.0.0
*/

@EqualsAndHashCode(callSuper=true)
@Data
public class UserDefault extends BaseDto
{
	private static final long serialVersionUID = -7744392550886070713L;

	public static final String MANUFACURING_DEVICE = "manufacturing.device";
	public static final String MANUFACURING_REVISION = "manufacturing.revision";
	public static final String MANUFACURING_SERIAL = "manufacturing.serial";
	
	public static final String LAST_PASSWORD1 = "last.password.1";
	public static final String LAST_PASSWORD2 = "last.password.2";
	
	public static final String FLEET_WIDGET_FROM = "fleet.widget.from";
	public static final String FLEET_WIDGET_TO = "fleet.widget.to";
	public static final String FLEET_WIDGET_REGION = "fleet.widget.region";
	public static final String FLEET_WIDGET_PLANT = "fleet.widget.plant";
	public static final String FLEET_WIDGET_DEVICE = "fleet.widget.device";
	public static final String FLEET_WIDGET_HOSPITAL = "fleet.widget.hospital";

	public static final String DUTIES_WIDGET_FROM = "duties.widget.from";
	public static final String DUTIES_WIDGET_TO = "duties.widget.to";
	public static final String DUTIES_WIDGET_REGION = "duties.widget.region";
	public static final String DUTIES_WIDGET_PLANT = "duties.widget.plant";
	public static final String DUTIES_WIDGET_DEVICE = "duties.widget.device";
	public static final String DUTIES_WIDGET_HOSPITAL = "duties.widget.hospital";

	public static final String DELIVERY_TIME_WIDGET_FROM = "deliver.time.widget.from";
	public static final String DELIVERY_TIME_WIDGET_TO = "deliver.time.widget.to";
	public static final String DELIVERY_TIME_WIDGET_REGION = "deliver.time.widget.region";
	public static final String DELIVERY_TIME_WIDGET_PLANT = "deliver.time.widget.plant";
	public static final String DELIVERY_TIME_WIDGET_DEVICE = "deliver.time.widget.device";
	public static final String DELIVERY_TIME_WIDGET_HOSPITAL = "deliver.time.widget.hospital";

	public static final String TIME_FIRST_USE_WIDGET_FROM = "time.first.use.widget.from";
	public static final String TIME_FIRST_USE_WIDGET_TO = "time.first.use.widget.to";
	public static final String TIME_FIRST_USE_WIDGET_REGION = "time.first.use.widget.region";
	public static final String TIME_FIRST_USE_WIDGET_PLANT = "time.first.use.widget.plant";
	public static final String TIME_FIRST_USE_WIDGET_DEVICE = "time.first.use.widget.device";
	public static final String TIME_FIRST_USE_WIDGET_HOSPITAL = "time.first.use.widget.hospital";

	public static final String RETURN_PRESSURE_WIDGET_FROM = "return.pressure.widget.from";
	public static final String RETURN_PRESSURE_WIDGET_TO = "return.pressure.widget.to";
	public static final String RETURN_PRESSURE_WIDGET_REGION = "return.pressure.widget.region";
	public static final String RETURN_PRESSURE_WIDGET_PLANT = "return.pressure.widget.plant";
	public static final String RETURN_PRESSURE_WIDGET_DEVICE = "return.pressure.widget.device";
	public static final String RETURN_PRESSURE_WIDGET_HOSPITAL = "return.pressure.widget.hospital";

	public static final String TREATMENTS_WIDGET_FROM = "treatments.widget.from";
	public static final String TREATMENTS_WIDGET_TO = "treatments.widget.to";
	public static final String TREATMENTS_WIDGET_REGION = "treatments.widget.region";
	public static final String TREATMENTS_WIDGET_PLANT = "treatments.widget.plant";
	public static final String TREATMENTS_WIDGET_DEVICE = "treatments.widget.device";
	public static final String TREATMENTS_WIDGET_HOSPITAL = "treatments.widget.hospital";

	public static final String POWER_CONSUMPTION_WIDGET_FROM = "power.consumption.widget.from";
	public static final String POWER_CONSUMPTION_WIDGET_TO = "power.consumption.widget.to";
	public static final String POWER_CONSUMPTION_WIDGET_REGION = "power.consumption.widget.region";
	public static final String POWER_CONSUMPTION_WIDGET_PLANT = "power.consumption.widget.plant";
	public static final String POWER_CONSUMPTION_WIDGET_DEVICE = "power.consumption.widget.device";
	public static final String POWER_CONSUMPTION_WIDGET_HOSPITAL = "power.consumption.widget.hospital";

	public static final String POWER_REMAINING_WIDGET_FROM = "power.remaining.widget.from";
	public static final String POWER_REMAINING_WIDGET_TO = "power.remaining.widget.to";
	public static final String POWER_REMAINING_WIDGET_REGION = "power.remaining.widget.region";
	public static final String POWER_REMAINING_WIDGET_PLANT = "power.remaining.widget.plant";
	public static final String POWER_REMAINING_WIDGET_DEVICE = "power.remaining.widget.device";
	public static final String POWER_REMAINING_WIDGET_HOSPITAL = "power.remaining.widget.hospital";

	private String data_key;
	private String data_value;
	
	private Long ref_user;
	
	public Long asLong()
	{
		try
		{
			return Long.valueOf( data_value );
		}
		catch ( Throwable e )
		{
			return null;
		}
	}
}
