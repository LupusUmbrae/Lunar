package org.moss.lunar.image.threads;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.moss.lunar.types.HeightDto;
import org.moss.lunar.types.PixelDto;
import org.moss.lunar.types.RowDto;
import org.moss.lunar.types.ThreadList;
import org.moss.lunar.types.exceptions.ListFullException;

/**
 * Class to store the data transmission between threads
 * 
 * @author Robin
 * 
 */
public class ImageStorage
{

	private static ThreadList<File> pixelFiles = new ThreadList<File>();

	private static ThreadList<RowDto<PixelDto>> pixelRows = new ThreadList<RowDto<PixelDto>>(
																								4);

	private static ThreadList<RowDto<HeightDto>> heightRows = new ThreadList<RowDto<HeightDto>>(
																								4);

	private static ThreadList<RowDto<HeightDto>> heightRowsProcessed = new ThreadList<RowDto<HeightDto>>(
																											4);

	private static BlockingQueue<String> statements = new LinkedBlockingQueue<String>();
	private static BlockingQueue<File> rowFiles = new LinkedBlockingQueue<File>();

	private static boolean endStatement = false;
	private static Object endStatementLock = new Object();

	private static boolean endReadFiles = false;
	private static Object endReadFilesLock = new Object();

	// Pixel Rows

	public static RowDto<PixelDto> getPixelRow()
	{
		return pixelRows.getNext();
	}

	public static void addPixelRow(RowDto<PixelDto> rowDto)
															throws ListFullException
	{
		pixelRows.add(rowDto);
	}

	public static boolean pixelRowHasNext()
	{
		return pixelRows.hasNext();
	}

	public static boolean pixelRowIsFull()
	{
		return pixelRows.isFull();
	}

	public static void pixelRowRemove(int index)
	{
		pixelRows.remove(index);
	}

	// Height Rows

	public static RowDto<HeightDto> getheightRow()
	{
		return heightRows.getNext();
	}

	public static void addheightRow(RowDto<HeightDto> rowDto)
																throws ListFullException
	{
		heightRows.add(rowDto);
	}

	public static boolean heightRowHasNext()
	{
		return heightRows.hasNext();
	}

	public static boolean heightRowIsFull()
	{
		return heightRows.isFull();
	}

	public static void heightRowRemove(int index)
	{
		heightRows.remove(index);
	}

	public static RowDto<HeightDto> getheightRowsProcessed()
	{
		return heightRowsProcessed.getNext();
	}

	public static void addheightRowsProcessed(RowDto<HeightDto> rowDto)
																		throws ListFullException
	{
		heightRowsProcessed.add(rowDto);
	}

	public static boolean heightRowsProcessedHasNext()
	{
		return heightRowsProcessed.hasNext();
	}

	public static boolean heightRowsProcessedIsFull()
	{
		return heightRowsProcessed.isFull();
	}

	public static void pixelFilesAdd(File file) throws ListFullException
	{
		synchronized (pixelFiles)
		{
			pixelFiles.add(file);
		}
	}

	public static File pixelFilesGet(int index)
	{
		File file;
		synchronized (pixelFiles)
		{
			file = pixelFiles.get(index);
		}
		return file;
	}

	public static int pixelFilesSize()
	{
		int size;
		synchronized (pixelFiles)
		{
			size = pixelFiles.size();
		}
		return size;
	}

	public static void rowFilesAdd(File file)
	{
		rowFiles.add(file);

	}

	public static File rowFilesPoll()
	{
		File file;
		file = rowFiles.poll();
		return file;
	}

	public static int rowFilesSize()
	{
		int size;
		size = rowFiles.size();
		return size;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static String statementsTake() throws InterruptedException
	{
		String statement;
		statement = statements.take();
		return statement;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static String statementsPoll() throws InterruptedException
	{
		String statement;
		statement = statements.poll();
		return statement;
	}

	/**
	 * 
	 * @param person
	 * @throws InterruptedException
	 */
	public static void statementsPut(String statement)
														throws InterruptedException
	{
		statements.put(statement);
	}

	public static boolean isEndStatement()
	{
		synchronized (endStatementLock)
		{
			return endStatement;
		}
	}

	public static void setEndStatement(boolean endStatement)
	{
		synchronized (endStatementLock)
		{
			ImageStorage.endStatement = endStatement;
		}
	}

	public static boolean isEndReadFiles()
	{
		synchronized (endReadFilesLock)
		{
			return endReadFiles;
		}
	}

	public static void setEndReadFiles(boolean endReadFiles)
	{
		synchronized (endReadFilesLock)
		{
			ImageStorage.endReadFiles = endReadFiles;
		}
	}

}
