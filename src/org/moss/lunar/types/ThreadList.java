package org.moss.lunar.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.moss.lunar.types.exceptions.ListFullException;

/**
 * Thread safe list
 * 
 * @author Robin
 * 
 * @param <T>
 */
public class ThreadList<T>
{

	private List<T> storageList = Collections.synchronizedList(new ArrayList<T>());

	private int pos;

	private Integer limit;

	public ThreadList(Integer limit)
	{
		this.limit = limit;
	}

	public ThreadList()
	{
		// TODO Auto-generated constructor stub
	}

	public void add(T item) throws ListFullException
	{
		if (isFull())
		{
			throw new ListFullException("List is full");
		}
		
		storageList.add(item);
	}

	public T get(int index)
	{
		return storageList.get(index);

	}

	/**
	 * Are there any more items to get from the list?
	 * 
	 * @return
	 */
	public boolean hasNext()
	{
		return (this.storageList.size() - 1) == pos;
	}

	public boolean isFull()
	{
		boolean full = false;
		int size =  this.storageList.size();
		if (limit != null)
		{
			full = limit <= this.storageList.size();
		}

		return full;
	}

	/**
	 * Gets the next item
	 * 
	 * @return
	 */
	public T getNext()
	{
		if (pos < storageList.size())
		{
			return this.storageList.get(pos++);
		}
		return null;
	}

	public int size()
	{
		return this.storageList.size();
	}

	/**
	 * Removes the item at the given index, and adjusts the current position to
	 * compensate (if required)
	 * 
	 * @param index
	 * @return
	 */
	public T remove(int index)
	{
		if (index <= pos && pos != 0)
		{
			pos--;
		}
		return this.storageList.remove(index);
	}

}
