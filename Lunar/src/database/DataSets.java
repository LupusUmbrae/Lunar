package database;

import java.util.HashMap;

public enum DataSets {

	METERS_10("", 10), METERS_20("", 20), METERS_30("", 30), METERS_40("", 40), METERS_50(
			"", 50), METERS_100("", 100), METERS_200("", 200);

	private int meters;
	private String dbName;

	private static HashMap<Integer, String> dbLookup = new HashMap<Integer, String>();
	
	static{
		dbLookup.put(10, "set_10_m");
		dbLookup.put(20, "set_20_m");
		dbLookup.put(30, "set_30_m");
		dbLookup.put(40, "set_40_m");
		dbLookup.put(50, "set_50_m");
		dbLookup.put(100, "set_100_m");
		dbLookup.put(200, "set_200_m");
	}
	
	private DataSets(String dbName, int meters) {
		this.meters = meters;
		this.dbName = dbName;
	}
}
