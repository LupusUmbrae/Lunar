package org.moss.lunar.types;

public class HeightDto extends PixelDto
{

	private Float lat;

	private Float lon;
	
	private Float height;

	public HeightDto(PixelDto pixelDto, Float lat, Float lon, Float height)
	{
		super(pixelDto.getRgb(), pixelDto.getRow(), pixelDto.getColumn());

		this.setLat(lat);
		this.setLon(lon);
		this.setHeight(height);
	}

	public Float getLat()
	{
		return lat;
	}

	public void setLat(Float lat)
	{
		this.lat = lat;
	}

	public Float getLon()
	{
		return lon;
	}

	public void setLon(Float lon)
	{
		this.lon = lon;
	}

	public Float getHeight()
	{
		return height;
	}

	public void setHeight(Float height)
	{
		this.height = height;
	}

}
