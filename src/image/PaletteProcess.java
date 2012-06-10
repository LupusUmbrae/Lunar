package image;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.ArrayUtils;

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
	public void createPalette(String filename, Float totalElevation,
			Float startElevationValue) throws IOException {

		// int totalElevation = 19910;
		// int startElevationValue = -9150;

		File file = new File(filename);
		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);
		Raster image = dec.decodeAsRaster();

		// Find the number of pixels.
		int maxYPixel = image.getHeight();
		Float elevationIncrement = totalElevation / maxYPixel;

		// get the rgb value of each pixel and allocate an elevation to it.
		Float i = startElevationValue;
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

	public Float convertRgb(String[] rgbString) throws Exception {
		int red = Integer.parseInt(rgbString[0]);
		int green = Integer.parseInt(rgbString[1]);
		int blue = Integer.parseInt(rgbString[2]);
		if (evaluationMap.containsKey(red, green, blue)) {
			return (Float) evaluationMap.get(red, green, blue);
		} else {
			return findCloesetRgb(red, green, blue);
		}

		// if (evaluationMap.containsKey(red, green, blue)) {
		// return (Double) evaluationMap.get(red, green, blue);
		// } else {
		// throw new Exception(
		// "It thought it found a height but apparently it hadnt :S");
		// }
	}

	@SuppressWarnings("unchecked")
	private Float findCloesetRgb(int red, int green, int blue) {

		int runningRed = red;
		int runningGreen = green;
		int runningBlue = blue;

		int diffRed = 255;
		int diffGreen = 255;
		int diffBlue = 255;

		Set<Integer> possibleReds = redMap.keySet();

		// red, green, blue, diff red, diff green, diff blue
		ArrayList<Integer[]> matches = new ArrayList<Integer[]>();

		// Brute force.. early morning.. sleeping..
		for (Integer possibleRed : possibleReds) {

			ArrayList<Integer> possibleGreens = redMap.get(possibleRed);

			for (Integer possibleGreen : possibleGreens) {
				ArrayList<Integer> possibleBlues = (ArrayList<Integer>) greenMap
						.get(possibleRed, possibleGreen);

				for (Integer possibleBlue : possibleBlues) {
					if (evaluationMap.containsKey(possibleRed, possibleGreen,
							possibleBlue)) {
						matches.add(calcRgbDiff(red, green, blue, possibleRed,
								possibleGreen, possibleBlue));
					}
				}
			}
		}

		for (Integer[] match : matches) {
			if (match[3] < diffRed && match[4] < diffGreen
					&& match[5] < diffBlue) {
				runningRed = match[0];
				runningGreen = match[1];
				runningBlue = match[2];
				diffRed = match[3];
				diffGreen = match[4];
				diffBlue = match[5];
			}
		}

		return (Float) evaluationMap
				.get(runningRed, runningGreen, runningBlue);

		// boolean rgbFound = false;
		//
		// for (int i = 0; i < 255; i++) {
		// for (int j = 0; j <= i; j++) {
		// for (int k = 0; k <= j; k++) {
		// for (int l = 0; l <= k; l++) {
		// rgbFound = evaluationMap.containsKey(red + j, green + k, blue + l);
		// if (rgbFound) {
		// return (Float) evaluationMap.get(red + j, green + k, blue + l);
		// }
		//
		// rgbFound = evaluationMap.containsKey(red - j, green - k, blue - l);
		// if (rgbFound) {
		// return (Float) evaluationMap.get(red - j, green - k, blue - l);
		// }
		// }
		// }
		// }
		// }
		//
		// return 0f;

	}

	private Integer[] calcRgbDiff(int startRed, int startGreen, int startBlue,
			int possibleRed, int possibleGreen, int possibleBlue) {

		int diffRed;
		int diffGreen;
		int diffBlue;

		if (startRed > possibleRed) {
			diffRed = startRed - possibleRed;
		} else {
			diffRed = possibleRed - startRed;
		}

		if (startGreen > possibleGreen) {
			diffGreen = startGreen - possibleGreen;
		} else {
			diffGreen = possibleGreen - startGreen;
		}

		if (startBlue > possibleBlue) {
			diffBlue = startBlue - possibleBlue;
		} else {
			diffBlue = possibleBlue - startBlue;
		}

		int[] rgbArray = { possibleRed, possibleGreen, possibleBlue, diffRed,
				diffGreen, diffBlue };

		return ArrayUtils.toObject(rgbArray);
	}
}
