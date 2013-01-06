package org.moss.lunar.palette.types;

import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moss.lunar.image.types.PixelDto;
import org.moss.lunar.image.types.RgbEnum;

public class Section
{

    private ColourChannel redChannel;
    private ColourChannel greenChannel;
    private ColourChannel blueChannel;

    // Prioritised channels
    private ColourChannel channelA;
    private ColourChannel channelB;
    private ColourChannel channelC;

    private List<ScalePointDto> knownPoints;

    private String name;

    private static final int maxInterpDiff = 10;

    /**
     * Creates the section using the details given.
     * 
     * @param red
     *            Is this channel to be considered when converting pixels?
     * @param green
     *            Is this channel to be considered when converting pixels?
     * @param blue
     *            Is this channel to be considered when converting pixels?
     * @param knownPoints
     *            List of {@link ScalePointDto}s containing the RGB->Altitude
     *            known points
     */
    public Section(boolean red, boolean green, boolean blue,
                   List<ScalePointDto> knownPoints)
    {

        if (!red && !green && !blue)
        {
            throw new IllegalArgumentException(
                                               "Cannot have all three channels not applicable");
        }

        this.knownPoints = knownPoints;

        // Calculate other important variables
        int numberOfPoints = knownPoints.size() - 1;
        int startRed = knownPoints.get(0).getRgbValue().getRed();
        int startGreen = knownPoints.get(0).getRgbValue().getGreen();
        int startBlue = knownPoints.get(0).getRgbValue().getBlue();

        int endRed = knownPoints.get(numberOfPoints).getRgbValue().getRed();
        int endGreen = knownPoints.get(numberOfPoints).getRgbValue().getRed();
        int endBlue = knownPoints.get(numberOfPoints).getRgbValue().getRed();

        this.redChannel = new ColourChannel(RgbEnum.RED, startRed, endRed, red);
        this.greenChannel = new ColourChannel(RgbEnum.GREEN, startGreen,
                                              endGreen, green);
        this.blueChannel = new ColourChannel(RgbEnum.BLUE, startBlue, endBlue,
                                             blue);

        // Prioritise the channels

        List<ColourChannel> channels = new ArrayList<ColourChannel>();
        channels.add(redChannel);
        channels.add(greenChannel);
        channels.add(blueChannel);
        Collections.sort(channels);
        channelA = channels.get(0);
        channelB = channels.get(1);
        channelC = channels.get(2);

    }

    /**
     * Creates the section using the details given.
     * 
     * @param red
     *            Is this channel to be considered when converting pixels?
     * @param green
     *            Is this channel to be considered when converting pixels?
     * @param blue
     *            Is this channel to be considered when converting pixels?
     * @param knownPoints
     *            List of {@link ScalePointDto}s containing the RGB->Altitude
     *            known points
     * @param name
     *            A name used in the to string method to help identify this
     *            section
     */
    public Section(boolean red, boolean green, boolean blue,
                   List<ScalePointDto> knownPoints, String name)
    {
        this(red, green, blue, knownPoints);
        this.name = name;
    }

    /**
     * Checks the RGB values of the given {@link PixelDto} to see if it's within
     * the RGB bounds of this section
     * 
     * @param pixel
     *            A {@link PixelDto} to be used
     * @return whether the RGB values are in range
     */
    public boolean inSection(PixelDto pixel)
    {
        return inSection(pixel, 0);
    }

    /**
     * Checks the RGB values of the given {@link PixelDto} to see if it's within
     * the RGB bounds of this section with a +/- diff applied to each value.
     * 
     * @param pixel
     *            A {@link PixelDto} to be used
     * @param diff
     *            The diff to be applied to the values
     * @return whether the RGB values are in range
     */
    public boolean inSection(PixelDto pixel, int diff)
    {
        int red = pixel.getRed();
        int blue = pixel.getBlue();
        int green = pixel.getGreen();
        boolean redInSection = false;
        boolean greenInSection = false;
        boolean blueInSection = false;

        if (red - diff <= redChannel.getMax()
            && red + diff >= redChannel.getMin())
        {
            redInSection = true;
        }
        if (green - diff <= greenChannel.getMax()
            && green + diff >= greenChannel.getMin())
        {
            greenInSection = true;
        }
        if (blue - diff <= blueChannel.getMax()
            && blue + diff >= blueChannel.getMin())
        {
            blueInSection = true;
        }

        return redInSection & greenInSection & blueInSection;
    }

