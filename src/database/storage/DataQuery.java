package database.storage;

public class DataQuery {

	private String statement;
	private Double lat;
	private Double lon;

	public DataQuery(String statement, Double lat, Double lon) {
		this.lat = lat;
		this.lon = lon;
		this.statement = statement;
	}

	public String getStatement() {
		return statement;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLon() {
		return lon;
	}
}
