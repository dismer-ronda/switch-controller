package es.pryades.smartswitch.common;

import org.apache.log4j.Logger;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.style.FontWeight;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Style;
import com.vaadin.addon.charts.util.SVGGenerator;

import es.pryades.smartswitch.dto.Parameter;

public class Charts 
{
    @SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger( Charts.class );
    
	public static Chart prepareDonutsChart( DataSeries dataSeries )
	{
		Configuration config = new Configuration();
		
		Style style = new Style();
		style.setFontSize( Constants.CHART_FONT_SIZE );
		style.setFontWeight( FontWeight.BOLD );

		config.getTitle().setStyle( style );
		config.getCredits().setEnabled( false );
		
		Chart chart = new Chart( ChartType.PIE );
		
		chart.setConfiguration( config );

		chart.setWidth( "100%" );
		
		Labels dataLabels = new Labels();
		dataLabels.setEnabled( true );
		dataLabels.setColor( new SolidColor( 0, 0, 0 ) );
		dataLabels.setConnectorColor( new SolidColor( 0, 0, 0 ) );
		dataLabels.setStyle( style );

		PlotOptionsPie outerSeriesOptions = new PlotOptionsPie();
    	outerSeriesOptions.setShowInLegend( false );
		outerSeriesOptions.setInnerSize("60%");
		outerSeriesOptions.setDataLabels( dataLabels );
		outerSeriesOptions.setAnimation( false );

		dataSeries.setPlotOptions( outerSeriesOptions );

		Tooltip t = new Tooltip();
		t.setEnabled( false );
		chart.getConfiguration().setTooltip( t );

		chart.getConfiguration().setSeries( dataSeries );

		return chart;
	}
	
	public static void exportChartToPng( AppContext ctx, Chart chart, String fileName )
	{
		String svg = SVGGenerator.getInstance().generate( chart.getConfiguration(), ctx.getIntegerParameter( Parameter.PAR_CHARTS_WIDTH ), ctx.getIntegerParameter( Parameter.PAR_CHARTS_HEIGHT ) );
		Utils.writeFile( fileName + ".svg", svg, false );
		Utils.cmdExec( "rsvg " + fileName + ".svg " + fileName + ".png" );
	}
}
