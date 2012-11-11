package org.moss.lunar.test.error;

import java.awt.image.Raster;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.moss.lunar.image.palette.PaletteProcess;
import org.moss.lunar.image.threads.ConvertImage;
import org.moss.lunar.image.threads.ConvertRgb;
import org.moss.lunar.image.threads.ImageStorage;
import org.moss.lunar.types.HeightDto;
import org.moss.lunar.types.RowDto;

import com.sun.istack.logging.Logger;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

public class NullHeightTest
{

	private Logger logger = Logger.getLogger(NullHeightTest.class);

	@Before
	public void setUp()
	{

	}

	@Test
	public void conversionTEst() throws Exception
	{
		Long found = 0l;
		Long notFound = 0l;

		Float pixelLon;
		Float pixelLat;
		Integer pixelHeight;
		Integer pixelWidth;

		Map<Float, Boolean> lats = new HashMap<Float, Boolean>();

		String path = "resources\\COLOR_SCALEBAR.TIF";
		Float totalElevation = 19910f;
		Float startElevationValue = -9150f;
		logger.info("Build Database Started");
		PaletteProcess palette = new PaletteProcess();
		palette.createPalette(path, totalElevation, startElevationValue);

		File file = new File("resources\\WAC_CSHADE_E000N1800_016P.TIF");

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);

		Raster image = dec.decodeAsRaster();
		pixelHeight = image.getHeight();
		pixelWidth = image.getWidth();

		pixelLat = 180f / pixelHeight.floatValue();
		pixelLon = 360f / pixelWidth.floatValue();

		Thread convertImage = new Thread(new ConvertImage(pixelHeight,
															pixelWidth, image));
		Thread convertRgb = new Thread(new ConvertRgb(pixelLon, pixelLat,
														palette));

		convertImage.start();
		convertRgb.start();

		while (convertImage.isAlive() || convertRgb.isAlive()
				|| ImageStorage.heightRowHasNext())
		{
			RowDto<HeightDto> rowDto;
			if (!convertImage.isAlive() && convertRgb.isAlive())
			{
				ConvertRgb.stopConvertRgb();
				logger.info("Stop command sent");
			}
			rowDto = ImageStorage.getheightRow();
			if (rowDto != null)
			{
				while (rowDto.hasMore())
				{
					HeightDto heightDto = rowDto.getNextItem();
					lats.put(heightDto.getLat(), true);
					if (heightDto.getHeight() == null)
					{
						notFound++;
					} else
					{
						found++;
					}
				}
				ImageStorage.heightRowRemove(0);
			}
		}
		System.out.println("Completed conversion with: \nFound: " + found
							+ "\nNot Found: " + notFound);
		for(Float lat : lats.keySet()){
			System.out.println(lat);
		}
	}

}
