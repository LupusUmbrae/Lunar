package org.moss.lunar.database.threads;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.moss.lunar.DataTile;
import org.moss.lunar.database.DbConnection;
import org.moss.lunar.database.storage.ThreadStorage;


public class EnterTileSetThread implements Runnable {

	private String db;

	public EnterTileSetThread(String db) {
		this.db = db;
	}

	public void run() {
		DbConnection dbConn = new DbConnection();
		Connection conn = dbConn.getConnection();
		DataTile tile;
		try {
			while (true) {

				tile = ThreadStorage.pollProcessedTile();
				if (tile == null && ThreadStorage.isEndStatement()) {
					break;
				} else if (tile != null) {

					Statement stmt;
					stmt = conn.createStatement();

					String stmtBuilder = String
							.format("INSERT INTO %s (LAT, LON, HEIGHT, RANK) VALUES (%s, %s, %s, %d)",
									db, tile.getLat().toString(), tile.getLon()
											.toString(), tile.getHeight()
											.toString(), tile.getRank());
					stmt.execute(stmtBuilder);
					stmt.close();
					tile = null;
					stmt = null;
				}
			}
		} catch (SQLException e) {
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
}
