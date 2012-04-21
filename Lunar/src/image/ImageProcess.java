package image;

import java.awt.image.Raster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.ArrayUtils;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

public class ImageProcess {

	private int pixelHeight;
	private int pixelWidth;

	private MultiKeyMap heightMap;

	private Raster image;

	public ImageProcess(String filename) throws IOException {
		loadImage(filename);
		convertImage();
	}

	private void loadImage(String filename) throws IOException {

		File file = new File(filename);

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);

		image = dec.decodeAsRaster();
		pixelHeight = image.getHeight();
		pixelWidth = image.getWidth();

	}

	private void convertImage() throws IOException {

		heightMap = new MultiKeyMap();
		for (int y = 0; y < pixelHeight; y++) {
			ArrayList<Integer[]> row = new ArrayList<Integer[]>();
			for (int x = 0; x < pixelWidth; x++) {

				int[] rgb = null;

				rgb = image.getPixel(x, y, rgb);

				row.add(ArrayUtils.toObject(rgb));
			}
			// Write row to disc
			File file = new File("c:\\temp\\row" + y + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (Integer[] pixel : row) {
				out.write(String.format("%d,%d,%d\n", pixel[0], pixel[1], pixel[2]));
			}
			out.close();
		}
		System.out.println("hi");
	}
}
