package database.storage;

import java.util.ArrayList;

import main.DataTile;

public class RawTile {

	private ArrayList<ArrayList<DataTile>> rwaDataTile;
	private Double lat;
	private Double lon;

	public RawTile(ArrayList<ArrayList<DataTile>> rwaDataTile, Double lat,
			Double lon) {
		this.rwaDataTile = rwaDataTile;
		this.lat = lat;
		this.lon = lon;
	}

	public ArrayList<ArrayList<DataTile>> getRwaDataTile() {
		return rwaDataTile;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLon() {
		return lon;
	}
}
