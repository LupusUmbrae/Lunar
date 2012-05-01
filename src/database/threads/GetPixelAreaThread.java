package database.threads;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.DataTile;
import database.DbConnection;
import database.storage.DataQuery;
import database.storage.RawTile;
import database.storage.ThreadStorage;

public class GetPixelAreaThread implements Runnable {


	@Override
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

	private void processTile(ResultSet results, Double latStart, Double lonStart)
			throws SQLException, InterruptedException {
		ArrayList<ArrayList<DataTile>> dataTileResults = new ArrayList<ArrayList<DataTile>>();

		results.beforeFirst();

		Long preLat = 0L;
		ArrayList<DataTile> dataTileRow = new ArrayList<DataTile>();
		while (results.next()) {
			Long lat = results.getLong("LAT");
			Long lon = results.getLong("LON");
			Long height = results.getLong("HEIGHT");
			if (!lat.equals(preLat)) {
				dataTileRow = new ArrayList<DataTile>();
				dataTileResults.add(dataTileRow);
				preLat = lat;
			}
			dataTileRow.add(new DataTile(lat.doubleValue(), lon.doubleValue(),
					height.doubleValue()));

		}

		ThreadStorage.putDataTileResults(new RawTile(dataTileResults, latStart,
				lonStart));
		dataTileResults = null;
	}
}
