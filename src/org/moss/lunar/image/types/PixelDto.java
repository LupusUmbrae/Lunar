package org.moss.lunar.image.types;

import java.rmi.activation.UnknownObjectException;

/**
 * 
 * @author Robin
 * 
 */
public class PixelDto
{
    private int red;
    private int green;
    private int blue;
    private int x;
    private int y;

    public PixelDto()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor for use when getting the pixel from a raster or other reasons
     * for having RGB in a primitive array
     * 
     * @param rgb
     *            RGB array
     * @param x
     *            Column position
     * @param y
     *            Row position
     */
    public PixelDto(int[] rgb, int x, int y)
    {
        red = rgb[0];
        green = rgb[1];
        blue = rgb[2];
        this.x = x;
        this.y = y;
    }

    /**
     * 
     * @param red
     * @param green
     * @param blue
     * @param x
     * @param y
     */
    public PixelDto(int red, int green, int blue, int x, int y)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.x = x;
        this.x = y;
    }

    public int getRed()
    {
        return red;
    }

    public void setRed(int red)
    {
        this.red = red;
    }

    public int getGreen()
    {
        return green;
    }

    public void setGreen(int green)
    {
        this.green = green;
    }

    public int getBlue()
    {
        return blue;
    }

    public void setBlue(int blue)
    {
        this.blue = blue;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getValueFromEnum(RgbEnum channel) throws UnknownObjectException
    {
        int value;
        switch (channel)
        {
        case RED:
            value = red;
            break;
        case GREEN:
            value = green;
            break;
        case BLUE:
            value = blue;
            break;
        default:
            throw new UnknownObjectException("Unknown channel: "
                                             + channel.name());
        }
        return value;
    }

}
