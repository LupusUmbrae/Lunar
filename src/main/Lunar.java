package main;

import image.ImageCreateOverlay;
import image.ImageProcess;
import image.palette.PaletteProcess;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import map.CreateKml;
import database.BuildDataSets;
import database.DbConnection;

public class Lunar {

	public static final int LAT_MAX = 90;
	public static final int LAT_MIN = -90;
	public static final int LON_MAX = 360;
	public static final int LON_MIN = 0;

	public static final String OUT_DIR = "c:\\temp";
	public static final String PIXEL_DIR = "\\pixels";
	public static final String ROW_DIR = "\\row";
	public static final String PROCESSED_DIR = "\\processed";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			boolean buildDb = false;
			boolean buildSet = false;
			boolean createImage = true;

			File outImage;

			String path = "resources\\COLOR_SCALEBAR.TIF";
			Float totalElevation = 19910f;
			Float startElevationValue = -9150f;
			if (buildDb) {
				PaletteProcess palette = new PaletteProcess();
				palette.createPalette(path, totalElevation, startElevationValue);

				ImageProcess image = new ImageProcess(palette);
				image.generateData("resources\\WAC_CSHADE_E000N1800_016P.TIF");
			}

			if (buildSet) {
				BuildDataSets dataSet = new BuildDataSets();
				dataSet.buildDataSet(30000);

				System.out.println("Data set built... wooo!");
			}

			if (createImage) {
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

				CreateKml map = new CreateKml();
				map.CreateKmz("test", outImage);
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

	}
}
