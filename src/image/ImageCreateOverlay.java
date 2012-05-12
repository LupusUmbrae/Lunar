package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.DataTile;

public class ImageCreateOverlay {

	private final String OUT_DIR = "c:\\temp";

	public void createOverlay(ArrayList<DataTile> dataTiles, int latStep,
			int lonStep) throws IOException {
		File outputImage = new File(OUT_DIR + "\\lunar.png");
		int imageWidth = 360 / lonStep;
		int imageHeight = 180 / latStep;

		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		for (DataTile dataTile : dataTiles) {
			int rank = dataTile.getRank();
			int x = (dataTile.getLon().intValue() + 180) / lonStep;
			int y = (dataTile.getLat().intValue() + 90) / latStep;
			image.setRGB(x, y, getRgb(rank));
		}

		ImageIO.write(image, "png", outputImage);
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
