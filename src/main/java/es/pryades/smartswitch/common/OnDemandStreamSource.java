package es.pryades.smartswitch.common;

import com.vaadin.server.StreamResource.StreamSource;

public interface OnDemandStreamSource extends StreamSource
{
	boolean prepareDownload( OnDemandFileDownloader downloader );
	String getFilename();
}