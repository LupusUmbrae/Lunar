package image.palette;

import image.RGB;

import java.util.ArrayList;

import org.apache.commons.collections.map.ListOrderedMap;

public class Section
{

    private final int[] min;
    private final int[] max;

    private boolean red;
    private boolean green;
    private boolean blue;

    private boolean redDownwards;
    private boolean greenDownwards;
    private boolean blueDownwards;

    private final ArrayList<Integer> knownRed;
    private final ArrayList<Integer> knownGreen;
    private final ArrayList<Integer> knownBlue;

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
    public Section(boolean red, boolean green,
                   boolean blue, ListOrderedMap knownPoints)
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

        int redFirst = first.getRed();
        int greenFirst = first.getGreen();
        int blueFirst = first.getBlue();
        int redLast = last.getRed();
        int greenLast = last.getGreen();
        int blueLast = last.getBlue();

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
        if (redFirst > redLast)
        {
            redLast = first.getRed();
            redFirst = last.getRed();
        }
        if (greenFirst > greenLast)
        {
            greenLast = first.getGreen();
            greenFirst = last.getGreen();
        }
        if (blueFirst > blueLast)
        {
            blueLast = first.getBlue();
            blueFirst = last.getBlue();
        }

        this.min = new int[] { redFirst, greenFirst, blueFirst };
        this.max = new int[] { redLast, greenLast, blueLast };
    }

    /**
     * From the given first and last RGB work out the maximum and minimum RGB
     * values to allow into this section
     * 
     * @param first
     *            RGB first RGB key in the known points
     * @param last
     *            RGB last RGB key in the known points
     */
    private void findMaxMinValues(RGB first, RGB last)
    {

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
    private int[] findCloesetPoints(int point, ArrayList<Integer> knownPoints,
                                    boolean downwards) throws InterpException
    {
        int positionLast = 300;
        int position = 0;

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

        if (positionLast == 300 || position == positionLast)
        {
            throw new InterpException(
                                      "Given point is too big/small for this section");
        }

        return new int[] { positionLast, position };
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
        RGB rgbObject = new RGB(rgb);
        if (this.knownPoints.containsKey(rgbObject))
        {
            this.knownPoints.getValue(this.knownPoints.indexOf(rgbObject));
        }

        if (this.red)
        {
            if (this.knownRed.contains(rgb[0]))
            {
                // its complicated
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
                // its complicated
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
                // its complicated
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
            throw new InterpException("No Channels set");
        }

        altitude /= channels;

        return altitude;
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
                                ArrayList<Integer> searchPoints,
                                boolean downwards) throws InterpException
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

}