    /**
     * Attempt to convert the given pixel to an altitude value.
     * 
     * @param pixel
     *            {@link PixelDto} to be converted
     * @param maxDiff
     *            The max diff to be applied to the RGB values when converting.
     * @return Returns the altitude or null if it cannot be converted
     */
    public Float convertPixel(PixelDto pixel, int maxDiff)
    {
        Float altitude = null;

        // Attempt to find a match in the known points
        altitude = findMatch(pixel, maxDiff);

        // If null attempt an interpolation on the points
        if (altitude == null)
        {
            try
            {
                altitude = interpolate(pixel, maxDiff);
            } catch (InterpolationException e)
            {
                // Don't need to do anything. should be logged though
                System.out.println(e.getMessage());
            }
        }

        return altitude;
    }

    /**
     * Searches the given channel for the closet point. <br />
     * The closest point is always the position in the list before the value
     * gets too big or small, depending on slope <br />
     * 
     * If a close point cannot be found the method returns null. This true if
     * the return value is equal to the size of the list
     * 
     * @param givenValue
     *            Given value to find
     * @param colourChannel
     *            The current colour channel to look through.
     * @return Either the closest point or a null.
     * @throws UnknownObjectException
     */
    private Integer findClosestPoint(int givenValue, ColourChannel colourChannel)
    {

        Integer returnPoint = -1;
        int curValue;
        boolean downwards = colourChannel.isDownwardsSlope();

        for (int i = 0; i < knownPoints.size(); i++)
        {
            curValue = knownPoints.get(i)
                                  .getRgbValue()
                                  .getChannelValueFromEnum(colourChannel.getChannel());
            if ((downwards && curValue <= givenValue)
                || (!downwards && curValue >= givenValue))
            {
                break;
            }
            returnPoint = i;
        }

        // Check to make sure that the point is valid
        if (returnPoint == -1 || (returnPoint + 1) == knownPoints.size())
        {
            returnPoint = null;
        }

        return returnPoint;
    }

    /**
     * Search through the known points and attempt to find a match for the given
     * {@link PixelDto} in the knownPoints. The max diff is used to adjust the
     * values to improve chances of finding a match.
     * 
     * @param pixel
     *            Given {@link PixelDto} to find a match for
     * @param maxDiff
     *            Maximum diff to be applied to each value
     * @return Altitude. If no match is found a null is returned
     */
    private Float findMatch(PixelDto pixel, int maxDiff)
    {
        int valueA = pixel.getChannelValueFromEnum(channelA.getChannel());
        int valueB = pixel.getChannelValueFromEnum(channelB.getChannel());
        int valueC = pixel.getChannelValueFromEnum(channelC.getChannel());

        int curVal;
        for (ScalePointDto curPoint : knownPoints)
        {
            for (int aDiff = 0; aDiff < maxDiff; aDiff++)
            {
                curVal = curPoint.getRgbValue()
                                 .getChannelValueFromEnum(channelA.getChannel());
                if ((curVal == valueA + aDiff) || (curVal == valueA - aDiff))
                {
                    for (int bDiff = 0; bDiff < maxDiff; bDiff++)
                    {
                        curVal = curPoint.getRgbValue()
                                         .getChannelValueFromEnum(channelB.getChannel());
                        if ((curVal == valueB + bDiff)
                            || (curVal == valueB - bDiff))
                        {
                            for (int cDiff = 0; cDiff < maxDiff; cDiff++)
                            {
                                curVal = curPoint.getRgbValue()
                                                 .getChannelValueFromEnum(channelC.getChannel());
                                if ((curVal == valueC + cDiff)
                                    || (curVal == valueC - cDiff))
                                {
                                    return curPoint.getAltitude();
                                }
                            } // End channel c loop
                        }
                    } // End channel b loop
                }
            } // End channel a loop
        }
        return null;
    }

