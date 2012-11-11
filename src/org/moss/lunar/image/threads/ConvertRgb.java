package org.moss.lunar.image.threads;

import java.util.concurrent.atomic.AtomicBoolean;

import org.moss.lunar.Lunar;
import org.moss.lunar.image.palette.InterpException;
import org.moss.lunar.image.palette.PaletteProcess;
import org.moss.lunar.types.HeightDto;
import org.moss.lunar.types.PixelDto;
import org.moss.lunar.types.RowDto;
import org.moss.lunar.types.exceptions.ListFullException;

import com.sun.istack.logging.Logger;

public class ConvertRgb implements Runnable
{

	private static AtomicBoolean running = new AtomicBoolean(true);

	private Float pixelLat;

	private Float pixelLon;

	private Logger logger = Logger.getLogger(ConvertRgb.class);

	private PaletteProcess palette;

	public ConvertRgb(Float pixelLon, Float pixelLat, PaletteProcess palette)
	{
		this.pixelLat = pixelLat;
		this.pixelLon = pixelLon;
		this.palette = palette;
	}

	public void run()
	{
		RowDto<PixelDto> pixelRowDto;
		RowDto<HeightDto> heightRowDto;

		while (running.get() || ImageStorage.pixelRowHasNext())
		{
			pixelRowDto = ImageStorage.getPixelRow();

			if (pixelRowDto != null)
			{
				heightRowDto = new RowDto<HeightDto>();
				while (pixelRowDto.hasMore())
				{
					processPixel(pixelRowDto, heightRowDto);
				}
				saveData(heightRowDto);
			} else
			{
				this.sleep();
			}
		}
		logger.info("Convert Rgb Thread ending");
	}

	private void processPixel(RowDto<PixelDto> pixelRowDto,
								RowDto<HeightDto> heightRowDto)
	{
		PixelDto pixelDto;
		HeightDto heightDto;
		Float lat;
		Float lon;
		Float height;
		try
		{
			pixelDto = pixelRowDto.getNextItem();
			lat = Lunar.LAT_MIN + (this.pixelLat * pixelDto.getRow());
			lon = Lunar.LON_MIN + (this.pixelLon * pixelDto.getColumn());

			height = this.palette.convertRgb(pixelDto.getRgb());

			heightDto = new HeightDto(pixelDto, lat, lon, height);
			heightRowDto.addItem(heightDto);
		} catch (InterpException e)
		{
			logger.warning("Interperation Exception caught");
			e.printStackTrace();
		}
	}

	private void saveData(RowDto<HeightDto> heightRowDto)
	{
		boolean dataSaved = false;
		while (!dataSaved)
		{
			try
			{
				if (!ImageStorage.heightRowIsFull())
				{
					ImageStorage.addheightRow(heightRowDto);
					ImageStorage.pixelRowRemove(0);
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

	public static void stopConvertRgb()
	{
		running.set(false);
	}

}
