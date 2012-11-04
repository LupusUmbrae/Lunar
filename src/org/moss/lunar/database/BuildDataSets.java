package org.moss.lunar.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.moss.lunar.DataTile;
import org.moss.lunar.analysis.threads.ProcessTileThread;
import org.moss.lunar.database.storage.DataQuery;
import org.moss.lunar.database.storage.ThreadStorage;
import org.moss.lunar.database.threads.EnterTileSetThread;
import org.moss.lunar.database.threads.GetPixelAreaThread;




public class BuildDataSets {
	// in meters!!
	private final long MOON_CIRC = 10921000;
	private final int LAT_MAX = 90;
	private final int LAT_MIN = -90;
	private final int LON_MAX = 360;
	private final int LON_MIN = 0;

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
		DecimalFormat dec = new DecimalFormat();
		dec.setMaximumFractionDigits(2);
		String latStepString = dec.format((360f / (MOON_CIRC / size)));
		String lonStepString = dec.format((360f / (MOON_CIRC / size)));
		float latStep = Float.parseFloat(latStepString);
		float lonStep = Float.parseFloat(lonStepString);
		float currentLat = LAT_MIN;
		float currentLon = LON_MIN;
		// This is the distance between two pixels
		// TODO: Move this into the database
		int distance = (int) (MOON_CIRC / 5760);
		String db = DataSets.getDb(size);
		boolean alive = true;
		DataQuery dataQuery;

		Connection conn = dbConn.getConnection();
		updateSetConfig(conn, db, latStep, lonStep);
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

		enterTileSetThreadA.start();
		enterTileSetThreadB.start();

		// Lets create the statements
		currentLat = LAT_MIN;
		currentLon = LON_MIN;
		int statementCount = 0;
		while (currentLat < LAT_MAX) {
			float startLat;
			float endLat;
			startLat = currentLat;
			currentLon = LON_MIN;
			endLat = currentLat + latStep;

			while (currentLon < LON_MAX) {
				float startLon;
				float endLon;
				startLon = currentLon;
				endLon = currentLon + lonStep;

				try {

					String stmtBuilder = String
							.format("SELECT * FROM base_data WHERE (LAT BETWEEN %s AND %s) AND (LON BETWEEN %s AND %s) ORDER BY LAT, LON",
									dec.format(startLat), dec.format(endLat), dec.format(startLon), dec.format(endLon));
					dataQuery = new DataQuery(stmtBuilder, currentLat,
							currentLon);
					ThreadStorage.putSqlQueries(dataQuery);
					dataQuery = null;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				statementCount++;
				currentLon += lonStep;
			}
			currentLat += latStep;
		}

		System.out.println(statementCount + " Select statements created");

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
		while (processTileThreadA.isAlive()) {
			Thread.sleep(10000);
		}
		System.out.println("Data processed and ready for re-entry to database");

		ThreadStorage.setEndStatement(true);
		while (enterTileSetThreadA.isAlive() && enterTileSetThreadB.isAlive()) {
			Thread.sleep(10000);
		}
		System.out.println("Data entered into database");
	}

	private void updateSetConfig(Connection conn, String setName,
			float latStep, float lonStep) throws SQLException {
		Statement stmt = conn.createStatement();
		String stmtBuilder = String
				.format("UPDATE set_config SET LAT_STEP=%f, LON_STEP=%f WHERE DATA_SET='%s';",
						latStep, lonStep, setName);
		stmt.executeUpdate(stmtBuilder);
	}
}
