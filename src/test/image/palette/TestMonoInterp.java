package test.image.palette;

import image.palette.InterpException;
import image.palette.MonoInterp;
import image.palette.Section;

import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class TestMonoInterp extends TestCase
{
	private SortedMap<Integer, Float> knownPoints = new TreeMap<Integer, Float>();
	private Section testInterp;

	@Before
	public void setUp() throws InterpException
	{
		knownPoints.put(234, -6949.1475f);
		knownPoints.put(229, -6897.965f);
		knownPoints.put(224, -6846.782f);
		knownPoints.put(220, -6795.5996f);
		knownPoints.put(215, -6744.417f);
		knownPoints.put(210, -6693.2344f);
		knownPoints.put(206, -6642.052f);
		knownPoints.put(201, -6590.869f);
		knownPoints.put(196, -6539.6865f);
		knownPoints.put(192, -6488.504f);
		knownPoints.put(187, -6437.3213f);
		knownPoints.put(182, -6386.1387f);
		knownPoints.put(178, -6334.956f);
		knownPoints.put(174, -6283.7734f);
		knownPoints.put(169, -6232.591f);
		knownPoints.put(164, -6181.408f);
		knownPoints.put(160, -6130.2256f);
		knownPoints.put(155, -6079.043f);
		knownPoints.put(150, -6027.8604f);
		knownPoints.put(146, -5976.6777f);
		knownPoints.put(141, -5925.495f);
		knownPoints.put(136, -5874.3125f);
		knownPoints.put(132, -5823.13f);
		knownPoints.put(127, -5771.9473f);
		knownPoints.put(123, -5720.7646f);
		knownPoints.put(118, -5669.582f);
		knownPoints.put(114, -5618.3994f);
		knownPoints.put(109, -5567.217f);
		knownPoints.put(104, -5516.034f);
		knownPoints.put(101, -5464.8516f);
		knownPoints.put(98, -5413.669f);
		knownPoints.put(95, -5362.4863f);
		knownPoints.put(93, -5311.3037f);
		knownPoints.put(90, -5260.121f);
		knownPoints.put(89, -5208.9385f);
		knownPoints.put(86, -5157.756f);
		knownPoints.put(83, -5106.573f);
		knownPoints.put(81, -5055.3906f);
		knownPoints.put(78, -5004.208f);
		knownPoints.put(75, -4953.0254f);
		knownPoints.put(73, -4901.843f);
		knownPoints.put(70, -4850.66f);
		knownPoints.put(67, -4799.4775f);
		knownPoints.put(65, -4748.295f);
		knownPoints.put(63, -4697.1123f);
		knownPoints.put(60, -4645.9297f);
		knownPoints.put(58, -4594.747f);
		knownPoints.put(55, -4543.5645f);
		knownPoints.put(52, -4492.382f);
		knownPoints.put(50, -4441.199f);
		knownPoints.put(47, -4390.0166f);
		knownPoints.put(44, -4338.834f);
		knownPoints.put(42, -4287.6514f);
		knownPoints.put(40, -4236.4688f);

		// Min/Max needs some improvements in the section class currently just
		// pander to it :P
		testInterp = new MonoInterp(new int[] { 237, 41, 40 }, new int[] { 243,
				234, 234 }, -6949.1475f, -4236.4688f, false, false, true, 234,
				40, knownPoints);
	}

	/**
	 * Expected results = -6667.643
	 * 
	 * @throws InterpException
	 */
	@Test
	public void testFindMatch() throws InterpException
	{
		float testResult;
		boolean inSection;
		int[] testPoint = new int[] { 238, 208, 208 };

		inSection = testInterp.inSection(testPoint);
		assertTrue("In Section call returned false", inSection);

		testResult = testInterp.process(testPoint);
		assertEquals("Float isnt right :(", -6667.643f, testResult, 0);
	}

}
