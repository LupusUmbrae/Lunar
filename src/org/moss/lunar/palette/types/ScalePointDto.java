package org.moss.lunar.palette.types;

import org.moss.lunar.image.types.PixelDto;

public class ScalePointDto
{

    private PixelDto rgbValue;
    private float altitude;

    public ScalePointDto(PixelDto rgbValue, Float altitude)
    {
        this.altitude = altitude;
        this.rgbValue = rgbValue;
    }

    public ScalePointDto(int[] rgb, int x, int y, Float altitude)
    {
        this.rgbValue = new PixelDto(rgb, x, y);
        this.altitude = altitude;
    }

    public PixelDto getRgbValue()
    {
        return rgbValue;
    }

    public void setRgbValue(PixelDto rgbValue)
    {
        this.rgbValue = rgbValue;
    }

    public Float getAltitude()
    {
        return altitude;
    }

    public void setAltitude(Float altitude)
    {
        this.altitude = altitude;
    }

}
