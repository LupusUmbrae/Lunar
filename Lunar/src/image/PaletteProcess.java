package image;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.map.MultiKeyMap;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

////String path = "git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
//String path = "C:\\Users\\berrimang\\git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
//int totalElevation = 19910;
//int startElevationValue	= -9150;
//
//PaletteProcess palette = new PaletteProcess();
//palette.createPalette(path, totalElevation, startElevationValue);

public class PaletteProcess {

	private MultiKeyMap evaluationMap = new MultiKeyMap();
	private HashMap<Integer, ArrayList<Integer>> redMap = new HashMap<Integer, ArrayList<Integer>>();
	private MultiKeyMap greenMap = new MultiKeyMap();

	public PaletteProcess() throws IOException {

	}

	// Read in the tiff file
	public void createPalette(String filename, Double totalElevation,
			Double startElevationValue) throws IOException {

		// int totalElevation = 19910;
		// int startElevationValue = -9150;

		File file = new File(filename);
		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);
		Raster image = dec.decodeAsRaster();

		// Find the number of pixels.
		int maxYPixel = image.getHeight();
		Double elevationIncrement = totalElevation / maxYPixel;

		// get the rgb value of each pixel and allocate an elevation to it.
		Double i = startElevationValue;
		for (int y = 0; y < maxYPixel; y++) {
			int[] rgb = null;
			rgb = image.getPixel(0, y, rgb);

			evaluationMap.put(rgb[0], rgb[1], rgb[2], i);

			ArrayList<Integer> greens;
			ArrayList<Integer> blues;

			if (redMap.containsKey(rgb[0])) {
				greens = redMap.get(rgb[0]);
				greens.add(rgb[1]);
			} else {
				greens = new ArrayList<Integer>();
				greens.add(rgb[1]);
			}
			redMap.put(rgb[0], greens);

			if (greenMap.containsKey(rgb[0], rgb[1])) {
				blues = (ArrayList<Integer>) greenMap.get(rgb[0], rgb[1]);
				blues.add(rgb[2]);
			} else {
				blues = new ArrayList<Integer>();
				blues.add(rgb[2]);
			}
			greenMap.put(rgb[0], rgb[1], blues);

			i = i + elevationIncrement;
		}

	}

	public Double convertRgb(String[] rgbString) throws Exception {
		int red = Integer.parseInt(rgbString[0]);
		int green = Integer.parseInt(rgbString[1]);
		int blue = Integer.parseInt(rgbString[2]);
		if (evaluationMap.containsKey(red, green, blue)) {
			return (Double) evaluationMap.get(red, green, blue);
		} else {
			red = findRed(red);
			green = findGreen(red, green);
			blue = findBlue(red, green, blue);
		}
		
		if (evaluationMap.containsKey(red, green, blue)) {
			return (Double) evaluationMap.get(red, green, blue);
		} else {
			throw new Exception("It thought it found a height but apparently it hadnt :S");
		}
	}

	private int findRed(int red) {

		int runningRed = red;

		while (runningRed <= 255) {
			runningRed++;
			if (redMap.containsKey(runningRed)) {
				return runningRed;
			}
		}

		while (runningRed >= 0) {
			runningRed--;
			if (redMap.containsKey(runningRed)) {
				return runningRed;
			}
		}
		return 0;
	}

	private int findGreen(int red, int green) throws Exception {
		ArrayList<Integer> greens = redMap.get(red);
		int runningGreen = green;
		int greenDiff = 255;

		// Find closest Match
		for (Integer greenMatch : greens) {
			int diff;
			if (greenMatch < runningGreen) {
				diff = runningGreen - greenMatch;
			} else {
				diff = greenMatch - runningGreen;
			}
			if (greenDiff > diff) {
				runningGreen = greenMatch;
				greenDiff = diff;
			}
		}
		if (runningGreen == green) {
			throw new Exception("It broke trying to find green");
		}
		return runningGreen;
	}

	private int findBlue(int red, int green, int blue) throws Exception {
		ArrayList<Integer> blues = (ArrayList<Integer>) greenMap.get(red, green);
		int runningBlue = blue;
		int blueDiff = 255;

		// Find closest Match
		for (Integer blueMatch : blues) {
			int diff;
			if (blueMatch < runningBlue) {
				diff = runningBlue - blueMatch;
			} else {
				diff = blueMatch - runningBlue;
			}
			if (blueDiff > diff) {
				runningBlue = blueMatch;
				blueDiff = diff;
			}
		}
		if (runningBlue == blue) {
			throw new Exception("It broke trying to find a blue");
		}
		return runningBlue;
	}

}
