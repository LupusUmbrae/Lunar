package main;

import image.ImageProcess;
import image.PaletteProcess;

import java.io.IOException;
import java.sql.SQLException;

import database.BuildDataSets;

public class Lunar {

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
