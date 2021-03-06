package com.redhat.bobbycar.carsim.gpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.redhat.bobbycar.carsim.routes.Route;
import com.redhat.bobbycar.carsim.routes.RoutePoint;

@ApplicationScoped
public class GpxReader {
	private Unmarshaller unmarshaller;
	
	public GpxReader() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class);
		unmarshaller = jaxbContext.createUnmarshaller();
	}

	public Route readGpx(File input) throws JAXBException, IOException {
		try (FileInputStream fis = new FileInputStream(input)) {
			return readGpx(fis);
		}
	}
	
	public Route readGpx(InputStream input) throws JAXBException {
			Source source = new StreamSource(input);
			JAXBElement<GpxType> element = unmarshaller.unmarshal(source, GpxType.class);
			GpxType gpx = element.getValue();
			return new Route(gpx.getMetadata() != null ? gpx.getMetadata().getName() : null, transform(gpx.getTrk()));
		
	}
	
	private List<RoutePoint> transform(List<TrkType> trks) {
		return trks.stream().flatMap(trk -> trk.getTrkseg().stream().flatMap(seg -> seg.getTrkpt().stream()))
				.map(trk -> new RoutePoint(trk.getLon(), trk.getLat(), trk.getEle(), trk.getTime() != null ? trk.getTime().toGregorianCalendar().toZonedDateTime() : null))
				.collect(Collectors.toList());
				
	}

}
