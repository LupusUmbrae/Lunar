package org.moss.lunar.database.threads;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.moss.lunar.database.DbConnection;
import org.moss.lunar.image.ImageProcess;
import org.moss.lunar.image.threads.ImageStorage;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class EnterBaseDataThread implements Runnable {

	public void run() {
		DbConnection dbConn = new DbConnection();
		Connection conn = dbConn.getConnection();
		try {

			String statement;

			while (true) {
				Statement stmt = conn.createStatement();
				statement = ImageStorage.statementsPoll();
				try {
					if (statement != null) {
						stmt.execute(statement);
					} else if (ImageStorage.isEndStatement()) {
						stmt.close();
						break;
					} else {
						Thread.sleep(1000);
					}
				} catch (MySQLSyntaxErrorException e) {
					System.out.println(statement);
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
