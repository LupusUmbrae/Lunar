package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageProcess {

	public ImageProcess() {

	}

	private void loadImage(String filename) throws IOException {

		File file = new File(filename);

		BufferedImage image = ImageIO.read(file);
		image.getHeight();

	}
}
