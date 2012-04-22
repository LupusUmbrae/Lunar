package main;

import image.ImageProcess;
import image.PaletteProcess;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

			 String path =
			 "C:\\Users\\Robin\\git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
			 Double totalElevation = 19910d;
			 Double startElevationValue = -9150d;
			
			 PaletteProcess palette = new PaletteProcess();
			 palette.createPalette(path, totalElevation, startElevationValue);

			if (buildDb) {
				ImageProcess image = new ImageProcess(palette);
				image.generateData(
						"C:\\Users\\Robin\\git\\Lunar\\Lunar\\resources\\WAC_CSHADE_E000N1800_016P.TIF",
						conn);
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
