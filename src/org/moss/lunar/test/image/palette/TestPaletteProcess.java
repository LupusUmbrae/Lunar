package org.moss.lunar.test.image.palette;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.moss.lunar.image.palette.InterpException;
import org.moss.lunar.image.palette.PaletteProcess;

public class TestPaletteProcess extends TestCase
{
    private PaletteProcess palette;
    
    /**
     * 
     */
    @Before
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
    @Test
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
