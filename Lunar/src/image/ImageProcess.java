package image;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;

import org.apache.commons.collections.map.MultiKeyMap;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;

public class ImageProcess {

	private int pixelHeight;
	private int pixelWidth;
	
	private MultiKeyMap heightMap;
	
	private NullOpImage image;
	
	public ImageProcess(String filename) throws IOException {
		loadImage(filename);
		convertImage();
	}

	private void loadImage(String filename) throws IOException {

		File file = new File(filename);

		ImageDecoder dec = ImageCodec.createImageDecoder("tiff", file, null);
		
		RenderedImage asd = dec.decodeAsRenderedImage();
		int[] rgb = null;
		asd.getTile(0, 0).getPixel(0, 0, rgb);
		NullOpImage image = new NullOpImage(dec.decodeAsRenderedImage(0), null, null,OpImage.OP_COMPUTE_BOUND);
		
		pixelHeight = image.getHeight();
		pixelWidth = image.getWidth();
		
	}
	
	private void convertImage(){
		
//		heightMap = new MultiKeyMap();
//		
//		for(int x = 0; x < pixelHeight; x++){
//			for(int y = 0; y < pixelWidth; y++){
//				int rgb = image.getRGB(x, y);
//				heightMap.put(x, y, rgb);
//			}
//		}
	}
}
