package org.moss.lunar.database.threads;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.moss.lunar.DataTile;
import org.moss.lunar.database.DbConnection;
import org.moss.lunar.database.storage.DataQuery;
import org.moss.lunar.database.storage.RawTile;
import org.moss.lunar.database.storage.ThreadStorage;


public class GetPixelAreaThread implements Runnable {

	public void run() {

		DbConnection dbConn = new DbConnection();
		Connection conn = dbConn.getConnection();
		try {
			while (true) {
				DataQuery sqlQuery = ThreadStorage.pollSqlQueries();
				if (sqlQuery == null && ThreadStorage.isEndQueries()) {
					break;
				} else if (sqlQuery != null) {

					Statement stmt = conn.createStatement();
					ResultSet results = stmt.executeQuery(sqlQuery
							.getStatement());
					processTile(results, sqlQuery.getLat(), sqlQuery.getLon());
					results = null;
					stmt.close();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				dbConn.closeConnection(conn);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void processTile(ResultSet results, float latStart, float lonStart)
			throws SQLException, InterruptedException {
		ArrayList<ArrayList<DataTile>> dataTileResults = new ArrayList<ArrayList<DataTile>>();

		results.beforeFirst();

		Float preLat = 0f;
		ArrayList<DataTile> dataTileRow = new ArrayList<DataTile>();
		while (results.next()) {
			Float lat = results.getFloat("LAT");
			Float lon = results.getFloat("LON") > 180 ? 180f - results
					.getFloat("LON") : results.getFloat("LON");
			Long height = results.getLong("HEIGHT");
			if (!lat.equals(preLat)) {
				dataTileRow = new ArrayList<DataTile>();
				dataTileResults.add(dataTileRow);
				preLat = lat;
			}
			dataTileRow.add(new DataTile(lat, lon, height.doubleValue()));

		}

		ThreadStorage.putDataTileResults(new RawTile(dataTileResults, latStart,
				lonStart));
		dataTileResults = null;
	}
}
