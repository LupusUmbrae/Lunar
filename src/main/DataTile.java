package main;

import java.util.Comparator;

public class DataTile implements Comparator<DataTile> {

	private Float lat;

	private Float lon;

	private int rank;

	private Double height;

	public DataTile(Float lat, Float lon, int rank, Double height) {
		this.lat = lat;
		this.lon = lon;
		this.rank = rank;
		this.height = height;
	}
	
	public DataTile(Float lat, Float lon, Double height) {
		this.lat = lat;
		this.lon = lon;
		this.height = height;
	}

	public Float getLat() {
		return lat;
	}

	public Float getLon() {
		return lon;
	}

	public int getRank() {
		return rank;
	}

	public Double getHeight() {
		return height;
	}

	@Override
	public int compare(DataTile tileA, DataTile tileB) {
		
		if(tileA.getLat() < tileB.getLat()){
			return -1;
		}else if(tileA.getLat() > tileB.getLat()){
			return 1;
		}else{
			return 0;
		}
	}
	

}
