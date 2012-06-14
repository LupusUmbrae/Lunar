package image.palette;

import java.util.SortedMap;

public class Section
{

	private int[] min;
	private int[] max;
	private float minAltitude;
	private float maxAltitude;

	private boolean downwardsLine = false;
	
	

	private boolean red;
	private boolean green;
	private boolean blue;
	
	

	private final SortedMap<Integer, Float> knownRed;
	private final SortedMap<Integer, Float> knownGreen;
	private final SortedMap<Integer, Float> knownBlue;

	/**
	 * 
	 * @param min
	 *            Minimum RGB values allowed
	 * @param max
	 *            Maximum RGB Values allowed
	 * @param minAltitude
	 * @param maxAltitude
	 * @param red
	 * @param green
	 * @param blue
	 */
	public Section(int[] min, int[] max, float minAltitude, float maxAltitude,
			boolean red, boolean green, boolean blue,
			SortedMap<Integer, Float> knownRed,
			SortedMap<Integer, Float> knownGreen,
			SortedMap<Integer, Float> knownBlue)
	{
		this.min = min;
		this.max = max;
		this.minAltitude = minAltitude;
		this.maxAltitude = maxAltitude;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.knownRed = knownRed;
		this.knownGreen = knownGreen;
		this.knownBlue = knownBlue;
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
	public float process(int[] rgb) throws InterpException
	{
		
		int channels = 0;
		
		float altitude = 0f;

		if (red)
		{
			altitude += processChanel(rgb[0], this.knownRed);
			channels++;
		}
		if (green)
		{
			altitude += processChanel(rgb[1], this.knownGreen);
			channels++;
		}
		if (blue)
		{
			altitude += processChanel(rgb[2], this.knownBlue);
			channels++;
		}

		if(channels == 0){
			throw new InterpException("No Channels set");
		}
		
		altitude /= channels;
		
		return altitude;
	}

	private float processChanel(Integer colourValue,
			SortedMap<Integer, Float> knownPoints) throws InterpException
	{
		int[] positions;

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

		positions = findCloesetPoints(colourValue, knownPoints);

		startPoint = positions[0];
		endPoint = positions[1];

		steps = endPoint - startPoint;
		
		if (downwardsLine)
		{
			xDiff = endPoint - colourValue;
		} else
		{
			xDiff = startPoint - colourValue;
		}

		startAlt = knownPoints.get(startPoint);
		endAlt = knownPoints.get(endPoint);
		gradient = (endAlt - startAlt) / steps;
		altitude = (gradient * xDiff) + startAlt;

		return altitude;
	}

	/**
	 * 
	 * @param rgb
	 * @return
	 */
	public boolean inSection(int[] rgb)
	{
		boolean inSection = false;
		if ((min[0] <= rgb[0] && rgb[0] <= max[0])
				&& (min[1] <= rgb[1] && rgb[1] <= max[1])
				&& (min[2] <= rgb[2] && rgb[2] <= max[2]))
		{
			inSection = true;
		}
		return inSection;
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
	private int[] findCloesetPoints(int point,
			SortedMap<Integer, Float> knownPoints) throws InterpException
	{
		int positionLast = 300;
		int position = 0;

		for (Integer curPoint : knownPoints.keySet())
		{
			position = curPoint;
			if (curPoint > point)
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
