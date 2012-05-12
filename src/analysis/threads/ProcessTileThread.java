package analysis.threads;

import java.util.ArrayList;

import main.DataTile;
import database.storage.RawTile;
import database.storage.ThreadStorage;

public class ProcessTileThread implements Runnable {

	private Double distance;

	public ProcessTileThread(Double distance) {
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
			}
		}

	}

	private void processTile(ArrayList<ArrayList<DataTile>> tiles, Double lat,
			Double lon) throws InterruptedException {

		int rankAverage;
		int rankTotal = 0;
		Double finalHeight;
		Double heightTotal = 0d;

		ArrayList<Integer> rankArray = new ArrayList<Integer>();
		ArrayList<Double> heightArray = new ArrayList<Double>();

		for (int x = 0; x < tiles.size() - 1; x++) {

			ArrayList<DataTile> tileRow = tiles.get(x);

			for (int y = 0; y < tileRow.size() - 1; y++) {

				Double slope;
				Double opp;
				Double centre = tileRow.get(y).getHeight();
				Double right = tileRow.get(y + 1).getHeight();
				Double below = tiles.get(x).get(y).getHeight();

				heightArray.add(centre);

				// Calc right slope first
				opp = centre - right;
				if (opp < 0) {
					opp *= -1;
				}
				slope = Math.toDegrees(Math.atan((opp / distance)));
				rankArray.add(calcSlopeRank(slope));

				// Calc below slope
				opp = centre - below;
				if (opp < 0) {
					opp *= -1;
				}
				slope = Math.toDegrees(Math.atan((opp / distance)));
				rankArray.add(calcSlopeRank(slope));
			}
		}

		// Calculate rank
		for (Integer rank : rankArray) {
			rankTotal += rank;
		}
		rankAverage = rankTotal / rankArray.size();

		for (Double height : heightArray) {
			heightTotal += height;
		}
		finalHeight = heightTotal / heightArray.size();

		DataTile dataTile = new DataTile(lat.doubleValue(), lon.doubleValue(),
				rankAverage, finalHeight);
		ThreadStorage.putProcessedTile(dataTile);

		// Clean up
		dataTile = null;
		heightArray = null;
		rankArray = null;

	}

	private int calcSlopeRank(Double slope) {
		Double calcSlope = slope;

		// (-0.5*(slope^2))+100
		int rank = (int) (-0.5 * (Math.pow(calcSlope, 2)) + 100);
		return rank;
	}

}
