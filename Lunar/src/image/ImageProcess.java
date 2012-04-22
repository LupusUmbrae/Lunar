package image;

import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.ArrayUtils;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

public class ImageProcess {

	private PaletteProcess palette;

	private Integer pixelHeight;
	private Integer pixelWidth;

	private Double pixelLat;
	private Double pixelLon;

	private final Double LAT_MAX = 90d;
	private final Double LAT_MIN = -90d;
	private final Double LON_MAX = 180d;
	private final Double LON_MIN = -180d;

	private final String OUT_DIR = "c:\\temp";

	private MultiKeyMap heightMap;

	private Raster image;

	private ArrayList<File> pixelFiles = new ArrayList<File>();
	private ArrayList<File> rowFiles = new ArrayList<File>();

	public ImageProcess(PaletteProcess palette) {
		this.palette = palette;
	}

	public void generateData(String filename, Connection conn) throws Exception {
		loadImage(filename);
		// convertImage();
		loadConvertImage();
		//convertRgb();
		fillDb(conn);
	}

	private void loadImage(String filename) throws IOException {
		heightMap = new MultiKeyMap();
		File file = new File(filename);

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);

		image = dec.decodeAsRaster();
		pixelHeight = image.getHeight();
		pixelWidth = image.getWidth();

	}

	// Convert
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
			File file = new File(OUT_DIR + "\\pixel" + y + ".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (Integer[] pixel : row) {
				out.write(String.format("%d,%d,%d\n", pixel[0], pixel[1],
						pixel[2]));
			}
			out.close();
			pixelFiles.add(file);
		}
		System.out.println("Picture to text complete");
	}

	private void loadConvertImage() {
		File filePlace = new File(OUT_DIR + "//");
		String[] files = filePlace.list(new Filter(".txt"));
		for (String file : files) {
			rowFiles.add(new File(OUT_DIR + "//" + file));
		}
	}

	private void convertRgb() throws Exception {
		int latPos = 0;
		int lonPos = 0;
		pixelLat = 180d / pixelHeight.doubleValue();
		pixelLon = 360d / pixelWidth.doubleValue();

		for (File pixelFile : pixelFiles) {
			// +1 just to make it the same numbering as pixel files
			File heightFile = new File(OUT_DIR + "\\row" + (latPos + 1)
					+ ".txt");
			BufferedReader in = new BufferedReader(new FileReader(pixelFile));
			BufferedWriter out = new BufferedWriter(new FileWriter(heightFile));
			String line = null;
			line = in.readLine();
			while (line != null) {
				String[] rgbString = line.split(",");
				Double lat = LAT_MAX - (pixelLat * latPos);
				Double lon = LON_MIN + (pixelLon * lonPos);
				Double height = palette.convertRgb(rgbString);
				out.write(String.format("%s,%s,%s\n", lat.toString(), lon.toString(), height.toString()));
				line = in.readLine();
				lonPos++;
			}
			in.close();
			out.close();
			lonPos = 0;
			latPos++;
		}

	}

	private void fillDb(Connection conn) throws IOException, SQLException {
		for (File row : rowFiles) {
			BufferedReader in = new BufferedReader(new FileReader(row));
			String line = in.readLine();
			Statement stmt = conn.createStatement();
			String stmtBuilder = "INSERT INTO base_data(LAT, LON, HEIGHT) VALUES ";
			while (line != null) {
				line = in.readLine();
				if(line != null){
					String[] item = line.split(",");
					stmtBuilder += String.format("(%s, %s, %s),", item[0],
							item[1], item[2]);
				}
				
			}
			stmtBuilder = stmtBuilder.substring(0, stmtBuilder.length()-1);
			stmtBuilder += ";";
			stmt.execute(stmtBuilder);
		}
		System.out.println("dbFilled");
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

}
