package test.image.palette;

import static org.junit.Assert.fail;
import junit.framework.TestCase;
import image.RGB;

import org.junit.Test;

public class TestRGB extends TestCase
{

    @Test
    public void testEqualsTrue()
    {
        boolean equals;
        RGB rgb = new RGB(10, 11, 12);
        RGB compareTo = new RGB(10, 11, 12);

        equals = rgb.equals(compareTo);

        assertTrue("RGB Equals returned false", equals);
    }

    public void testEqualsFalse()
    {
        boolean equals;
        RGB rgb = new RGB(10, 11, 12);
        RGB compareTo = new RGB(11, 10, 12);

        equals = rgb.equals(compareTo);

        assertFalse("RGB Equals returned true", equals);
    }

    public void testEqualsDifferentObject()
    {
        boolean equals;
        RGB rgb = new RGB(10, 11, 12);
        Object compareTo = new Object();

        equals = rgb.equals(compareTo);

        assertFalse("RGB Equals returned true", equals);
    }

}
