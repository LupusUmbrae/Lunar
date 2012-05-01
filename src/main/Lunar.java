package main;

import image.ImageProcess;
import image.PaletteProcess;

import java.io.IOException;
import java.sql.SQLException;

import database.BuildDataSets;

public class Lunar {

	public static final Double LAT_MAX = 90d;
	public static final Double LAT_MIN = -90d;
	public static final Double LON_MAX = 180d;
	public static final Double LON_MIN = -180d;

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

			String path = "resources\\COLOR_SCALEBAR.TIF";
			Double totalElevation = 19910d;
			Double startElevationValue = -9150d;
			if (buildDb) {
				PaletteProcess palette = new PaletteProcess();
				palette.createPalette(path, totalElevation, startElevationValue);

				ImageProcess image = new ImageProcess(palette);
				image.generateData("resources\\WAC_CSHADE_E000N1800_016P.TIF");
			}
			BuildDataSets dataSet = new BuildDataSets();
			dataSet.buildDataSet(100000);

			System.out.println("Data set built... wooo!");
			
			// ArrayList<ArrayList<DataTile>> dataTiles = new
			// ArrayList<ArrayList<DataTile>>();
			// ArrayList<DataTile> dataTileRow = new ArrayList<DataTile>();
			// dataTileRow.add(new DataTile(90, 180, 100, 10.0));
			// dataTileRow.add(new DataTile(0, 180, 0, 10.0));
			// dataTiles.add(dataTileRow);
			// dataTiles.add(dataTileRow);
			// ImageCreateOverlay image = new ImageCreateOverlay();
			// image.createOverlay(dataTiles);

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
