package org.moss.lunar.image;

import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.moss.lunar.Lunar;
import org.moss.lunar.database.threads.EnterBaseDataThread;
import org.moss.lunar.database.threads.ReadRowFilesThread;
import org.moss.lunar.image.palette.PaletteProcess;
import org.moss.lunar.image.threads.ConvertImage;
import org.moss.lunar.image.threads.ConvertRgb;
import org.moss.lunar.image.threads.ImageStorage;
import org.moss.lunar.types.exceptions.ListFullException;

import com.sun.istack.logging.Logger;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

/**
 * 
 * @author Robin
 * 
 */
public class ImageProcess
{
	
	private Logger logger = Logger.getLogger(ImageProcess.class);

	/**
	 * 
	 * @author Robin
	 * 
	 */
	private class Filter implements FilenameFilter
	{
		private String ext;

		public Filter(String ext)
		{
			this.ext = ext;
		}

		public boolean accept(File dir, String name)
		{
			return name.endsWith(this.ext);
		}
	}

	private PaletteProcess palette;
	private Integer pixelHeight;

	private Integer pixelWidth;
	private Float pixelLat;

	private Float pixelLon;

	private Raster image;

	public ImageProcess(PaletteProcess palette)
	{
		this.palette = palette;
	}

	/**
	 * PRocesses the given file and enters the values into the database
	 * 
	 * @param imageFile
	 *            absolute path to image file. Including image file name
	 * @throws Exception
	 */
	public void generateData(String imageFile) throws Exception
	{
		boolean old = false;
		this.loadImage(imageFile);

		this.pixelLat = 180f / this.pixelHeight.floatValue();
		this.pixelLon = 360f / this.pixelWidth.floatValue();

		Thread convertImage = new Thread(new ConvertImage(this.pixelHeight,
															this.pixelWidth,
															this.image));
		Thread convertRgb = new Thread(new ConvertRgb(pixelLon, pixelLat,
														palette));

		convertImage.start();
		convertRgb.start();
		
		while (convertImage.isAlive() || convertRgb.isAlive())
		{
			if(!convertImage.isAlive()){
				ConvertRgb.stopConvertRgb();
				logger.info("Stop command sent");
			}
			if(ImageStorage.heightRowIsFull()){
				ImageStorage.heightRowRemove(0);
			}
		}

		if (old)
		{
			this.createDirectories();
			this.loadImage(imageFile);
			// this.convertImage();
			this.loadConvertImage();
			this.convertRgb();

			Thread readFiles1 = new Thread(new ReadRowFilesThread());
			Thread readFiles2 = new Thread(new ReadRowFilesThread());
			Thread readFiles3 = new Thread(new ReadRowFilesThread());
			Thread readFiles4 = new Thread(new ReadRowFilesThread());

			Thread baseData1 = new Thread(new EnterBaseDataThread());
			Thread baseData2 = new Thread(new EnterBaseDataThread());
			Thread baseData3 = new Thread(new EnterBaseDataThread());
			Thread baseData4 = new Thread(new EnterBaseDataThread());

			readFiles1.start();
			readFiles2.start();
			readFiles3.start();
			readFiles4.start();

			baseData1.start();
			baseData2.start();
			baseData3.start();
			baseData4.start();

			while (readFiles1.isAlive() && readFiles2.isAlive()
					&& readFiles3.isAlive() && readFiles4.isAlive())
			{
				Thread.sleep(5000);
			}

			ImageStorage.setEndStatement(true);

			while (baseData1.isAlive() && baseData2.isAlive()
					&& baseData3.isAlive() && baseData4.isAlive())
			{
				Thread.sleep(5000);
			}
		}
		System.out.println("Database Filled");
	}

	/**
	 * 
	 * @throws IOException
	 * @throws ListFullException
	 */
	private void convertImage() throws IOException, ListFullException
	{
		// TODO: Thread this
		for (int y = 0; y < this.pixelHeight; y++)
		{
			ArrayList<Integer[]> row = new ArrayList<Integer[]>();
			for (int x = 0; x < this.pixelWidth; x++)
			{

				int[] rgb = null;

				rgb = this.image.getPixel(x, y, rgb);

				row.add(ArrayUtils.toObject(rgb));
			}
			// Write row to disc
			File file = new File(Lunar.OUT_DIR + Lunar.PIXEL_DIR + "\\pixel"
									+ y + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (Integer[] pixel : row)
			{
				out.write(String.format("%d,%d,%d\n", pixel[0], pixel[1],
										pixel[2]));
			}
			out.close();
			ImageStorage.pixelFilesAdd(file);
		}
		System.out.println("Picture to text complete");
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void convertRgb() throws Exception
	{
		// TODO: Thread this
		int latPos = 0;
		int lonPos = 0;
		this.pixelLat = 180f / this.pixelHeight.floatValue();
		this.pixelLon = 360f / this.pixelWidth.floatValue();

		File processedFileDir = new File(Lunar.OUT_DIR + Lunar.PIXEL_DIR
											+ Lunar.PROCESSED_DIR);

		for (int i = 0; i < ImageStorage.pixelFilesSize(); i++)
		{
			File pixelFile = ImageStorage.pixelFilesGet(i);
			// +1 just to make it the same numbering as pixel files
			File heightFile = new File(Lunar.OUT_DIR + Lunar.ROW_DIR + "\\row"
										+ (latPos + 1) + ".txt");
			BufferedReader in = new BufferedReader(new FileReader(pixelFile));
			BufferedWriter out = new BufferedWriter(new FileWriter(heightFile));
			String line = null;
			line = in.readLine();
			try
			{
				while (line != null)
				{
					String[] rgbString = line.split(",");
					Float lat = Lunar.LAT_MIN + (this.pixelLat * latPos);
					Float lon = Lunar.LON_MIN + (this.pixelLon * lonPos);
					Float height = this.palette.convertRgb(rgbString);
					out.write(String.format("%s,%s,%s\n", lat.toString(),
											lon.toString(), height));
					line = in.readLine();
					lonPos++;
					// FileUtils.moveFileToDirectory(pixelFile,
					// processedFileDir,
					// false);
				}
			} finally
			{
				in.close();
				out.close();
			}

			ImageStorage.rowFilesAdd(heightFile);
			lonPos = 0;
			latPos++;
		}
		System.out.println("Pixels convert to raw data");
	}

	/**
	 * Creates temporary filesystem directories for storing temporary file
	 */
	private void createDirectories()
	{
		// TODO: Do some checks here
		File toCreate = new File(Lunar.OUT_DIR + Lunar.PIXEL_DIR
									+ Lunar.PROCESSED_DIR);
		toCreate.mkdirs();
		toCreate = null;
		toCreate = new File(Lunar.OUT_DIR + Lunar.ROW_DIR + Lunar.PROCESSED_DIR);
		toCreate.mkdirs();
		toCreate = null;
	}

	private void loadConvertImage() throws ListFullException
	{
		File filePlace = new File(Lunar.OUT_DIR + Lunar.PIXEL_DIR + "//");
		String[] files = filePlace.list(new Filter(".txt"));
		for (String file : files)
		{
			ImageStorage.pixelFilesAdd(new File(Lunar.OUT_DIR + Lunar.PIXEL_DIR
												+ "//" + file));
		}
		System.out.println("Loaded files back in");
	}

	private void loadImage(String imageFile) throws IOException
	{
		File file = new File(imageFile);

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);

		this.image = dec.decodeAsRaster();
		this.pixelHeight = this.image.getHeight();
		this.pixelWidth = this.image.getWidth();
		System.out.println("Image loaded");

	}

}
