package org.moss.lunar.test.image.palette;

import junit.framework.TestCase;

import org.apache.commons.collections.map.ListOrderedMap;
import org.junit.Before;
import org.junit.Test;
import org.moss.lunar.image.palette.InterpException;
import org.moss.lunar.image.palette.Section;
import org.moss.lunar.types.RgbDto;

public class TestSection extends TestCase
{

    @Before
    public void setUp() throws InterpException
    {

    }

    /**
     * Expected results = -6667.643
     * 
     * @throws InterpException
     */
    @Test
    public void testDownSlope() throws InterpException
    {
        ListOrderedMap knownPoints = new ListOrderedMap();

        Section testInterp;

        knownPoints.put(new RgbDto(new int[] { 238, 215, 215 }), -6744.417f);
        knownPoints.put(new RgbDto(new int[] { 238, 210, 210 }), -6693.2344f);
        knownPoints.put(new RgbDto(new int[] { 238, 206, 206 }), -6642.052f);
        knownPoints.put(new RgbDto(new int[] { 239, 201, 201 }), -6590.869f);

        testInterp = new Section(false, true, true, knownPoints);

        float testResult;
        boolean inSection;
        int[] testPoint = new int[] { 238, 208, 208 };

        inSection = testInterp.inSection(testPoint);
        assertTrue("In Section call returned false", inSection);

        testResult = testInterp.process(testPoint, true);
        assertEquals("Float isnt right :(", -6667.643f, testResult, 0);
    }

    public void testUpSlope() throws InterpException
    {
        ListOrderedMap knownPoints = new ListOrderedMap();
        Section testInterp;

        knownPoints.put(new RgbDto(new int[] { 243, 47, 39 }), -4134.1035f);
        knownPoints.put(new RgbDto(new int[] { 243, 49, 38 }), -4082.921f);
        knownPoints.put(new RgbDto(new int[] { 242, 52, 38 }), -4031.7383f);
        knownPoints.put(new RgbDto(new int[] { 242, 54, 37 }), -3980.5557f);

        testInterp = new Section(false, true, false, knownPoints);

        float testResult;
        boolean inSection;
        int[] testPoint = new int[] { 243, 51, 39 };

         inSection = testInterp.inSection(testPoint);
         assertTrue("In Section call returned false", inSection);

        testResult = testInterp.process(testPoint, true);
        assertEquals("Float isnt right :(", -4065.8601f, testResult, 0);
    }

}
