package image.palette;

import image.RGB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.map.ListOrderedMap;

public class Section
{

    private final int[] min;
    private final int[] max;

    private final boolean red;
    private final boolean green;
    private final boolean blue;

    private boolean redDownwards;
    private boolean greenDownwards;
    private boolean blueDownwards;

    private final List<Integer> knownRed;
    private final List<Integer> knownGreen;
    private final List<Integer> knownBlue;

    private final ListOrderedMap knownPoints;

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
    public Section(boolean red, boolean green, boolean blue,
                   ListOrderedMap knownPoints)
    {
        //
        // Assign class variables
        //
        this.red = red;
        this.green = green;
        this.blue = blue;

        this.knownPoints = knownPoints;

        this.knownRed = new ArrayList<Integer>();
        this.knownGreen = new ArrayList<Integer>();
        this.knownBlue = new ArrayList<Integer>();

        //
        // Constructor variables
        //

        RGB first = (RGB) knownPoints.firstKey();
        RGB last = (RGB) knownPoints.lastKey();

        int redFirst;
        int greenFirst;
        int blueFirst;
        int redLast;
        int greenLast;
        int blueLast;

        List<Integer> points = new ArrayList<Integer>();

        //
        // Get a list of known colours into array lists
        //
        for (Object key : knownPoints.keySet())
        {
            RGB currentRgb = (RGB) key;
            this.knownRed.add(currentRgb.getRed());
            this.knownGreen.add(currentRgb.getGreen());
            this.knownBlue.add(currentRgb.getBlue());
        }

        //
        // Determine each channels slope
        //
        if (red)
        {
            this.redDownwards = first.getRed() > last.getRed() ? true : false;
        }
        if (green)
        {
            this.greenDownwards = first.getGreen() > last.getGreen() ? true
                                                                    : false;
        }
        if (blue)
        {
            this.blueDownwards = first.getBlue() > last.getBlue() ? true
                                                                 : false;
        }

        //
        // Find max/min RGB's for this section
        //

        points = new ArrayList<Integer>(knownRed);
        Collections.sort(points);
        redFirst = points.get(0);
        redLast = points.get(points.size() - 1);

        points = new ArrayList<Integer>(knownGreen);
        Collections.sort(points);
        greenFirst = points.get(0);
        greenLast = points.get(points.size() - 1);

        points = new ArrayList<Integer>(knownBlue);
        Collections.sort(points);
        blueFirst = points.get(0);
        blueLast = points.get(points.size() - 1);

        this.min = new int[] { redFirst, greenFirst, blueFirst };
        this.max = new int[] { redLast, greenLast, blueLast };
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
    private int[] findCloesetPoints(int point, List<Integer> knownPoints,
                                    boolean downwards) throws InterpException
    {
        int positionLast = -1;
        int position = -1;

        for (Integer curPoint : knownPoints)
        {
            position = knownPoints.indexOf(curPoint);
            if ((downwards && curPoint < point)
                || (!downwards && curPoint > point))
            {
                break;
            }
            positionLast = position;
        }

        if (positionLast == -1 || position == positionLast)
        {
            throw new InterpException(
                                      "Given point is too big/small for this section");
        }

        return new int[] { positionLast, position };
    }

    private List<Integer> findPoints(int point, List<Integer> knownPoints)
    {
        ArrayList<Integer> positions = new ArrayList<Integer>();
        if (knownPoints.contains(point))
        {
            for (int i = 0; i < knownPoints.size(); i++)
            {
                if (knownPoints.get(i) == point)
                {
                    positions.add(i);
                }
            }
        }
        return positions;
    }

    /**
     * 
     * @param rgb
     * @return
     */
    public boolean inSection(int[] rgb)
    {
        boolean inSection = false;
        if ((this.min[0] <= rgb[0] && rgb[0] <= this.max[0])
            && (this.min[1] <= rgb[1] && rgb[1] <= this.max[1])
            && (this.min[2] <= rgb[2] && rgb[2] <= this.max[2]))
        {
            inSection = true;
        }
        return inSection;
    }

    /**
     * 
     * @param rgb
     * @param diff
     * @return
     */
    public boolean inSection(int[] rgb, int diff)
    {
        boolean inSectionRed = false;
        boolean inSectionGreen = false;
        boolean inSectionBlue = false;

        int redMin = this.min[0];
        int greenMin = this.min[1];
        int blueMin = this.min[2];
        int redMax = this.max[0];
        int greenMax = this.max[1];
        int blueMax = this.max[2];

        int red = rgb[0];
        int green = rgb[1];
        int blue = rgb[2];

        if ((redMin <= red || redMin <= red + diff)
            && (redMax >= red || redMax <= red - diff))
        {
            inSectionRed = true;
        }

        if ((greenMin <= green || greenMin <= green + diff)
            && (greenMax >= green || greenMax <= green - diff))
        {
            inSectionGreen = true;
        }

        if ((blueMin <= blue || blueMin <= blue + diff)
            && (blueMax >= blue || blueMax <= blue - diff))
        {
            inSectionBlue = true;
        }

        return inSectionRed && inSectionGreen && inSectionBlue;
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
    public float process(int[] rgb, boolean exact) throws InterpException
    {
        // If we got here by using a difference in a channel bring that channel
        // into the bounds of this section.
        //
        // Maybe look at not doing this...
        if (!exact)
        {
            int red = rgb[0];
            int green = rgb[1];
            int blue = rgb[2];

            if (red > this.max[0])
            {
                red = this.max[0];
            }
            else if (red < this.min[0])
            {
                red = this.min[0];
            }

            if (green > this.max[1])
            {
                green = this.max[1];
            }
            else if (green < this.min[1])
            {
                green = this.min[1];
            }

            if (blue > this.max[2])
            {
                blue = this.max[2];
            }
            else if (blue < this.min[2])
            {
                blue = this.min[2];
            }

            rgb = new int[] { red, green, blue };
        }

        int channels = 0;

        float altitude = 0f;
        RGB rgbObject = new RGB(rgb);
        if (this.knownPoints.containsKey(rgbObject))
        {
            return (Float) this.knownPoints.getValue(this.knownPoints.indexOf(rgbObject));
        }

        List<Integer> redPositions;
        List<Integer> greenPositions;
        List<Integer> bluePositions;

        if (this.red)
        {
            if (this.knownRed.contains(rgb[0]))
            {
                redPositions = this.findPoints(rgb[0], this.knownRed);
                if (redPositions.size() == 1)
                {
                    altitude += this.knownRed.get(redPositions.get(0));
                }
                else
                // Like it would be that easy!!
                {
                    greenPositions = this.findPoints(rgb[1], this.knownRed);
                    bluePositions = this.findPoints(rgb[2], this.knownRed);
                }
            }
            else
            {
                altitude += this.processChanel(rgb[0], this.knownRed,
                                               this.redDownwards);
                channels++;
            }

        }
        if (this.green)
        {
            if (this.knownGreen.contains(rgb[1]))
            {
                greenPositions = this.findPoints(rgb[0], this.knownRed);
                if (greenPositions.size() == 1)
                {
                    altitude += this.knownRed.get(greenPositions.get(0));
                }
            }
            else
            {
                altitude += this.processChanel(rgb[1], this.knownGreen,
                                               this.greenDownwards);
                channels++;
            }
        }
        if (this.blue)
        {
            if (this.knownBlue.contains(rgb[2]))
            {
                bluePositions = this.findPoints(rgb[0], this.knownRed);
                if (bluePositions.size() == 1)
                {
                    altitude += this.knownRed.get(bluePositions.get(0));
                }
            }
            else
            {
                altitude += this.processChanel(rgb[2], this.knownBlue,
                                               this.blueDownwards);
                channels++;
            }
        }

        if (channels == 0)
        {
            return -20000f;
            // throw new InterpException("No Channels set");
        }

        altitude /= channels;

        return altitude;
    }

    public String toString()
    {
        String classDetails = String.format("min: %s,%s,%s \nmax: %s,%s,%s \nRed: %s \nGreen: %s \nBlue: %s",
                                            min[0], min[1], min[2], max[0],
                                            max[1], max[2], red, green, blue);

        return classDetails;
    }

    /**
     * The actual implementation of the maths behind y=mx+c. This method will
     * run the analysis for the given values
     * 
     * @param colourValue
     * @param searchPoints
     * @param downwards
     * @return
     * @throws InterpException
     */
    private float processChanel(Integer colourValue,
                                List<Integer> searchPoints, boolean downwards)
                                                                              throws InterpException
    {
        int[] positions;

        // Start/End points in the rgb list
        int startPoint;
        int endPoint;

        // Start/End Indexs;
        int startIndex;
        int endIndex;

        // Start/End altitudes in the rgb list
        float startAlt;
        float endAlt;

        // Used in equation
        int steps;
        int xDiff;
        float gradient;
        float altitude;

        positions = this.findCloesetPoints(colourValue, searchPoints, downwards);

        startIndex = positions[0];
        endIndex = positions[1];
        startPoint = searchPoints.get(startIndex);
        endPoint = searchPoints.get(endIndex);
        if (downwards)
        {
            steps = startPoint - endPoint;
            xDiff = colourValue - endPoint;
        }
        else
        {
            steps = endPoint - startPoint;
            xDiff = endPoint - colourValue;
        }

        startAlt = (Float) this.knownPoints.getValue(startIndex);
        endAlt = (Float) this.knownPoints.getValue(endIndex);
        gradient = (endAlt - startAlt) / steps;
        altitude = (gradient * xDiff) + startAlt;

        return altitude;
    }

    private float findClosestAltitude()
    {

        return 0f;  
    }

}
