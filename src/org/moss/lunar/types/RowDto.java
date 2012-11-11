package org.moss.lunar.types;

import java.util.ArrayList;
import java.util.List;

public class RowDto<T>
{

	private List<T> row = new ArrayList<T>();

	private int pos = 0;

	public RowDto(List<T> row)
	{
		this.row = row;
	}

	public RowDto()
	{

	}

	public void addItem(T item)
	{
		this.row.add(item);
	}

	public void addRow(List<T> row)
	{
		this.row = row;
	}

	public T getNextItem()
	{
		return this.row.get(pos++);
	}

	public boolean hasMore()
	{
		return this.row.size() > pos;
	}
}
