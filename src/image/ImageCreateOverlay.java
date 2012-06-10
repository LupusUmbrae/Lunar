package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.DataTile;

public class ImageCreateOverlay {

	private final String OUT_DIR = "resources";

	public File createOverlay(ArrayList<DataTile> dataTiles, float latStep,
			float lonStep) throws IOException {
		File outputImage = new File(OUT_DIR + "\\lunar_"
				+ System.currentTimeMillis() + ".png");
		int imageWidth = (int) (360 / lonStep);
		int imageHeight = (int) (180 / latStep);

		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_RGB);

		for (DataTile dataTile : dataTiles) {
			int rank = dataTile.getRank();
			Float lon = dataTile.getLon();
			Float lat = dataTile.getLat() * -1;
			if (lon > 179) {
				lon -= 180;
			} else {
				lon += 180;
			}
			int x = (int) ((lon.intValue()) / lonStep);
			int y = (int) ((lat.intValue() + 89) / latStep);
			try {
				image.setRGB(x, y, getRgb(rank));
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("x: " + x + ", Y: " + y);
				break;
			}

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
		String redString = String.format("%x", red);
		String blueString = String.format("%x", blue);
		if(redString.length() == 1){
			redString = "0" + redString;
		}
		
		if(blueString.length() == 1){
			blueString = "0" + blueString;
		}
		
		rgbString += redString + "00" + blueString;
		rgb = Integer.parseInt(rgbString, 16);
		return rgb;
	}

}
