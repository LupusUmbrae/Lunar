package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import main.DataTile;

public class ImageCreateOverlay {

	private final String OUT_DIR = "resources";

	public File createOverlay(ArrayList<DataTile> dataTiles, float latStep,
			float lonStep) throws IOException {
		File outputImage = new File(OUT_DIR + "\\lunar_" + System.currentTimeMillis() + ".png");
		int imageWidth = (int) (360 / lonStep);
		int imageHeight = (int) (180 / latStep);

		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		for (DataTile dataTile : dataTiles) {
			int rank = dataTile.getRank();
			int x = (int) ((dataTile.getLon().intValue() + 180) / lonStep);
			int y = (int) ((dataTile.getLat().intValue() + 90) / latStep);
			image.setRGB(x, y, getRgb(rank));
		}
		ImageIO.write(image, "png", outputImage);
		return outputImage;
	}

	private int getRgb(int rank) {
		rank = rank < 0 ? 0 : rank;
		String rgbString = "";
		Integer rgb = 0;
		int red = (int) (255 - (255 * (rank / 100d)));
		int blue = (int) (255 * (rank / 100d));

		rgbString += String.format("%x00%x", red, blue);
		rgb = Integer.parseInt(rgbString, 16);
		return rgb;
	}

}
