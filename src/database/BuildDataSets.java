package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import analysis.threads.ProcessTileThread;

import database.storage.DataQuery;
import database.storage.ThreadStorage;
import database.threads.EnterTileSetThread;
import database.threads.GetPixelAreaThread;

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
	 * @throws InterruptedException
	 */
	public void buildDataSet(int size) throws SQLException,
			InterruptedException {
		DbConnection dbConn = new DbConnection();

		int latLonStep = (int) (360 / (MOON_CIRC / size));
		Double currentLat = LAT_MIN;
		Double currentLon = LON_MIN;
		// This is the distance between two pixels
		Double distance = MOON_CIRC / 5760d;
		String db = DataSets.getDb(size);
		boolean alive = true;
		DataQuery dataQuery;

		Connection conn = dbConn.getConnection();
		updateSetConfig(conn, db, latLonStep, latLonStep);
		dbConn.closeConnection(conn);

		// Create query threads
		// TODO: Create a nicer way of doing this
		Thread queryThreadA = new Thread(new GetPixelAreaThread());
		Thread queryThreadB = new Thread(new GetPixelAreaThread());
		Thread queryThreadC = new Thread(new GetPixelAreaThread());
		Thread queryThreadD = new Thread(new GetPixelAreaThread());
		Thread queryThreadE = new Thread(new GetPixelAreaThread());
		Thread queryThreadF = new Thread(new GetPixelAreaThread());

		Thread processTileThreadA = new Thread(new ProcessTileThread(distance));
		Thread processTileThreadB = new Thread(new ProcessTileThread(distance));

		Thread enterTileSetThreadA = new Thread(new EnterTileSetThread(db));
		Thread enterTileSetThreadB = new Thread(new EnterTileSetThread(db));

		// Start em up
		queryThreadA.start();
		queryThreadB.start();
		queryThreadC.start();
		queryThreadD.start();
		queryThreadE.start();
		queryThreadF.start();

		processTileThreadA.start();
		processTileThreadB.start();

		enterTileSetThreadA.start();
		enterTileSetThreadB.start();

		// Lets create the statements
		currentLat = LAT_MIN;
		currentLon = LON_MIN;
		while (currentLat < LAT_MAX) {
			Double startLat;
			Double endLat;
			startLat = currentLat;
			currentLon = LON_MIN;
			endLat = currentLat + latLonStep;

			while (currentLon < LON_MAX) {
				Double startLon;
				Double endLon;
				startLon = currentLon;
				endLon = currentLon + latLonStep;

				try {

					String stmtBuilder = String
							.format("SELECT * FROM base_data WHERE (LAT BETWEEN %f AND %f) AND (LON BETWEEN %f AND %f) ORDER BY LAT, LON",
									startLat, endLat, startLon, endLon);
					dataQuery = new DataQuery(stmtBuilder, currentLat,
							currentLon);
					ThreadStorage.putSqlQueries(dataQuery);
					dataQuery = null;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				currentLon += latLonStep;
			}
			currentLat += latLonStep;
		}

		System.out.println("Select statements compelted");

		ThreadStorage.setEndQueries(true);

		while (alive) {
			if (!queryThreadA.isAlive() && !queryThreadB.isAlive()
					&& !queryThreadC.isAlive() && !queryThreadD.isAlive()
					&& !queryThreadE.isAlive() && !queryThreadF.isAlive()) {
				alive = false;
			} else {
				Thread.sleep(10000);
			}
		}

		System.out.println("Data collected from database");

		ThreadStorage.setEndProcess(true);
		while (processTileThreadA.isAlive() && processTileThreadB.isAlive()) {
			Thread.sleep(10000);
		}
		System.out.println("Data processed and ready for re-entry to database");

		ThreadStorage.setEndStatement(true);
		while (enterTileSetThreadA.isAlive() && enterTileSetThreadB.isAlive()) {
			Thread.sleep(10000);
		}
		System.out.println("Data entered into database");
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
