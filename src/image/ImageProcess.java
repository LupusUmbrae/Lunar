package image;

import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import main.Lunar;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

import database.threads.EnterBaseDataThread;

public class ImageProcess {

	private PaletteProcess palette;

	private Integer pixelHeight;
	private Integer pixelWidth;

	private Double pixelLat;
	private Double pixelLon;

	private Raster image;

	private static ArrayList<File> pixelFiles = new ArrayList<File>();
	// private static ArrayList<File> rowFiles = new ArrayList<File>();

	private static BlockingQueue<String> statements = new LinkedBlockingQueue<String>();
	private static BlockingQueue<File> rowFiles = new LinkedBlockingQueue<File>();

	public ImageProcess(PaletteProcess palette) {
		this.palette = palette;
	}

	public void generateData(String filename) throws Exception {
		createDirectories();
		loadImage(filename);
		// convertImage();
		loadConvertImage();
		// convertRgb();
		fillDb();
	}

	/**
	 * 
	 */
	private void createDirectories() {
		// TODO: Do some checks here
		File toCreate = new File(Lunar.OUT_DIR + Lunar.PIXEL_DIR + Lunar.PROCESSED_DIR);
		toCreate.mkdirs();
		toCreate = null;
		toCreate = new File(Lunar.OUT_DIR + Lunar.ROW_DIR + Lunar.PROCESSED_DIR);
		toCreate.mkdirs();
		toCreate = null;
	}

	private void loadImage(String filename) throws IOException {
		File file = new File(filename);

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);

		image = dec.decodeAsRaster();
		pixelHeight = image.getHeight();
		pixelWidth = image.getWidth();
		System.out.println("Image loaded");

	}

	// Convert
	private void convertImage() throws IOException {
		// TODO: Thread this
		for (int y = 0; y < pixelHeight; y++) {
			ArrayList<Integer[]> row = new ArrayList<Integer[]>();
			for (int x = 0; x < pixelWidth; x++) {

				int[] rgb = null;

				rgb = image.getPixel(x, y, rgb);

				row.add(ArrayUtils.toObject(rgb));
			}
			// Write row to disc
			File file = new File(Lunar.OUT_DIR + "\\pixel" + y + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (Integer[] pixel : row) {
				out.write(String.format("%d,%d,%d\n", pixel[0], pixel[1],
						pixel[2]));
			}
			out.close();
			pixelFilesAdd(file);
		}
		System.out.println("Picture to text complete");
	}

	private void loadConvertImage() {
		File filePlace = new File(Lunar.OUT_DIR + Lunar.ROW_DIR + "//");
		String[] files = filePlace.list(new Filter(".txt"));
		for (String file : files) {
			rowFilesAdd(new File(Lunar.OUT_DIR + Lunar.ROW_DIR + "//" + file));
		}
		files = null;
	}

	private void convertRgb() throws Exception {
		// TODO: Thread this
		int latPos = 0;
		int lonPos = 0;
		pixelLat = 180d / pixelHeight.doubleValue();
		pixelLon = 360d / pixelWidth.doubleValue();

		for (int i = 0; i < pixelFilesSize(); i++) {
			File pixelFile = pixelFilesGet(i);
			// +1 just to make it the same numbering as pixel files
			File heightFile = new File(Lunar.OUT_DIR + "\\row" + (latPos + 1)
					+ ".txt");
			BufferedReader in = new BufferedReader(new FileReader(pixelFile));
			BufferedWriter out = new BufferedWriter(new FileWriter(heightFile));
			String line = null;
			line = in.readLine();
			while (line != null) {
				String[] rgbString = line.split(",");
				Double lat = Lunar.LAT_MAX - (pixelLat * latPos);
				Double lon = Lunar.LON_MIN + (pixelLon * lonPos);
				Double height = palette.convertRgb(rgbString);
				out.write(String.format("%s,%s,%s\n", lat.toString(),
						lon.toString(), height.toString()));
				line = in.readLine();
				lonPos++;
			}
			in.close();
			out.close();
			rowFilesAdd(heightFile);
			lonPos = 0;
			latPos++;
		}
		System.out.println("Pixels convert to raw data");
	}

	private void fillDb() throws IOException, SQLException,
			InterruptedException {
		File processedFileDir = new File(Lunar.OUT_DIR + Lunar.ROW_DIR + Lunar.PROCESSED_DIR);
		Thread baseData = new Thread(new EnterBaseDataThread(this));
		baseData.start();
		int rowFileSize = rowFilesSize();
		for (int i = 0; i < rowFileSize; i++) {
			File row = rowFilesPoll();
			BufferedReader in = new BufferedReader(new FileReader(row));
			String line = in.readLine();
			String stmtBuilder = "INSERT INTO base_data(LAT, LON, HEIGHT) VALUES ";
			while (line != null) {
				line = in.readLine();
				if (line != null) {
					String[] item = line.split(",");
					stmtBuilder += String.format("(%s, %s, %s),", item[0],
							item[1], item[2]);
				}

			}
			in.close();
			in = null;
			stmtBuilder = stmtBuilder.substring(0, stmtBuilder.length() - 1);
			stmtBuilder += ";";
			statementsPut(stmtBuilder);

			// Move file
			FileUtils.moveFileToDirectory(row, processedFileDir, false);
		}
		statementsPut("end");
		System.out.println("Database Filled");
	}

	private class Filter implements FilenameFilter {
		private String ext;

		public Filter(String ext) {
			this.ext = ext;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(ext);
		}
	}

	private void pixelFilesAdd(File file) {
		synchronized (pixelFiles) {
			pixelFiles.add(file);
		}
	}

	private void rowFilesAdd(File file) {
		synchronized (rowFiles) {
			rowFiles.add(file);
		}
	}

	private File pixelFilesGet(int index) {
		File file;
		synchronized (pixelFiles) {
			file = pixelFiles.get(index);
		}
		return file;
	}

	private File rowFilesPoll() {
		File file;
		synchronized (rowFiles) {
			file = rowFiles.poll();
		}
		return file;
	}

	private int pixelFilesSize() {
		int size;
		synchronized (pixelFiles) {
			size = pixelFiles.size();
		}
		return size;
	}

	private int rowFilesSize() {
		int size;
		synchronized (rowFiles) {
			size = rowFiles.size();
		}
		return size;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public String statementsTake() throws InterruptedException {
		String statement;
		synchronized (statements) {
			statement = statements.take();
		}
		return statement;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public String statementsPoll() throws InterruptedException {
		String statement;
		synchronized (statements) {
			statement = statements.poll();
		}
		return statement;
	}

	/**
	 * 
	 * @param person
	 * @throws InterruptedException
	 */
	protected void statementsPut(String statement) throws InterruptedException {
		synchronized (statements) {
			statements.put(statement);
		}
	}
}
