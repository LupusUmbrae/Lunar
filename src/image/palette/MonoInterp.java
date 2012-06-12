package image.palette;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class MonoInterp extends Section
{

	private int startColour;
	private int endColour;
	private float stepping;
	private boolean downwardsLine;
	// private List<Integer> knownPoints;
	private SortedMap<Integer, Float> knownPoints;

	public MonoInterp(int[] min, int[] max, float minAltitude,
			float maxAltitude, boolean red, boolean green, boolean blue,
			int startColour, int endColour, SortedMap<Integer, Float> knownPoints)
			throws InterpException
	{
		super(min, max, minAltitude, maxAltitude, red, green, blue);

//		if ((red ^ green) ^ blue)
//		{
//			throw new InterpException(
//					"More than one colour is set for the interprotation");
//		}
		this.knownPoints = knownPoints;
		this.startColour = startColour;
		this.endColour = endColour;
		int steps;
		if (startColour < endColour)
		{
			steps = endColour - startColour;
			this.downwardsLine = false;
		} else
		{
			steps = startColour - endColour;
			this.downwardsLine = true;
		}
		this.stepping = (maxAltitude - minAltitude) / steps;
	}

	@Override
	public float process(int[] rgb)
	{
		int[] positions;

		// Start/End points in the rgb list
		int startPoint;
		int endPoint;

		// Start/End altitudes in the rgb list
		float startAlt;
		float endAlt;

		int steps;
		float stepping;

		// R, G or B? and its value
		int colourValue;
		int colourPosition = -1;
		float alt;

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

		colourValue = rgb[colourPosition];

		positions = findCloesetPoints(colourValue);

		startPoint = positions[0];
		endPoint = positions[1];

		if (downwardsLine)
		{
			steps = startPoint - endPoint;
		} else
		{
			steps = endPoint - startPoint;
		}

		startAlt = this.knownPoints.get(startPoint);
		endAlt = this.knownPoints.get(endPoint);
		stepping = (endAlt - startAlt) / steps;
		alt = startAlt + (stepping * (startPoint - colourValue));

		return alt;
	}

	private int[] findCloesetPoints(int point)
	{
		int positionLast = 0;
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

		return new int[] { position, positionLast };
	}
}
