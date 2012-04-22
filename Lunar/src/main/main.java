package main;

import image.ImageCreateOverlay;
import image.ImageProcess;
import image.PaletteProcess;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import database.buildDataSets;

public class main {

	private final static String MYSQL_IP = "localhost";
	private final static String MYSQL_PORT = "3306";
	private final static String MYSQL_DB = "lunar";
	private final static String MYSQL_USER = "lunar";
	private final static String MYSQL_PASSWORD = "lunar";

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		try {
			boolean buildDb = true;

			Class.forName("com.mysql.jdbc.Driver");
			String url = String.format("jdbc:mysql://%s:%s/%s", MYSQL_IP,
					MYSQL_PORT, MYSQL_DB);
			Connection conn = DriverManager.getConnection(url, MYSQL_USER,
					MYSQL_PASSWORD);
			System.out.println("MySQL Connected");

			String path = "C:\\Users\\Robin\\git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
			Double totalElevation = 19910d;
			Double startElevationValue = -9150d;
			if (buildDb) {
				PaletteProcess palette = new PaletteProcess();
				palette.createPalette(path, totalElevation, startElevationValue);

				ImageProcess image = new ImageProcess(palette);
				image.generateData(
						"C:\\Users\\Robin\\git\\Lunar\\Lunar\\resources\\WAC_CSHADE_E000N1800_016P.TIF",
						conn);
			}
			buildDataSets dataSet = new buildDataSets();
			dataSet.buildDataSet(100, conn);
			
//			ArrayList<ArrayList<DataTile>> dataTiles = new ArrayList<ArrayList<DataTile>>();
//			ArrayList<DataTile> dataTileRow = new ArrayList<DataTile>();
//			dataTileRow.add(new DataTile(90, 180, 100, 10.0));
//			dataTileRow.add(new DataTile(0, 180, 0, 10.0));
//			dataTiles.add(dataTileRow);
//			dataTiles.add(dataTileRow);
//			ImageCreateOverlay image = new ImageCreateOverlay();
//			image.createOverlay(dataTiles);
			
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
