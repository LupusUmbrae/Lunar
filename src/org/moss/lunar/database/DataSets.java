package org.moss.lunar.database;

import java.util.HashMap;

public enum DataSets {

	K_METERS_10("set_10", 10000), K_METERS_20("set_20", 20000), K_METERS_30("set_30", 30000), K_METERS_40(
			"set_40", 40000), K_METERS_50("set_50", 50000), K_METERS_100("set_100", 100000), K_METERS_200(
			"set_200", 200000);

	private int meters;
	private String dbName;

	private static HashMap<Integer, String> dbLookup = new HashMap<Integer, String>();

	static {
		dbLookup.put(K_METERS_10.meters, K_METERS_10.dbName);
		dbLookup.put(K_METERS_20.meters, K_METERS_20.dbName);
		dbLookup.put(K_METERS_30.meters, K_METERS_30.dbName);
		dbLookup.put(K_METERS_40.meters, K_METERS_40.dbName);
		dbLookup.put(K_METERS_50.meters, K_METERS_50.dbName);
		dbLookup.put(K_METERS_100.meters, K_METERS_100.dbName);
		dbLookup.put(K_METERS_200.meters, K_METERS_200.dbName);
	}

	public static String getDb(int meters){
		if(dbLookup.containsKey(meters)){
			return dbLookup.get(meters);
		}
		return "set_custom";
	}
	
	private DataSets(String dbName, int meters) {
		this.meters = meters;
		this.dbName = dbName;
	}
}
