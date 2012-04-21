package main;

import java.io.IOException;

import image.ImageProcess;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ImageProcess image = new ImageProcess("C:\\Users\\Robin\\git\\Lunar\\Lunar\\resources\\WAC_CSHADE_E000N1800_016P.TIF");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
