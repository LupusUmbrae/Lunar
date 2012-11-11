package org.moss.lunar.test.image.palette;

import junit.framework.TestCase;

import org.junit.Test;
import org.moss.lunar.types.RgbDto;

public class TestRGB extends TestCase
{

    @Test
    public void testEqualsTrue()
    {
        boolean equals;
        RgbDto rgb = new RgbDto(10, 11, 12);
        RgbDto compareTo = new RgbDto(10, 11, 12);

        equals = rgb.equals(compareTo);

        assertTrue("RGB Equals returned false", equals);
    }

    public void testEqualsFalse()
    {
        boolean equals;
        RgbDto rgb = new RgbDto(10, 11, 12);
        RgbDto compareTo = new RgbDto(11, 10, 12);

        equals = rgb.equals(compareTo);

        assertFalse("RGB Equals returned true", equals);
    }

    public void testEqualsDifferentObject()
    {
        boolean equals;
        RgbDto rgb = new RgbDto(10, 11, 12);
        Object compareTo = new Object();

        equals = rgb.equals(compareTo);

        assertFalse("RGB Equals returned true", equals);
    }

}
