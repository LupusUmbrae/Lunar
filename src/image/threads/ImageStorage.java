package image.threads;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ImageStorage {

	private static ArrayList<File> pixelFiles = new ArrayList<File>();

	private static BlockingQueue<String> statements = new LinkedBlockingQueue<String>();
	private static BlockingQueue<File> rowFiles = new LinkedBlockingQueue<File>();

	private static boolean endStatement = false;
	private static Object endStatementLock = new Object();

	private static boolean endReadFiles = false;
	private static Object endReadFilesLock = new Object();

	public static void pixelFilesAdd(File file) {
		synchronized (pixelFiles) {
			pixelFiles.add(file);
		}
	}

	public static void rowFilesAdd(File file) {
		rowFiles.add(file);

	}

	public static File pixelFilesGet(int index) {
		File file;
		synchronized (pixelFiles) {
			file = pixelFiles.get(index);
		}
		return file;
	}

	public static File rowFilesPoll() {
		File file;
		file = rowFiles.poll();
		return file;
	}

	public static int pixelFilesSize() {
		int size;
		synchronized (pixelFiles) {
			size = pixelFiles.size();
		}
		return size;
	}

	public static int rowFilesSize() {
		int size;
		size = rowFiles.size();
		return size;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static String statementsTake() throws InterruptedException {
		String statement;
		statement = statements.take();
		return statement;
	}

	/**
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public static String statementsPoll() throws InterruptedException {
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
			throws InterruptedException {
		statements.put(statement);
	}

	public static boolean isEndStatement() {
		synchronized (endStatementLock) {
			return endStatement;
		}
	}

	public static void setEndStatement(boolean endStatement) {
		synchronized (endStatementLock) {
			ImageStorage.endStatement = endStatement;
		}
	}

	public static boolean isEndReadFiles() {
		synchronized (endReadFilesLock) {
			return endReadFiles;
		}
	}

	public static void setEndReadFiles(boolean endReadFiles) {
		synchronized (endReadFilesLock) {
			ImageStorage.endReadFiles = endReadFiles;
		}
	}
}
