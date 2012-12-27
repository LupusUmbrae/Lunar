package org.moss.lunar.palette;

import java.awt.image.Raster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.moss.lunar.image.types.PixelDto;
import org.moss.lunar.palette.types.ScalePointDto;
import org.moss.lunar.palette.types.Section;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

/**
 * Class to hold the palette the image will be processed against
 * 
 * @author Robin
 * 
 */
public class Palette
{
    private final File PALETTE_FILE;
    private final Raster PALETTE_RASTER;

    private final List<ScalePointDto> scale;

    private final List<Section> sections;

    /**
     * Takes the given path and reads in the palette. If the given path does not
     * exists and {@link FileNotFoundExceotion} is thrown.
     * 
     * @param palettePath
     *            path to image file
     * @throws IOException
     *             Thrown if there are any IO Errors decoding the image
     */
    public Palette(String palettePath) throws IOException
    {
        PALETTE_FILE = new File(palettePath);
        if (!PALETTE_FILE.exists())
        {
            throw new FileNotFoundException(
                                            "Cannot find the image file from the given path. Path: "
                                                    + palettePath);
        }

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", PALETTE_FILE,
                                                         null);
        PALETTE_RASTER = dec.decodeAsRaster();

        scale = new ArrayList<ScalePointDto>();
        sections = new ArrayList<Section>();
    }

    /**
     * Find the pixel at the given location and return it in a {@link PixelDto}.
     * Checks that the requested row and column are not outside of the image
     * size. Throws a {@link ArrayIndexOutOfBoundsException } if they are.
     * 
     * @param x
     *            Column position of the pixel
     * @param y
     *            Row position of the pixel
     * @return {@link PixelDto} with the RGB and location details of the pixel
     */
    public PixelDto getPixel(int x, int y)
    {
        int[] rgb = null;

        if (PALETTE_RASTER.getWidth() < x || PALETTE_RASTER.getHeight() < y)
        {
            throw new ArrayIndexOutOfBoundsException(
                                                     "Requested pixel is outside of the image bounds");
        }

        rgb = this.PALETTE_RASTER.getPixel(x, y, rgb);
        return new PixelDto(rgb, x, y);
    }

    public int getHeight()
    {
        return PALETTE_RASTER.getHeight();
    }
}
