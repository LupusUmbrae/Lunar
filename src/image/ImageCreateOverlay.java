package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.DataTile;

public class ImageCreateOverlay {

	private final String OUT_DIR = "c:\\temp";

	public void createOverlay(ArrayList<ArrayList<DataTile>> dataTiles)
			throws IOException {
		File outputImage = new File(OUT_DIR + "\\lunar.png");

		BufferedImage image = new BufferedImage(dataTiles.get(1).size(),
				dataTiles.size(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < dataTiles.size(); x++) {
			ArrayList<DataTile> dataTiles2 = dataTiles.get(x);
			for (int y = 0; y < dataTiles2.size(); y++) {
				DataTile dataTile = dataTiles2.get(y);
				int rank = dataTile.getRank();
				image.setRGB(x, y, getRgb(rank));
			}
		}

		ImageIO.write(image, "png", outputImage);
	}

	public int getRgb(int rank) {
		String rgbString = "";
		Integer rgb = 0;
		int red = (int) (255 - (255 * (rank / 100d)));
		int blue = (int) (255 * (rank / 100d));

		rgbString += String.format("%x00%x", red, blue);
		rgb = Integer.parseInt(rgbString, 16);
		return rgb;
	}
}
