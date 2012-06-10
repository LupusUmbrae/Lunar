package image.palette;

import java.util.List;

public class MonoInterp extends Section {

	private int startColour;
	private int endColour;
	private float stepping;
	private boolean downwardsLine;
	private List<Integer> knownPoints;

	public MonoInterp(int[] min, int[] max, float minAltitude,
			float maxAltitude, boolean red, boolean green, boolean blue,
			int startColour, int endColour, List<Integer> knownPoints)
			throws InterpException {
		super(min, max, minAltitude, maxAltitude, red, green, blue);

		if (red ^ green ^ blue) {
			throw new InterpException(
					"More than one colour is set for the interprotation");
		}
		this.knownPoints = knownPoints;
		this.startColour = startColour;
		this.endColour = endColour;
		int steps;
		if (startColour < endColour) {
			steps = endColour - startColour;
			this.downwardsLine = false;
		} else {
			steps = startColour - endColour;
			this.downwardsLine = true;
		}
		this.stepping = (maxAltitude - minAltitude) / steps;
	}

	@Override
	public float process(int[] rgb) {
		float altitude;
		int colourPos = -1;

		if (red) {
			colourPos = 0;
		}
		if (green) {
			colourPos = 1;
		}
		if (blue) {
			colourPos = 2;
		}

		if (downwardsLine) {
			altitude = processDownwards(rgb, colourPos);
		} else {
			altitude = processUpwards(rgb, colourPos);
		}
		return altitude;
	}

	/**
	 * 
	 * @param rgb
	 * @param colourPos
	 * @return
	 */
	private float processDownwards(int[] rgb, int colourPos) {
		int[] positions;
		int startPoint;
		int endPoint;
		float startAlt;
		float endAlt;
		float pointStepping;
		int pointSteps;
		int colourPoint = rgb[colourPos];

		float alt;

		positions = findCloesetPoints(colourPoint);
		startPoint = knownPoints.get(positions[0]);
		endPoint = knownPoints.get(positions[1]);
		pointSteps = startPoint - endPoint;
		startAlt = this.minAltitude + (this.stepping * positions[0]);
		endAlt = startAlt + this.stepping;
		pointStepping = (startAlt - endAlt) / pointSteps;

		alt = startAlt + (pointStepping * (colourPoint - startPoint));

		return alt;
	}

	private float processUpwards(int[] rgb, int colourPos) {

		return 0;
	}

	private int[] findCloesetPoints(int point) {

		int position = 0;
		int closestFirstPoint = 0;
		int closestSecondPoint;

		for (int curPoint : knownPoints) {
			if ((downwardsLine && curPoint > point)
					|| (!downwardsLine && curPoint < point)) {
				break;
			}
			position++;
		}
		closestSecondPoint = knownPoints.get(position);

		return new int[] { position - 1, position };
	}
}
