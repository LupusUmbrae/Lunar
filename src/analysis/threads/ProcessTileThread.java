package analysis.threads;

import java.util.ArrayList;

import main.DataTile;
import database.storage.RawTile;
import database.storage.ThreadStorage;

public class ProcessTileThread implements Runnable {

	private int distance;

	public ProcessTileThread(int distance) {
		this.distance = distance;
	}

	@Override
	public void run() {
		RawTile rawTile;
		ArrayList<ArrayList<DataTile>> tiles;
		while (true) {
			rawTile = ThreadStorage.pollDataTileResults();
			if (rawTile == null && ThreadStorage.isEndProcess()) {
				break;
			} else if (rawTile != null) {
				try {
					tiles = rawTile.getRwaDataTile();
					processTile(tiles, rawTile.getLat(), rawTile.getLon());
					tiles = null;
					rawTile = null;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Waiting on Data");
				try {
					Thread.sleep(3000l);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void processTile(ArrayList<ArrayList<DataTile>> tiles, float lat,
			float lon) throws InterruptedException {

		int rankAverage;
		int rankTotal = 0;
		Double finalHeight;
		Double heightTotal = 0d;

		ArrayList<Integer> rankArray = new ArrayList<Integer>();
		ArrayList<Float> heightArray = new ArrayList<Float>();

		for (int x = 0; x < tiles.size() - 1; x++) {

			ArrayList<DataTile> tileRow = tiles.get(x);

			for (int y = 0; y < tileRow.size() - 1; y++) {

				float slope;
				float opp;
				float centre = tileRow.get(y).getHeight().floatValue();
				float right = tileRow.get(y + 1).getHeight().floatValue();
				float below = tiles.get(x).get(y).getHeight().floatValue();

				heightArray.add(centre);

				// Calc right slope first
				opp = centre - right;
				if (opp < 0) {
					opp *= -1;
				}
				slope = (float) Math.toDegrees(Math.atan((opp / distance)));
				rankArray.add(calcSlopeRank(slope));

				// Calc below slope
				opp = centre - below;
				if (opp < 0) {
					opp *= -1;
				}
				slope = (float) Math.toDegrees(Math.atan((opp / distance)));
				rankArray.add(calcSlopeRank(slope));
			}
		}

		// Calculate rank
		for (Integer rank : rankArray) {
			rankTotal += rank;
		}
		rankAverage = rankTotal / rankArray.size();

		for (Float height : heightArray) {
			heightTotal += height;
		}
		finalHeight = heightTotal / heightArray.size();

		DataTile dataTile = new DataTile(lat, lon, rankAverage, finalHeight);
		ThreadStorage.putProcessedTile(dataTile);

		System.out.printf("Data Tile processed. Lat: %s, Lon: %s \n", lat, lon);
		
		// Clean up
		dataTile = null;
		heightArray = null;
		rankArray = null;

	}

	private int calcSlopeRank(float slope) {
		int rank = (int) (-0.5 * (Math.pow(slope, 2)) + 100);
		return rank;
	}

}
