package database.storage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import main.DataTile;

/**
 * 
 * Note: Not that keen on this class but it saves loads of code being all over
 * the place
 * 
 * @author Robin
 * 
 */
public class ThreadStorage {
	/**
	 * 
	 */
	private static BlockingQueue<DataQuery> sqlQueries = new LinkedBlockingQueue<DataQuery>();

	/**
	 * 
	 */
	private static BlockingQueue<RawTile> dataTileResults = new LinkedBlockingQueue<RawTile>();

	/**
	 * 
	 */
	private static BlockingQueue<DataTile> processedTiles = new LinkedBlockingQueue<DataTile>();

	/**
	 * 
	 */
	private static boolean endQueries = false;
	private static Object endQueriesLock = new Object();

	private static boolean endProcess = false;
	private static Object endProcessLock = new Object();

	private static boolean endStatement = false;
	private static Object endStatementLock = new Object();

	// *********************//
	// Thread safe methods //
	// *********************//

	public static DataQuery pollSqlQueries() {
		synchronized (sqlQueries) {
			return sqlQueries.poll();
		}
	}

	public static void putSqlQueries(DataQuery sqlQuery)
			throws InterruptedException {
		synchronized (sqlQueries) {
			ThreadStorage.sqlQueries.put(sqlQuery);
		}
	}

	public static RawTile pollDataTileResults() {
		synchronized (dataTileResults) {
			return dataTileResults.poll();
		}
	}

	public static void putDataTileResults(RawTile dataTileResult)
			throws InterruptedException {
		synchronized (dataTileResults) {
			dataTileResults.put(dataTileResult);
			System.out.println("Data Tile Results Size: "
					+ dataTileResults.size());
		}

	}

	public static boolean isEndQueries() {
		synchronized (endQueriesLock) {
			return endQueries;
		}

	}

	public static void setEndQueries(boolean end) {
		synchronized (endQueriesLock) {
			ThreadStorage.endQueries = end;
		}

	}

	public static boolean isEndProcess() {
		synchronized (endProcessLock) {
			return endProcess;
		}
	}

	public static void setEndProcess(boolean endStatement) {
		synchronized (endProcessLock) {
			ThreadStorage.endProcess = endStatement;
		}
	}

	public static void putProcessedTile(DataTile tile)
			throws InterruptedException {
		synchronized (processedTiles) {
			processedTiles.put(tile);
			System.out
					.println("Processed Tiles Size: " + processedTiles.size());
		}
	}

	public static DataTile pollProcessedTile() {
		synchronized (processedTiles) {
			return processedTiles.poll();
		}
	}

	public static boolean isEndStatement() {
		synchronized (endStatementLock) {
			return endStatement;
		}
	}

	public static void setEndStatement(boolean endStatement) {
		synchronized (endStatementLock) {
			ThreadStorage.endStatement = endStatement;
		}
	}
}
