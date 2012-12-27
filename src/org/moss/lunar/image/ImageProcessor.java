package org.moss.lunar.image;

import org.moss.lunar.image.types.PixelDto;

public class ImageProcessor
{

    private Image image;

    public ImageProcessor(Image image)
    {
        this.image = image;
    }

    public void convertImage()
    {
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                PixelDto pixel = image.getPixel(x, y);
            }
        }
    }

}
