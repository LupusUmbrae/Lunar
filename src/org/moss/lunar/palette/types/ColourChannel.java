package org.moss.lunar.palette.types;

import org.moss.lunar.image.types.RgbEnum;

public class ColourChannel implements Comparable<ColourChannel>
{

    private final RgbEnum colourChannel;
    private final boolean downwardsSlope;
    private final int max;
    private final int min;
    private final int diff;

    /**
     * Creates a colour channel based on the given details
     * 
     * @param colourChannel
     * @param startPoint
     * @param endPoint
     */
    public ColourChannel(RgbEnum colourChannel, int startPoint, int endPoint)
    {
        this.colourChannel = colourChannel;
        downwardsSlope = startPoint > endPoint;
        min = downwardsSlope ? endPoint : startPoint;
        max = downwardsSlope ? startPoint : endPoint;
        diff = max - min;
    }

    public RgbEnum getChannel()
    {
        return colourChannel;
    }

    public boolean isDownwardsSlope()
    {
        return downwardsSlope;
    }

    public int getMax()
    {
        return max;
    }

    public int getMin()
    {
        return min;
    }

    public int getDiff()
    {
        return diff;
    }

    @Override
    public int compareTo(ColourChannel o)
    {
        int result = 0;
        if (o.getDiff() == diff)
        {
            result = 0;
        } else if (o.getDiff() > diff)
        {
            result = 1;
        } else if (o.getDiff() < diff)
        {
            result = -1;
        }
        return result;
    }

    @Override
    public String toString()
    {
        return String.format("Colours Channel for %s. Min %s, Max %s, %sSlope",
                      colourChannel.name(), min, max,
                      downwardsSlope ? "downwards" : "upwards");
    }

}
