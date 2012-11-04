package org.moss.lunar.database.storage;

public class DataQuery {

	private String statement;
	private float lat;
	private float lon;

	public DataQuery(String statement, float lat, float lon) {
		this.lat = lat;
		this.lon = lon;
		this.statement = statement;
	}

	public String getStatement() {
		return statement;
	}

	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}
}
