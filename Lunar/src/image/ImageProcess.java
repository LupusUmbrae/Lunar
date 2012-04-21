package image;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;

import org.apache.commons.collections.map.MultiKeyMap;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;

public class ImageProcess {

	private int pixelHeight;
	private int pixelWidth;
	
	private MultiKeyMap heightMap;
	
	private Raster image;
	
	public ImageProcess(String filename) throws IOException {
		loadImage(filename);
		convertImage();
	}

	private void loadImage(String filename) throws IOException {

		File file = new File(filename);

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);
		
		image = dec.decodeAsRaster();
		
		pixelHeight = image.getHeight();
		pixelWidth = image.getWidth();
		
	}
	
	private void convertImage(){
		
		heightMap = new MultiKeyMap();
		
		for(int x = 0; x < pixelWidth; x++){
			for(int y = 0; y < pixelHeight; y++){
				int[] rgb = null; 
				rgb = image.getPixel(x, y, rgb);
				Integer myInt = 1000;
				heightMap.put(x, y, myInt);
			}
		}
		System.out.println("hi");
	}
}
