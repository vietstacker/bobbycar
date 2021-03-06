package com.redhat.bobbycar.carsim.routes;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.bobbycar.carsim.gpx.GpxReader;

public abstract class FileBasedRouteSelector implements RouteSelectionStrategy{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedRouteSelector.class);
	
	GpxReader gpxReader;
	
	File[] gpxFiles;
	
	FileBasedRouteSelector(GpxReader gpxReader, String pathToRoutes) {
		gpxFiles = readRoutes(pathToRoutes);
		this.gpxReader = gpxReader;
	}
	
	private File[] readRoutes(String pathToRoutes) {
		LOGGER.info("Collecting routes from {}", pathToRoutes);
		File file = new File(pathToRoutes);
		File[] gpx;
        
		if (file.exists() && file.isDirectory()) {
        	gpx = file.listFiles(f -> "gpx".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
        	LOGGER.info("Found {} routes in directory", gpx.length);
        } else if(file.exists() && !file.isDirectory()) {
        	gpx = new File[] {file};
        	LOGGER.info("Route is single file");
        } else {
        	LOGGER.error("File does not exist");
        	gpx = new File[]{};
        }
        return gpx;
	}
	
	@Override
	public Route selectRoute() throws JAXBException, IOException {
		return gpxReader.readGpx(nextRoute());
	}
	
	abstract File nextRoute();
	
}
