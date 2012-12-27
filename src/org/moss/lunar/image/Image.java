package org.moss.lunar.image;

import java.awt.image.Raster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.moss.lunar.image.types.PixelDto;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

/**
 * Class to hold the image for processing
 * 
 * @author Robin
 * 
 */
public class Image
{

    private final File IMAGE_FILE;
    private final Raster IMAGE_RASTER;

    /**
     * Takes the given path and reads in the image. If the given path does not
     * exists and {@link FileNotFoundExceotion} is thrown.
     * 
     * @param imagePath
     *            path to image file
     * @throws IOException
     *             Thrown if there are any IO Errors decoding the image
     */
    public Image(String imagePath) throws IOException
    {
        IMAGE_FILE = new File(imagePath);
        if (!IMAGE_FILE.exists())
        {
            throw new FileNotFoundException(
                                            "Cannot find the image file from the given path. Path: "
                                                    + imagePath);
        }

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", IMAGE_FILE,
                                                         null);
        IMAGE_RASTER = dec.decodeAsRaster();
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

        if (IMAGE_RASTER.getWidth() < x || IMAGE_RASTER.getHeight() < y)
        {
            throw new ArrayIndexOutOfBoundsException(
                                                     "Requested pixel is outside of the image bounds");
        }

        rgb = this.IMAGE_RASTER.getPixel(x, y, rgb);
        return new PixelDto(rgb, x, y);
    }

    /**
     * Return the height in pixels
     * 
     * @return
     */
    public int getHeight()
    {
        return IMAGE_RASTER.getHeight();
    }

    /**
     * Returns the width in pixels
     * 
     * @return
     */
    public int getWidth()
    {
        return IMAGE_RASTER.getWidth();
    }
}
