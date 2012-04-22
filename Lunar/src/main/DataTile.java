package main;

public class DataTile {

	private int lat;

	private int lon;

	private int rank;

	private Double height;

	public DataTile(int lat, int lon, int rank, Double height) {
		this.lat = lat;
		this.lon = lon;
		this.rank = rank;
		this.height = height;
	}
	
	public DataTile(int lat, int lon, Double height) {
		this.lat = lat;
		this.lon = lon;
		this.height = height;
	}

	public int getLat() {
		return lat;
	}

	public int getLon() {
		return lon;
	}

	public int getRank() {
		return rank;
	}

	public Double getHeight() {
		return height;
	}

}
