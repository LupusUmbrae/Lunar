package org.moss.lunar;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.moss.lunar.database.BuildDataSets;
import org.moss.lunar.database.DbConnection;
import org.moss.lunar.image.ImageCreateOverlay;
import org.moss.lunar.image.ImageProcess;
import org.moss.lunar.image.palette.PaletteProcess;
import org.moss.lunar.map.CreateKml;
import org.moss.lunar.postprocess.PostProcessor;

import com.sun.istack.logging.Logger;

public class Lunar {

	public static final int LAT_MAX = 90;
	public static final int LAT_MIN = -90;
	public static final int LON_MAX = 360;
	public static final int LON_MIN = 0;

	public static final String OUT_DIR = "c:\\temp";
	public static final String PIXEL_DIR = "\\pixels";
	public static final String ROW_DIR = "\\row";
	public static final String PROCESSED_DIR = "\\processed";

	private static Logger logger = Logger.getLogger(Lunar.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		logger.info("Lunar starting");
		try {
			boolean buildDb = true;
			boolean postProcess = false;
			boolean buildSet = false;
			boolean createImage = false;

			logger.fine("Will run: " + (buildDb ? "Build DB, " : "")
					+ (postProcess ? "Post Process, " : "")
					+ (buildSet ? "Build Data Set, " : "")
					+ (createImage ? "Create KML" : ""));

			File outImage;

			String path = "resources\\COLOR_SCALEBAR.TIF";
			Float totalElevation = 19910f;
			Float startElevationValue = -9150f;
			if (buildDb) {
				logger.info("Build Database Started");
				PaletteProcess palette = new PaletteProcess();
				palette.createPalette(path, totalElevation, startElevationValue);

				ImageProcess image = new ImageProcess(palette);
				image.generateData("resources\\WAC_CSHADE_E000N1800_016P.TIF");
				logger.info("Build Database finished");
			}

			if (postProcess) {
				logger.info("Post Processing Started");
				PostProcessor postProc = new PostProcessor(0.0625f, 0.0625f);
				postProc.postProcess();
				logger.info("Post Pocessing Finished");
			}

			if (buildSet) {
				logger.info("Building data set started");
				BuildDataSets dataSet = new BuildDataSets();
				dataSet.buildDataSet(30000);

				logger.info("Building data set finished");
			}

			if (createImage) {
				logger.info("Creating height map");
				DbConnection dbConn = new DbConnection();
				Connection conn = dbConn.getConnection();
				ResultSet results = null;
				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					results = stmt
							.executeQuery("SELECT * FROM set_30 ORDER BY LAT, LON");

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

				}

				results.beforeFirst();

				ArrayList<DataTile> dataTiles = new ArrayList<DataTile>();
				while (results.next()) {
					Float lat = results.getFloat("LAT");
					Float lon = results.getFloat("LON");
					Long height = results.getLong("HEIGHT");
					int rank = results.getInt("RANK");
					dataTiles.add(new DataTile(lat, lon, rank, height
							.doubleValue()));
				}
				stmt.close();
				try {
					dbConn.closeConnection(conn);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ImageCreateOverlay image = new ImageCreateOverlay();
				outImage = image.createOverlay(dataTiles, 0.99f, 0.99f);
				logger.info("Height Map created, KML Creation started");
				CreateKml map = new CreateKml();
				map.CreateKmz("test", outImage);
				logger.info("KML Created");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Lunar Finished");
	}
}
