package org.moss.lunar.image.threads;

import java.awt.image.Raster;

import org.moss.lunar.types.PixelDto;
import org.moss.lunar.types.RowDto;
import org.moss.lunar.types.exceptions.ListFullException;

import com.sun.istack.logging.Logger;

/**
 * Convert the given image into pixel data
 * 
 * @author Robin
 * 
 */
public class ConvertImage implements Runnable
{

	private Integer pixelHeight;

	private Integer pixelWidth;

	private Raster image;

	private Logger logger = Logger.getLogger(ConvertImage.class);

	public ConvertImage(Integer pixelHeight, Integer pixelWidth, Raster image)
	{
		this.pixelHeight = pixelHeight;
		this.pixelWidth = pixelWidth;
		this.image = image;
	}

	@Override
	public void run()
	{
		for (int y = 0; y < this.pixelHeight; y++)
		{
			RowDto<PixelDto> rowDto = new RowDto<PixelDto>();
			for (int x = 0; x < this.pixelWidth; x++)
			{

				processPixel(y, rowDto, x);
			}
			saveData(rowDto);
		}
		logger.info("Image read in");
	}

	private void saveData(RowDto<PixelDto> rowDto)
	{
		boolean dataSaved = false;
		while (!dataSaved)
		{
			try
			{
				if (!ImageStorage.pixelRowIsFull())
				{
					ImageStorage.addPixelRow(rowDto);
					dataSaved = true;
				} else
				{
					this.sleep();
				}
			} catch (ListFullException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dataSaved = false;
	}

	private void processPixel(int y, RowDto<PixelDto> rowDto, int x)
	{
		int[] rgb = null;

		rgb = this.image.getPixel(x, y, rgb);

		PixelDto pixel = new PixelDto(rgb, y, x);
		rowDto.addItem(pixel);
	}

	private void sleep()
	{
		try
		{
			Thread.sleep(500L);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
