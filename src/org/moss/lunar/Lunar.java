package org.moss.lunar;

import java.io.IOException;
import java.sql.SQLException;

import org.moss.lunar.analyse.ImageAnalysis;
import org.moss.lunar.image.Image;
import org.moss.lunar.image.types.PixelDto;
import org.moss.lunar.palette.Palette;
import org.moss.lunar.palette.PaletteProcessor;

import com.sun.istack.logging.Logger;

public class Lunar
{

    private static Logger logger = Logger.getLogger(Lunar.class);

    /**
     * @param args
     * @throws SQLException
     * @throws IOException
     */
    public static void main(String[] args) throws SQLException
    {
        boolean runAnalysis = false;
        boolean runConversion = true;

        long failedCount = 0;
        try
        {
            Image image = new Image("resources\\WAC_CSHADE_E000N1800_016P.TIF");
            Palette palette = new Palette("resources\\COLOR_SCALEBAR.TIF");

            PaletteProcessor paletteProcessor = new PaletteProcessor(palette,
                                                                     -9150f,
                                                                     19910f, 5);

            if (runAnalysis)
            {
                ImageAnalysis analyse = new ImageAnalysis(image,
                                                          paletteProcessor);
                analyse.analyseImage("F:\\results");
            }

            if (runConversion)
            {
                PixelDto pixel;
                Float altitude;
                for (int x = 0; x < image.getWidth(); x++)
                {
                    pixel = image.getPixel(x, 0);
                    altitude = paletteProcessor.convertPixel(pixel);

                    System.out.println(String.format("Altitude: %s at %s,%s",
                                                     altitude, x, 0));

                    if (altitude == null)
                    {
                        failedCount++;
                    }
                }
            }

            System.out.println("Failed count:" + failedCount);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