    /**
     * Attempt to interpolate an altitude for the {@link PixelDto} using the
     * applicable channels. Throws {@link InterpolationException} if it fails.
     * 
     * @param pixel
     * @param maxDiff
     * @return Altitude
     * @throws InterpolationException
     */
    private Float interpolate(PixelDto pixel, int maxDiff)
                                                          throws InterpolationException
    {
        /*
         * Due to the compare in ColourChannel applicable/enabled channels will
         * be first in the list. This means that it's possible to assume that
         * previous channels will have been set by findClosestPoint
         */
        Integer pointA = 0;
        Integer pointB = 0;
        Integer pointC = 0;

        int channels = 0;

        float altitude = 0;

        if (channelA.isEnabled())
        {
            pointA = findClosestPoint(pixel.getChannelValueFromEnum(channelA.getChannel()),
                                      channelA);
            if (pointA == null)
            {
                throw new InterpolationException(
                                                 String.format("Failed to find a point for the %s channel",
                                                               channelA.getChannel()));
            }

            altitude += linearInteroplation(pixel.getChannelValueFromEnum(channelA.getChannel()),
                                            pointA, channelA);
            channels++;
        }

        if (channelB.isEnabled())
        {
            pointB = findClosestPoint(pixel.getChannelValueFromEnum(channelB.getChannel()),
                                      channelB);
            if (pointB == null)
            {
                throw new InterpolationException(
                                                 String.format("Failed to find a point for the %s channel",
                                                               channelB.getChannel()));
            }

            if (((pointA - pointB) > maxInterpDiff)
                || ((pointA - pointB) < maxInterpDiff))
            {
                throw new InterpolationException(
                                                 "Points between A and B are too big");
            }
            altitude += linearInteroplation(pixel.getChannelValueFromEnum(channelB.getChannel()),
                                            pointB, channelB);
            channels++;
        }

        if (channelC.isEnabled())
        {
            pointC = findClosestPoint(pixel.getChannelValueFromEnum(channelC.getChannel()),
                                      channelC);
            if (pointC == null)
            {
                throw new InterpolationException(
                                                 String.format("Failed to find a point for the %s channel",
                                                               channelC.getChannel()));
            }

            if (((pointB - pointC) > maxInterpDiff)
                || ((pointB - pointC) < maxInterpDiff))
            {
                throw new InterpolationException(
                                                 "Points between B and C are too big");
            }

            if (((pointA - pointC) > maxInterpDiff)
                || ((pointA - pointC) < maxInterpDiff))
            {
                throw new InterpolationException(
                                                 "Points between A and C are too big");
            }
            altitude += linearInteroplation(pixel.getChannelValueFromEnum(channelC.getChannel()),
                                            pointC, channelC);
            channels++;
        }

        // Currently altitude is the total for each number of channels
        // interpolated. Divide the total by the number of channels to get an
        // altitude
        altitude = altitude / channels;
        return altitude;
    }

    private Float linearInteroplation(int colourValue, int position,
                                      ColourChannel channel)
    {
        float startAlt;
        float endAlt;

        int steps;
        int xDiff;
        float gradient;
        float altitude;

        int startPoint = knownPoints.get(position)
                                    .getRgbValue()
                                    .getChannelValueFromEnum(channel.getChannel());
        int endPoint = knownPoints.get(position + 1)
                                  .getRgbValue()
                                  .getChannelValueFromEnum(channel.getChannel());

        if (channel.isDownwardsSlope())
        {
            steps = startPoint - endPoint;
            xDiff = colourValue - endPoint;
        } else
        {
            steps = endPoint - startPoint;
            xDiff = endPoint - colourValue;
        }

        startAlt = knownPoints.get(position).getAltitude();
        endAlt = knownPoints.get(position + 1).getAltitude();

        gradient = (endAlt - startAlt) / steps;

        altitude = (gradient * xDiff) + startAlt;

        return altitude;
    }

    @Override
    public String toString()
    {
        if (this.name != null)
        {
            return name;
        }
        return super.toString();
    }
}
