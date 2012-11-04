package org.moss.lunar.map;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import de.micromata.opengis.kml.v_2_2_0.GroundOverlay;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;

public class CreateKml {

	private String iconHrefPrefix = "files/";
	private String kmzExtension = ".kmz";
	private String kmlFilename = "doc.kml";

	public void CreateKmz(String kmzName, File image)
			throws FileNotFoundException {
		File kmlFile;
		String imageName = image.getName();

		kmlFile = createKml(imageName);

		String outFilename = "resources/" + kmzName + kmzExtension;
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				outFilename));
		try {
			byte[] imageBytes;
			byte[] kmlBytes;
			out.putNextEntry(new ZipEntry("files/" + imageName));
			imageBytes = FileUtils.readFileToByteArray(image);
			out.write(imageBytes);
			out.putNextEntry(new ZipEntry(kmlFilename));
			kmlBytes = FileUtils.readFileToByteArray(kmlFile);
			out.write(kmlBytes);
			out.closeEntry();

			out.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File createKml(String imageName) throws FileNotFoundException {

		File kmlFile = new File(kmlFilename);
		Kml kml = KmlFactory.createKml().withHint("target=moon");
		Icon icon = (Icon) new Icon();
		GroundOverlay groundoverlay = kml.createAndSetGroundOverlay();

		groundoverlay.withName("Lunar").withColor("3bffffff");

		groundoverlay.createAndSetLatLonBox().withNorth(90d).withSouth(-90d)
				.withEast(180d).withWest(-180d);

		icon.withHref(iconHrefPrefix + imageName).withViewBoundScale(0.75d);
		groundoverlay.setIcon(icon);

		kml.marshal(kmlFile);

		return kmlFile;

	}
}
