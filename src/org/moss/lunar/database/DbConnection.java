package org.moss.lunar.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

	private final static String MYSQL_IP = "localhost";
	private final static String MYSQL_PORT = "3306";
	private final static String MYSQL_DB = "lunar";
	private final static String MYSQL_USER = "lunar";
	private final static String MYSQL_PASSWORD = "lunar";

	private static int connectionPool = 8;

	public static Connection getConnection() {
		if (connectionPool > 0) {
			connectionPool--;
			return openConnection();
		}else{
			return null;
		}
	}

	public static void closeConnection(Connection conn) throws SQLException {
		conn.close();
		connectionPool++;
	}

	private static Connection openConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = String.format("jdbc:mysql://%s:%s/%s", MYSQL_IP,
					MYSQL_PORT, MYSQL_DB);
			return DriverManager.getConnection(url, MYSQL_USER, MYSQL_PASSWORD);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
