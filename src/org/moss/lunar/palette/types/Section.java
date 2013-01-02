package org.moss.lunar.palette.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moss.lunar.image.types.PixelDto;
import org.moss.lunar.image.types.RgbEnum;

public class Section
{

    private boolean redApplicable;
    private boolean greenApplicable;
    private boolean blueApplicable;

    private ColourChannel redChannel;
    private ColourChannel greenChannel;
    private ColourChannel blueChannel;

    // Prioritised channels
    private ColourChannel channelA;
    private ColourChannel channelB;
    private ColourChannel channelC;

    private List<ScalePointDto> knownPoints;

    private String name;

    /**
     * Creates the section using the details given.
     * 
     * @param red
     * @param green
     * @param blue
     * @param knownPoints
     */
    public Section(boolean red, boolean green, boolean blue,
                   List<ScalePointDto> knownPoints)
    {
        // Store variables
        this.redApplicable = red;
        this.blueApplicable = blue;
        this.greenApplicable = green;

        this.knownPoints = knownPoints;

        // Calculate other important variables
        int numberOfPoints = knownPoints.size() - 1;
        int startRed = knownPoints.get(0).getRgbValue().getRed();
        int startGreen = knownPoints.get(0).getRgbValue().getGreen();
        int startBlue = knownPoints.get(0).getRgbValue().getBlue();

        int endRed = knownPoints.get(numberOfPoints).getRgbValue().getRed();
        int endGreen = knownPoints.get(numberOfPoints).getRgbValue().getRed();
        int endBlue = knownPoints.get(numberOfPoints).getRgbValue().getRed();

        this.redChannel = new ColourChannel(RgbEnum.RED, startRed, endRed);
        this.greenChannel = new ColourChannel(RgbEnum.GREEN, startGreen,
                                              endGreen);
        this.blueChannel = new ColourChannel(RgbEnum.BLUE, startBlue, endBlue);

        // Prioritise the channels

        List<ColourChannel> channels = new ArrayList<ColourChannel>();
        channels.add(redChannel);
        channels.add(greenChannel);
        channels.add(blueChannel);
        Collections.sort(channels);
        channelA = channels.get(0);
        channelB = channels.get(1);
        channelC = channels.get(2);
        
        System.out.println(channelA.toString());
        System.out.println(channelB.toString());
        System.out.println(channelC.toString());
    }

    public Section(boolean red, boolean green, boolean blue,
                   List<ScalePointDto> knownPoints, String name)
    {
        this(red, green, blue, knownPoints);
        this.name = name;
    }

    public boolean inSection(PixelDto pixel)
    {
        return inSection(pixel, 0);
    }

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

    public Float convertPixel(PixelDto pixel, int diff)
    {

        return null;
    }

    private int findClosestLowerPoint(int givenPoint,
                                      List<ScalePointDto> knownPoints,
                                      boolean downwardsSlope, RgbEnum channel)
    {

        return 0;
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
