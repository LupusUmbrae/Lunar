package database.storage;

import java.util.ArrayList;

import main.DataTile;

public class RawTile {

	private ArrayList<ArrayList<DataTile>> rawDataTile;
	private float lat;
	private float lon;

	public RawTile(ArrayList<ArrayList<DataTile>> rawDataTile, float lat,
			float lon) {
		this.rawDataTile = rawDataTile;
		this.lat = lat;
		this.lon = lon;
	}

	public ArrayList<ArrayList<DataTile>> getRwaDataTile() {
		return rawDataTile;
	}

	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}
}
