package image.palette;

import java.util.SortedMap;

public class MonoInterp extends Section
{

	private final boolean downwardsLine;
	private final SortedMap<Integer, Float> knownPoints;

	public MonoInterp(int[] min, int[] max, float minAltitude,
			float maxAltitude, boolean red, boolean green, boolean blue,
			int startColour, int endColour,
			SortedMap<Integer, Float> knownPoints) throws InterpException
	{
		super(min, max, minAltitude, maxAltitude, red, green, blue);

		if (!(red ^ green ^ blue))
		{
			throw new InterpException(
					"More than one colour (or none) is set for the interprotation");
		}
		this.knownPoints = knownPoints;
		if (startColour < endColour)
		{
			this.downwardsLine = false;
		} else
		{
			this.downwardsLine = true;
		}
	}

	/**
	 * y = mx+c equation used between two known points either side of the given
	 * value to interpolate against
	 * 
	 * @param int[] rgb The full RGB Value to be interpolated against. Only a
	 *        single channel is used
	 * @return float Derived altitude
	 * @throws InterpException
	 */
	@Override
	public float process(int[] rgb) throws InterpException
	{
		int[] positions;

		// R, G or B? and its value
		int colourValue;
		int colourPosition = -1;

		// Start/End points in the rgb list
		int startPoint;
		int endPoint;

		// Start/End altitudes in the rgb list
		float startAlt;
		float endAlt;

		// Used in equation
		int steps;
		int xDiff;
		float gradient;
		float altitude;

		if (red)
		{
			colourPosition = 0;
		}
		if (green)
		{
			colourPosition = 1;
		}
		if (blue)
		{
			colourPosition = 2;
		}

		if (colourPosition == -1)
		{
			throw new InterpException("No colour channel defined for analysis");
		}
		colourValue = rgb[colourPosition];

		positions = findCloesetPoints(colourValue);

		startPoint = positions[0];
		endPoint = positions[1];

		if (downwardsLine)
		{
			steps = endPoint - startPoint;
			xDiff = endPoint - colourValue;
		} else
		{
			steps = startPoint - endPoint;
			xDiff = startPoint - colourValue;
		}

		startAlt = this.knownPoints.get(startPoint);
		endAlt = this.knownPoints.get(endPoint);
		gradient = (endAlt - startAlt) / steps;
		altitude = (gradient * xDiff) + startAlt;

		return altitude;
	}

	/**
	 * This finds the closest Colour value match both bigger and smaller than
	 * the given value and returns them
	 * 
	 * @param point
	 *            the Colour value to find cloesest points of
	 * @return int[] {point before, point after}
	 * @throws InterpException
	 */
	private int[] findCloesetPoints(int point) throws InterpException
	{
		int positionLast = 300;
		int position = 0;

		for (int curPoint : knownPoints.keySet())
		{
			position = curPoint;
			if ((downwardsLine && curPoint > point)
					|| (!downwardsLine && curPoint < point))
			{
				break;
			}
			positionLast = curPoint;
		}

		if (positionLast == 300 || position == positionLast)
		{
			throw new InterpException(
					"Given point is too big/small for this section");
		}

		return new int[] { positionLast, position };
	}
}
