package test.image.palette;

import java.io.IOException;

import image.palette.InterpException;
import image.palette.PaletteProcess;
import junit.framework.TestCase;

public class TestPaletteProcess extends TestCase
{
    private PaletteProcess palette;
    
    /**
     * 
     */
    public void setUp(){
        palette = new PaletteProcess();
        String path = "resources\\COLOR_SCALEBAR.TIF";
        Float totalElevation = 19910f;
        Float startElevationValue = -9150f;
        try
        {
            palette.createPalette(path, totalElevation, startElevationValue);
        }
        catch (IOException e)
        {
           fail(e.getMessage());
        }
    }
    
    /**
     * 
     */
    public void testPaletteProcess()
    {
        int[] testPoint = new int[] { 243, 51, 38 };
        float altitude = 0;
        try
        {
            altitude = palette.convertRgb(testPoint);
        }
        catch (InterpException e)
        {
            fail(e.getMessage());
        }
        
        assertEquals("Altitude isnt right :(", -4065.8601f, altitude, 0);
    }
}
