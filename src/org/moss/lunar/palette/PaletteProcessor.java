package org.moss.lunar.palette;

import java.util.ArrayList;
import java.util.List;

import org.moss.lunar.image.types.PixelDto;
import org.moss.lunar.palette.types.ScalePointDto;
import org.moss.lunar.palette.types.Section;

/**
 * Uses the given palette and scale details to create the conversion tool.
 * 
 * @author Robin
 * 
 */
public class PaletteProcessor
{

    private final List<ScalePointDto> scale;

    private final List<Section> sections;

    private final Palette palette;

    private final int maxDiff;

    /**
     * Takes the given palette and generates the scale data and sections from
     * it.
     * 
     * @param palette
     *            A created palette, if this is null will throw a
     *            {@link NullPointerException}
     * @param minElevation
     *            Minimum elevation of the scale
     * @param totalElevation
     *            Total elevation change of the scale
     */
    public PaletteProcessor(Palette palette, Float minElevation,
                            Float totalElevation, int maxDiff)
    {
        if (palette == null)
        {
            throw new NullPointerException(
                                           "Palette cannot be null, it is required");
        }
        this.palette = palette;
        scale = new ArrayList<ScalePointDto>();
        sections = new ArrayList<Section>();
        this.maxDiff = maxDiff;
        createScale(minElevation, totalElevation);
        createSections();
    }

    /**
     * Takes a single column of the scale an associates each RGB value with an
     * altitude.
     * 
     * @param minElevation
     *            Minimum elevation of the scale
     * @param totalElevation
     *            Total elevation change of the scale
     */
    private void createScale(Float minElevation, Float totalElevation)
    {
        Float elevationTick = totalElevation / palette.getHeight();

        for (int i = 0; i < palette.getHeight(); i++)
        {
            PixelDto pixel;
            Float elevation = minElevation + (elevationTick * i);
            pixel = palette.getPixel(0, i);
            scale.add(new ScalePointDto(pixel, elevation));
        }
    }

    /**
     * Creates the palette sections. Each section contains a part of the scale
     * in which one or more colour channels have a linear change.
     */
    private void createSections()
    {
        sections.add(new Section(true, true, true, scale.subList(0, 42), "A"));
        sections.add(new Section(false, true, true, scale.subList(43, 96), "B"));
        sections.add(new Section(false, true, true, scale.subList(97, 135), "C"));
        sections.add(new Section(false, true, true, scale.subList(136, 194),
                                 "D"));
        sections.add(new Section(true, true, true, scale.subList(195, 205), "E"));
        sections.add(new Section(true, true, true, scale.subList(206, 252), "F"));
        sections.add(new Section(true, true, false, scale.subList(253, 292),
                                 "G"));
        sections.add(new Section(true, true, false, scale.subList(293, 388),
                                 "H"));
    }

    /**
     * Attempt to convert the given pixel into an altitude based on its RGB
     * value
     * 
     * @param pixel
     *            The pixel dto to be converted
     * @return Returns the altitude or a null if it is unable to.
     */
    public Float convertPixel(PixelDto pixel)
    {
        // Attempt to find a section the pixel belongs to
        Section possibleSection = null;
        int diff = 0;
        Float altitude = null;

        for (Section section : sections)
        {
            if (section.inSection(pixel))
            {
                possibleSection = section;
                break;
            }
        }

        // If no success try using the set max diff
        if (possibleSection == null)
        {
            for (int i = 1; i <= maxDiff; i++)
            {
                for (Section section : sections)
                {
                    if (section.inSection(pixel, i))
                    {
                        possibleSection = section;
                        diff = i;
                        break;
                    }
                }
                if (possibleSection != null)
                {
                    break;
                }
            }
        }

        // possibleSection.
        if (possibleSection != null)
        {
            altitude = possibleSection.convertPixel(pixel, maxDiff);
        }

        return altitude;
    }

    /**
     * Used to run a test on the inoputted data. Will find which section the
     * pixel may exist in and report back the details
     * 
     * @param pixel
     * @param details
     * @return
     */
    public String testConvertPixel(PixelDto pixel)
    {
        String results;
        Section possibleSection = null;
        int diff = 0;
        for (Section section : sections)
        {
            if (section.inSection(pixel))
            {
                possibleSection = section;
                break;
            }
        }

        if (possibleSection == null)
        {
            for (int i = 1; i <= maxDiff; i++)
            {
                for (Section section : sections)
                {
                    if (section.inSection(pixel, i))
                    {
                        diff = i;
                        possibleSection = section;
                        break;
                    }
                }
                if (possibleSection != null)
                {
                    break;
                }
            }
        }
        results = String.format("%s,%s,%s,%s,%s,'%s','%s','%s'",
                                pixel.getX(),
                                pixel.getY(),
                                pixel.getRed(),
                                pixel.getGreen(),
                                pixel.getBlue(),
                                possibleSection == null ? "N" : "Y",
                                possibleSection == null ? ""
                                                       : possibleSection.toString(),
                                diff);
        return results;
    }

}
