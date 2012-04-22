package image;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

import org.apache.commons.collections.map.MultiKeyMap;

////String path = "git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
//String path = "C:\\Users\\berrimang\\git\\Lunar\\Lunar\\resources\\COLOR_SCALEBAR.TIF";
//int totalElevation = 19910;
//int startElevationValue	= -9150;
//
//PaletteProcess palette = new PaletteProcess();
//palette.createPalette(path, totalElevation, startElevationValue);

public class PaletteProcess
{
	
	public PaletteProcess() throws IOException
	{
		
	}
	
	//Read in the tiff file
	public void createPalette(String filename, int totalElevation, int startElevationValue) throws IOException
	{	
		MultiKeyMap evaluationMap = new MultiKeyMap();
		//int totalElevation = 19910;
		//int startElevationValue	= -9150;	
		
		File file = new File(filename);
		ImageDecoder dec = ImageCodec.createImageDecoder("png", file, null);	
		Raster image = dec.decodeAsRaster();
		
		//Find the number of pixels.		
		int maxYPixel = image.getHeight();
		int elevationIncrement = totalElevation /  maxYPixel;	
		
		//get the rgb value of each pixel and allocate an elevation to it.		
		int i = startElevationValue;			
		for(int y = 0; y < maxYPixel; y++)
		{								
			int[] rgb = null;		
			rgb = image.getPixel(0, y, rgb);
			
			evaluationMap.put(rgb, i);						
			
			i = i + elevationIncrement;						
		}
						
	}


	
	
	
}
