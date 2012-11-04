package org.moss.lunar.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.moss.lunar.DataTile;


public class ImageCreateOverlay
{

    private final String OUT_DIR = "resources";

    public File createOverlay(ArrayList<DataTile> dataTiles, float latStep,
                              float lonStep) throws IOException
    {
        File outputImage = new File(this.OUT_DIR + "\\lunar_"
                                    + System.currentTimeMillis() + ".png");
        int imageWidth = (int) (360 / lonStep);
        int imageHeight = (int) (180 / latStep);

        BufferedImage image = new BufferedImage(imageWidth, imageHeight,
                                                BufferedImage.TYPE_INT_RGB);

        for (DataTile dataTile : dataTiles)
        {
            int rank = dataTile.getRank();
            Float lon = dataTile.getLon();
            Float lat = dataTile.getLat() * -1;
            if (lon > 179)
            {
                lon -= 180;
            }
            else
            {
                lon += 180;
            }
            int x = (int) ((lon.intValue()) / lonStep);
            int y = (int) ((lat.intValue() + 89) / latStep);
            try
            {
                image.setRGB(x, y, this.getRgb(rank));
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                System.out.println("x: " + x + ", Y: " + y);
                break;
            }

        }
        ImageIO.write(image, "png", outputImage);
        return outputImage;
    }

    /**
     * Convert the given rank into an RGB value for a basic heatmap colour scale
     * 
     * @param rank
     * @return 6 digit hex RGB value
     */
    private int getRgb(int rank)
    {
        rank = rank < 0 ? 0 : rank;

        Integer rgb;
        int red = 0;
        int green = 0;
        int blue = 0;
        String rgbString;
        String redString;
        String greenString;
        String blueString;

        if (rank <= 25)
        {
            red = 255;
            green = (int) (10.2 * rank);
            blue = 0;
        }
        else if (rank <= 50)
        {
            red = (int) (255 - (10.2 * (rank - 25)));
            green = 255;
            blue = 0;
        }
        else if (rank <= 75)
        {
            red = 0;
            green = 255;
            blue = (int) (10.2 * (rank - 50));
        }
        else if (rank <= 100)
        {
            red = 0;
            green = (int) (255 - (10.2 * (rank - 75)));
            blue = 255;
        }

        redString = String.format("%x", red);
        blueString = String.format("%x", green);
        greenString = String.format("%x", blue);

        redString = redString.length() == 1 ? "0" + redString : redString;
        blueString = blueString.length() == 1 ? "0" + blueString : blueString;
        greenString = greenString.length() == 1 ? "0" + greenString
                                               : greenString;

        rgbString = redString + greenString + blueString;
        rgb = Integer.parseInt(rgbString, 16);
        return rgb;
    }

}
