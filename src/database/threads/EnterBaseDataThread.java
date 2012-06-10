package database.threads;

import image.ImageProcess;
import image.threads.ImageStorage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import database.DbConnection;

public class EnterBaseDataThread implements Runnable {

	@Override
	public void run() {
		DbConnection dbConn = new DbConnection();
		Connection conn = dbConn.getConnection();
		try {

			String statement;

			while (true) {
				Statement stmt = conn.createStatement();
				statement = ImageStorage.statementsPoll();
				if (statement != null) {
					stmt.execute(statement);
				} else if (ImageStorage.isEndStatement()) {
					stmt.close();
					break;
				} else {
					Thread.sleep(1000);
				}
			}

			System.out.println("Base Data Thread finished");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
