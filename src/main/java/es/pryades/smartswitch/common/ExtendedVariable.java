package es.pryades.smartswitch.common;

import java.util.HashMap;
import java.util.List;

import com.vaadin.addon.charts.model.DataSeriesItem;

public interface ExtendedVariable 
{
	List<DataSeriesItem> getDataSeries( HashMap<String, List<DataSeriesItem>> chartSeries );
}
