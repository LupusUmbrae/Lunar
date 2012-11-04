package image.palette;

import image.RGB;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.collections.map.MultiKeyMap;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

////String path = "git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
//String path = "C:\\Users\\berrimang\\git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
//int totalElevation = 19910;
//int startElevationValue	= -9150;
//
//PaletteProcess palette = new PaletteProcess();
//palette.createPalette(path, totalElevation, startElevationValue);

/**
 * Class to handle the palette for the image to be processed
 * 
 * @author Robin
 * 
 */
public class PaletteProcess
{

    private MultiKeyMap evaluationMap = new MultiKeyMap();
    // private HashMap<Integer, ArrayList<Integer>> redMap = new
    // HashMap<Integer, ArrayList<Integer>>();
    // private MultiKeyMap greenMap = new MultiKeyMap();

    private ArrayList<int[]> palette;
    private ArrayList<Float> altitudes;

    private ArrayList<Section> sections;

    public PaletteProcess()
    {

    }

    /**
     * Creates the tool for processing pixels based on the given palette image
     * 
     * @param paletteFile
     *            absolute path to the palette image file. Including file name
     * @param totalElevation
     *            total elevation in meters
     * @param startElevationValue
     *            minimum elevation in meters
     * @throws IOException
     */
    public void createPalette(String paletteFile, Float totalElevation,
                              Float startElevationValue) throws IOException
    {

        // int totalElevation = 19910;
        // int startElevationValue = -9150;

        palette = new ArrayList<int[]>();
        altitudes = new ArrayList<Float>();

        File file = new File(paletteFile);
        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);
        Raster image = dec.decodeAsRaster();

        // Find the number of pixels.
        int maxYPixel = image.getHeight();
        Float elevationIncrement = totalElevation / maxYPixel;

        // get the rgb value of each pixel and allocate an elevation to it.
        Float i = startElevationValue;
        for (int y = 0; y < maxYPixel; y++)
        {
            int[] rgb = null;
            rgb = image.getPixel(3, y, rgb);

            palette.add(rgb);
            altitudes.add(i);
            evaluationMap.put(rgb[0], rgb[1], rgb[2], i);

            i = i + elevationIncrement;
        }
        this.createPaletteSections();
    }

    /**
     * 
     * @param rgbString
     * @return
     * @throws Exception
     */
    public Float convertRgb(String[] rgbString) throws Exception
    {
        int red = Integer.parseInt(rgbString[0]);
        int green = Integer.parseInt(rgbString[1]);
        int blue = Integer.parseInt(rgbString[2]);

        if (evaluationMap.containsKey(red, green, blue))
        {
            return (Float) evaluationMap.get(red, green, blue);
        }
        else
        {
            int[] rgb = new int[] { red, green, blue };
            return convertRgb(rgb);
        }
    }

    /**
     * 
     * @param rgb
     * @return
     * @throws InterpException
     */
    public Float convertRgb(int[] rgb) throws InterpException
    {
        for (Section paletteSection : sections)
        {
            if (paletteSection.inSection(rgb))
            {
                return paletteSection.process(rgb, true);
            }
        }

        int diff = 0;
        while (diff < 5)
        {
            diff++;
            for (Section paletteSection : sections)
            {
                if (paletteSection.inSection(rgb, diff))
                {
                    return paletteSection.process(rgb, false);
                }
            }
        }

        return null;
        // throw new InterpException("Not in any sections :S. " + rgb[0] + "," +
        // rgb[1] + "," + rgb[2]);
    }

    /**
     * This will: Takes the palate and the altitudes array lists and checks the
     * deltas determining the points at which the colours change and under which
     * channels analysis should be run. Currently hard coded
     */
    private void createPaletteSections()
    {
        List<int[]> sectionRgb;
        List<Float> sectionAltitudes;
        Section section;

        sections = new ArrayList<Section>();

        // Section A 0-42
        sectionRgb = palette.subList(0, 42);
        sectionAltitudes = altitudes.subList(0, 42);
        section = processSection(sectionRgb, sectionAltitudes, true, true, true);
        sections.add(section);

        // Section b 43-96
        sectionRgb = palette.subList(43, 96);
        sectionAltitudes = altitudes.subList(43, 96);
        section = processSection(sectionRgb, sectionAltitudes, false, true,
                                 true);
        sections.add(section);

        // Section c 97-135
        sectionRgb = palette.subList(97, 135);
        sectionAltitudes = altitudes.subList(97, 135);
        section = processSection(sectionRgb, sectionAltitudes, false, true,
                                 true);
        sections.add(section);

        // Section d 136-194
        sectionRgb = palette.subList(136, 194);
        sectionAltitudes = altitudes.subList(136, 194);
        section = processSection(sectionRgb, sectionAltitudes, false, true,
                                 true);
        sections.add(section);

        // Section e 195-205
        sectionRgb = palette.subList(195, 205);
        sectionAltitudes = altitudes.subList(195, 205);
        section = processSection(sectionRgb, sectionAltitudes, true, true, true);
        sections.add(section);

        // Section f 206-252
        sectionRgb = palette.subList(206, 252);
        sectionAltitudes = altitudes.subList(206, 252);
        section = processSection(sectionRgb, sectionAltitudes, true, true, true);
        sections.add(section);

        // Section g 253-292
        sectionRgb = palette.subList(253, 292);
        sectionAltitudes = altitudes.subList(253, 292);
        section = processSection(sectionRgb, sectionAltitudes, true, true,
                                 false);
        sections.add(section);

        // Section h 293-388
        sectionRgb = palette.subList(293, 388);
        sectionAltitudes = altitudes.subList(293, 388);
        section = processSection(sectionRgb, sectionAltitudes, true, true,
                                 false);
        sections.add(section);
    }

    /**
     * Takes sub sections of the palette and converts it into a Secton class
     * 
     * @param sectionRgb
     * @param sectionAltitude
     * @param red
     * @param green
     * @param blue
     * @return
     */
    private Section processSection(List<int[]> sectionRgb,
                                   List<Float> sectionAltitude, boolean red,
                                   boolean green, boolean blue)
    {
        ListOrderedMap knownPoints = new ListOrderedMap();

        for (int i = 0; i < sectionRgb.size(); i++)
        {
            int[] rgb = sectionRgb.get(i);
            Float altitude = sectionAltitude.get(i);
            RGB currentRgb = new RGB(rgb[0], rgb[1], rgb[2]);
            knownPoints.put(currentRgb, altitude);
        }

        return new Section(red, green, blue, knownPoints);
    }
}
