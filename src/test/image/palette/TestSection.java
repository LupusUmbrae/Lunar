package test.image.palette;

import image.palette.InterpException;
import image.palette.Section;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

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
		SortedMap<Integer, Float> knownPoints = new TreeMap<Integer, Float>();
		Section testInterp;

		knownPoints.put(215, -6744.417f);
		knownPoints.put(210, -6693.2344f);
		knownPoints.put(206, -6642.052f);
		knownPoints.put(201, -6590.869f);

		testInterp = new Section(new int[] { 237, 41, 40 }, new int[] { 243,
				234, 234 }, -6949.1475f, -4236.4688f, false, true, true,
				knownPoints, knownPoints, knownPoints);

		float testResult;
		boolean inSection;
		int[] testPoint = new int[] { 238, 208, 208 };

		inSection = testInterp.inSection(testPoint);
		assertTrue("In Section call returned false", inSection);

		testResult = testInterp.process(testPoint);
		assertEquals("Float isnt right :(", -6667.643f, testResult, 0);
	}

	public void testUpSlope() throws InterpException
	{
		SortedMap<Integer, Float> knownPoints = new TreeMap<Integer, Float>();
		Section testInterp;

		knownPoints.put(44,-4185.286f);
		knownPoints.put(47,-4134.1035f);
		knownPoints.put(49,-4082.921f);
		knownPoints.put(52,-4031.7383f);
		knownPoints.put(54,-3980.5557f);
		knownPoints.put(56,-3929.373f);
		knownPoints.put(59,-3878.1904f);
		knownPoints.put(62,-3827.0078f);
		knownPoints.put(63,-3775.8252f);
		knownPoints.put(66,-3724.6426f);
		


		testInterp = new Section(new int[] { 237, 41, 40 }, new int[] { 243,
				234, 234 }, -6949.1475f, -4236.4688f, false, true, false,
				knownPoints, knownPoints, knownPoints);

		float testResult;
		boolean inSection;
		int[] testPoint = new int[] { 243, 49, 39 };

//		inSection = testInterp.inSection(testPoint);
//		assertTrue("In Section call returned false", inSection);

		testResult = testInterp.process(testPoint);
		assertEquals("Float isnt right :(", -4065.8601f, testResult, 0);
	}

}
