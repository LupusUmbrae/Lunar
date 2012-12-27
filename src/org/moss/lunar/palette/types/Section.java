package org.moss.lunar.palette.types;

import java.util.List;

import org.moss.lunar.image.types.PixelDto;
import org.moss.lunar.image.types.RgbEnum;

public class Section
{

    private boolean red;
    private boolean green;
    private boolean blue;
    private List<ScalePointDto> knownPoints;

    private boolean redSlopeDown = false;
    private boolean greenSlopeDown = false;
    private boolean blueSlopeDown = false;

    private int minRed;
    private int minGreen;
    private int minBlue;

    private int maxRed;
    private int maxGreen;
    private int maxBlue;

    private String name;

    /**
     * Creates the section using the details given.
     * @param red
     * @param green
     * @param blue
     * @param knownPoints
     */
    public Section(boolean red, boolean green, boolean blue,
                   List<ScalePointDto> knownPoints)
    {
        // Store variables
        this.red = red;
        this.blue = blue;
        this.green = green;

        this.knownPoints = knownPoints;

        // Calculate other important variables
        int numberOfPoints = knownPoints.size() - 1;
        int startRed = knownPoints.get(0).getRgbValue().getRed();
        int startGreen = knownPoints.get(0).getRgbValue().getGreen();
        int startBlue = knownPoints.get(0).getRgbValue().getBlue();

        int endRed = knownPoints.get(numberOfPoints).getRgbValue().getRed();
        int endGreen = knownPoints.get(numberOfPoints).getRgbValue().getRed();
        int endBlue = knownPoints.get(numberOfPoints).getRgbValue().getRed();

        redSlopeDown = startRed > endRed;
        greenSlopeDown = startGreen > endGreen;
        blueSlopeDown = startBlue > endBlue;

        minRed = redSlopeDown ? endRed : startRed;
        maxRed = redSlopeDown ? startRed : endRed;

        minGreen = greenSlopeDown ? endGreen : startGreen;
        maxGreen = greenSlopeDown ? startGreen : endGreen;

        minBlue = blueSlopeDown ? endBlue : startBlue;
        maxBlue = blueSlopeDown ? startBlue : endBlue;
        
        // Prioritise the channels
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

        if (red - diff <= maxRed && red + diff >= minRed)
        {
            redInSection = true;
        }
        if (green - diff <= maxGreen && green + diff >= minGreen)
        {
            greenInSection = true;
        }
        if (blue - diff <= maxBlue && blue + diff >= minBlue)
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
