package es.pryades.smartswitch.common;

import java.io.InputStream;

public interface ExcelExporter
{
	InputStream getExcelStream();
	String getFileName();
}
