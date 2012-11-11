package org.moss.lunar.types;

public class RgbDto
{
    private int red;
    private int green;
    private int blue;

    public RgbDto(int red, int green, int blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RgbDto(int[] rgb)
    {
        this.red = rgb[0];
        this.green = rgb[1];
        this.blue = rgb[2];
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean equals = false;
        if (obj instanceof RgbDto)
        {
            RgbDto compareTo = (RgbDto) obj;
            if (this.red == compareTo.getRed()
                && this.green == compareTo.getGreen()
                && this.blue == compareTo.getBlue())
            {
                equals = true;
            }
        }

        return equals;
    }

    public int getBlue()
    {
        return this.blue;
    }

    public int getGreen()
    {
        return this.green;
    }

    public int getRed()
    {
        return this.red;
    }

    public int[] getRgb()
    {
        return new int[] { this.red, this.green, this.blue };
    }
}
