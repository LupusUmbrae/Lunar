package database.threads;

import image.ImageProcess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import database.DbConnection;

public class EnterBaseData implements Runnable {
	private ImageProcess process;

	public EnterBaseData(ImageProcess process) {
		this.process = process;
	}

	@Override
	public void run() {
		try {

			String statement = process.statementsPoll();
			while (statement == null) {
				statement = process.statementsPoll();
			}
			DbConnection dbConn = new DbConnection();
			Connection conn = dbConn.getConnection();

			while (true) {
				Statement stmt = conn.createStatement();
				statement = process.statementsPoll();
				if (statement != null && statement.matches("end")) {
					break;
				} else if (statement != null) {
					stmt.execute(statement);
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
		}

	}
}
