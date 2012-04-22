package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.DataTile;

public class buildDataSets {
	// in meters!!
	private final Double MOON_CIRC = 10921000d;
	private final Double LAT_MAX = 90d;
	private final Double LAT_MIN = -90d;
	private final Double LON_MAX = 180d;
	private final Double LON_MIN = -180d;

	/**
	 * Get base data from database and create a dataset for analysis
	 * 
	 * @param lat
	 * @param lon
	 * @throws SQLException 
	 */
	public void buildDataSet(int size, Connection conn) throws SQLException {
		Double latLonStep = 360 / (MOON_CIRC / size);
		Double currentLat = LAT_MIN;
		Double currentLon = LON_MIN;
		ArrayList<DataTile> row = new ArrayList<DataTile>();
		ArrayList<ArrayList<DataTile>> dataTiles = new ArrayList<ArrayList<DataTile>>();
		getData(LAT_MAX, LAT_MAX - latLonStep, currentLon, currentLon+ latLonStep, conn);
		while (currentLat < LAT_MAX) {
			while (currentLon < LON_MAX) {
				//DataTile rowTile = processTile(tiles, distance, lat, lon)
				currentLon += latLonStep;
			}
			currentLat += latLonStep;
		}
	}

	/**
	 * 
	 * @param tiles
	 * @param distance
	 * @param lat
	 * @param lon
	 * @return
	 */
	private DataTile processTile(ArrayList<ArrayList<DataTile>> tiles,
			Double distance, double lat, double lon) {
		int rankAverage;
		int rankTotal = 0;
		int finalLat;
		int finalLon;
		Double finalHeight;
		Double heightTotal = 0d;

		ArrayList<Integer> rankArray = new ArrayList<Integer>();
		ArrayList<Double> heightArray = new ArrayList<Double>();

		for (int x = 0; x < tiles.size() - 1; x++) {

			ArrayList<DataTile> tileRow = tiles.get(x);

			for (int y = 0; y < tileRow.size() - 1; y++) {

				Double slope;
				Double opp;
				Double centre = tileRow.get(y).getHeight();
				Double right = tileRow.get(y + 1).getHeight();
				Double below = tiles.get(x).get(y).getHeight();

				// Calc right slope first
				opp = centre - right;
				slope = Math.tan((opp / distance));
				rankArray.add(calcSlopeRank(slope));

				// Calc below slope
				opp = centre - right;
				slope = Math.tan((opp / distance));
				rankArray.add(calcSlopeRank(slope));
			}
		}

		// Calualte rank
		for (Integer rank : rankArray) {
			rankTotal += rank;
		}
		rankAverage = rankTotal / rankArray.size();

		for (Double height : heightArray) {
			heightTotal += height;
		}
		finalHeight = heightTotal / heightArray.size();

		// truncate lat/lon
		finalLat = (int) lat;
		finalLon = (int) lon;

		return null;
		// return new DataTile(finalLat, finalLon, rankAverage, finalHeight);
	}

	/**
	 * 
	 * @param slope
	 * @return
	 */
	private int calcSlopeRank(Double slope) {
		// -0.5*slope^2+100
		int rank = (int) (-0.5 * (slope * slope) + 100);
		return rank;
	}

	private void getData(Double latStart, Double latEnd, Double lonStart,
			Double lonEnd, Connection conn) throws SQLException {
		ArrayList<DataTile> dataTileResults = new ArrayList<DataTile>();
		Statement stmt = conn.createStatement();
		String stmtBuilder = String
				.format("SELECT * FROM base_data WHERE (LAT > %f AND LAT < %f) AND (LON > %f AND LON < %f) ORDER BY LAT, LON",
						latStart, latEnd, lonStart, lonEnd);

		ResultSet results = stmt.executeQuery(stmtBuilder);
		results.beforeFirst();
		while (results.next()) {
			Double lat = results.getDouble(0);
			Double lon = results.getDouble(1);
			Double height = results.getDouble(2);
			dataTileResults.add(new DataTile(lat, lon, height));
		}

	}

	private void sortDataTiles(ArrayList<DataTile> unsorted) {
		// Split by Lat
	}

}
