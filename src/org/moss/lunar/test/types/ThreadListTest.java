package org.moss.lunar.test.types;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.moss.lunar.types.ThreadList;
import org.moss.lunar.types.exceptions.ListFullException;

public class ThreadListTest
{

	private ThreadList<String> testList = new ThreadList<String>();
	private ThreadList<String> testListLimited = new ThreadList<String>(2);

	@Before
	public void setUp() throws ListFullException
	{
		testList.add("Entry1");
		testList.add("Entry2");
		testList.add("Entry3");
		testList.add("Entry4");
		testList.add("Entry5");

		testListLimited.add("Entry1");
		testListLimited.add("Entry2");
	}

	@Test
	public void testRemove()
	{
		String entry;

		entry = testList.remove(0);

		Assert.assertEquals("Entry1", entry);

		entry = testList.remove(0);

		Assert.assertEquals("Entry2", entry);
	}

	@Test
	public void testGetNext()
	{
		String entry;
		
		entry = testList.getNext();
		Assert.assertEquals("Entry1", entry);
		entry = testList.getNext();
		Assert.assertEquals("Entry2", entry);
		entry = testList.getNext();
		Assert.assertEquals("Entry3", entry);
		entry = testList.getNext();
		Assert.assertEquals("Entry4", entry);
		entry = testList.getNext();
		Assert.assertEquals("Entry5", entry);
		entry = testList.getNext();
		Assert.assertEquals(null, entry);
	}
	
	@Test
	public void testIsFull(){
		
		Assert.assertEquals(true, testListLimited.isFull());
	}
}
