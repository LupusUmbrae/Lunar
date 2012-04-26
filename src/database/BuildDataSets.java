package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.DataTile;

public class BuildDataSets {
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
	public void buildDataSet(int size) throws SQLException {
		DbConnection dbConn = new DbConnection();

		int latLonStep = (int) (360 / (MOON_CIRC / size));
		Double currentLat = LAT_MIN;
		Double currentLon = LON_MIN;
		String db = DataSets.getDb(size);
		ArrayList<ArrayList<DataTile>> dataTiles;
		Connection conn = dbConn.getConnection();

		updateSetConfig(conn, db, latLonStep, latLonStep);

		currentLat = LAT_MIN;
		currentLon = LON_MIN;
		while (currentLat < LAT_MAX) {
			Double startLat;
			Double endLat;
			startLat = currentLat;
			endLat = currentLat + latLonStep;
			while (currentLon < LON_MAX) {
				dataTiles = new ArrayList<ArrayList<DataTile>>();

				Double startLon;
				Double endLon;
				startLon = currentLon;
				endLon = currentLon + latLonStep;

				// Get the data

				dataTiles = getData(startLat, endLat, startLon, endLon, conn);

				DataTile tile = processTile(dataTiles, 1896, currentLat,
						currentLon);
				if (tile != null) {
					addTileSet(conn, db, tile);
				}
				dataTiles = null;
				currentLon += latLonStep;
			}
			currentLat += latLonStep;
		}
		dbConn.closeConnection(conn);
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
			int distance, double lat, double lon) {
		int rankAverage;
		int rankTotal = 0;
		Integer finalLat;
		Integer finalLon;
		Double finalHeight;
		Double heightTotal = 0d;

		ArrayList<Integer> rankArray = new ArrayList<Integer>();
		ArrayList<Double> heightArray = new ArrayList<Double>();

		if (tiles.size() == 0) {
			// Theres missing data so return null :(
			return null;
		}

		for (int x = 0; x < tiles.size() - 1; x++) {

			ArrayList<DataTile> tileRow = tiles.get(x);

			for (int y = 0; y < tileRow.size() - 1; y++) {

				Double slope;
				Double opp;
				Double centre = tileRow.get(y).getHeight();
				Double right = tileRow.get(y + 1).getHeight();
				Double below = tiles.get(x).get(y).getHeight();

				heightArray.add(centre);

				// Calc right slope first
				opp = centre - right;
				slope = Math.toDegrees(Math.tan((opp / distance)));
				rankArray.add(calcSlopeRank(slope));

				// Calc below slope
				opp = centre - below;
				slope = Math.toDegrees(Math.tan((opp / distance)));
				rankArray.add(calcSlopeRank(slope));
			}
		}

		// Calculate rank
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

		return new DataTile(finalLat.doubleValue(), finalLon.doubleValue(),
				rankAverage, finalHeight);
	}

	/**
	 * 
	 * @param slope
	 * @return
	 */
	private int calcSlopeRank(Double slope) {
		if(slope < 0){
			slope *= -1;
		}
		
		// (-0.5*(slope^2))+100
		int rank = (int) (-0.5 * (Math.pow(slope, 2)) + 100);
		return rank;
	}

	private ArrayList<ArrayList<DataTile>> getData(Double latStart,
			Double latEnd, Double lonStart, Double lonEnd, Connection conn)
			throws SQLException {
		ArrayList<ArrayList<DataTile>> dataTileResults = new ArrayList<ArrayList<DataTile>>();
		Statement stmt = conn.createStatement();
		String stmtBuilder = String
				.format("SELECT * FROM base_data WHERE (LAT > %f AND LAT < %f) AND (LON > %f AND LON < %f) ORDER BY LAT, LON",
						latStart, latEnd, lonStart, lonEnd);

		ResultSet results = stmt.executeQuery(stmtBuilder);
		results.beforeFirst();
		Long preLat = 0L;
		ArrayList<DataTile> dataTileRow = new ArrayList<DataTile>();
		while (results.next()) {
			Long lat = results.getLong("LAT");
			Long lon = results.getLong("LON");
			Long height = results.getLong("HEIGHT");
			if (lat != preLat) {
				dataTileRow = new ArrayList<DataTile>();
				dataTileResults.add(dataTileRow);
				preLat = lat;
			}
			dataTileRow.add(new DataTile(lat.doubleValue(), lon.doubleValue(),
					height.doubleValue()));

		}
		return dataTileResults;
	}

	private void addTileSet(Connection conn, String db, DataTile tile)
			throws SQLException {
		Statement stmt = conn.createStatement();
		String stmtBuilder = String
				.format("INSERT INTO %s (LAT, LON, HEIGHT, RANK) VALUES (%s, %s, %s, %d)",
						db, tile.getLat().toString(), tile.getLon().toString(),
						tile.getHeight().toString(), tile.getRank());
		stmt.execute(stmtBuilder);
	}

	private void updateSetConfig(Connection conn, String setName, int latStep,
			int lonStep) throws SQLException {
		Statement stmt = conn.createStatement();
		String stmtBuilder = String
				.format("UPDATE set_config SET LAT_STEP=%d, LON_STEP=%s WHERE DATA_SET='%s';",
						latStep, lonStep, setName);
		stmt.executeUpdate(stmtBuilder);
	}
}
