package com.slemma.jdbc;

import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author igorshestakov.
 */
public abstract class AbstractMongoStatement
{
	final static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoDriver.class.getName());	

	/** Reference to store the ran Query run by Executequery or Execute */
	ResultSet resset = null;

	/** Variable that stores the closed state of the statement */
	boolean closed = false;

	/** Reference for the Connection that created this Statement object */
	MongoConnection connection;

	/** Variable that stores the set query timeout */
	int querytimeout = Integer.MAX_VALUE;
	/** Instance of log4j.Logger */
	/**
	 * Variable stores the time an execute is made
	 */
	long starttime = 0;
	/**
	 * Variable that stores the max row number which can be stored in the
	 * resultset
	 */
	int resultMaxRowCount = Integer.MAX_VALUE - 1;

	/** Variable to Store EscapeProc state */
	boolean EscapeProc = false;

	/**
	 * These Variables contain information about the type of resultset this
	 * statement creates
	 */
	int resultSetType;
	int resultSetConcurrency;

	/**
	 * to be used with setMaxFieldSize
	 */
	private int maxFieldSize = 0;

	private int fetchSize = 0;

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public void addBatch(String arg0) throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "addBatch(string)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public void cancel() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public void clearBatch() throws SQLException {
		throw new MongoSQLException("Not implemented." + "clearBatch()");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public void clearWarnings() throws SQLException {
		throw new MongoSQLException("Not implemented." + "clearWarnings()");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Only sets closed boolean to true
	 * </p>
	 */
	public void close() throws SQLException {
		this.closed = true;
		if (this.resset != null) {
			this.resset.close();
		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Executes the given SQL statement on MongoDb (note: it returns only 1
	 * resultset). This function directly uses executeQuery function
	 * </p>
	 */

	public boolean execute(String arg0) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		this.resset = this.executeQuery(arg0);
		this.logger.info("Executing Query: " + arg0);
		if (this.resset != null) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public boolean execute(String arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException("execute(String, int)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public boolean execute(String arg0, int[] arg1) throws SQLException {
		throw new UnsupportedOperationException("execute(string,int[])");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public boolean execute(String arg0, String[] arg1) throws SQLException {
		throw new UnsupportedOperationException("execute(string,string[])");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public int[] executeBatch() throws SQLException {
		throw new MongoSQLException("Not implemented." + "executeBatch()");
	}

	/** {@inheritDoc} */

	public ResultSet executeQuery(String querySql) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		this.starttime = System.currentTimeMillis();

		throw new UnsupportedOperationException();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public int executeUpdate(String arg0) throws SQLException {
		throw new UnsupportedOperationException("executeUpdate(string)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public int executeUpdate(String arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException("executeUpdate(String,int)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		throw new UnsupportedOperationException(
				  "executeUpdate(string,int[])");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		throw new UnsupportedOperationException(
				  "execute(update(string,string[])");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns the connection that made the object.
	 * </p>
	 *
	 * @returns connection
	 */
	public Connection getConnection() throws SQLException {
		return this.connection;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Fetch direction is unknown.
	 * </p>
	 *
	 * @return FETCH_UNKNOWN
	 */

	public int getFetchDirection() throws SQLException {
		return ResultSet.FETCH_UNKNOWN;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public int getFetchSize() throws SQLException {
		return this.fetchSize;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public ResultSet getGeneratedKeys() throws SQLException {
		throw new UnsupportedOperationException("getGeneratedKeys()");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public int getMaxFieldSize() throws SQLException {
		return maxFieldSize;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * We store it in an array which indexed through, int
	 * </p>
	 * We could return Integer.MAX_VALUE too, but i don't think we could get
	 * that much row.
	 *
	 * @return 0 -
	 */

	public int getMaxRows() throws SQLException {
		return this.resultMaxRowCount;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	public ResultSetMetaData getMetaData() throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException(
				  "getMetaData()"));
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Multiple result sets are not supported currently.
	 * </p>
	 *
	 * @return false;
	 */

	public boolean getMoreResults() throws SQLException {
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Multiple result sets are not supported currently. we check that the
	 * result set is open, the parameter is acceptable, and close our current
	 * resultset or throw a FeatureNotSupportedException
	 * </p>
	 *
	 * @param current
	 *            - one of the following Statement constants indicating what
	 *            should happen to current ResultSet objects obtained using the
	 *            method getResultSet: Statement.CLOSE_CURRENT_RESULT,
	 *            Statement.KEEP_CURRENT_RESULT, or Statement.CLOSE_ALL_RESULTS
	 * @throws UnsupportedOperationException
	 */

	public boolean getMoreResults(int current) throws SQLException {
		return false;
	}

	/** {@inheritDoc} */

	public int getQueryTimeout() throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		if (this.starttime == 0) {
			return 0;
		}
		if (this.querytimeout == Integer.MAX_VALUE) {
			return 0;
		}
		else {
			if (System.currentTimeMillis() - this.starttime > this.querytimeout) {
				throw new MongoSQLException("Time is over");
			}
			return this.querytimeout;
		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Gives back reultset stored in resset
	 * </p>
	 */

	public ResultSet getResultSet() throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		if (this.resset != null) {
			return this.resset;
		}
		else {
			return null;
		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * The driver is read only currently.
	 * </p>
	 *
	 * @return CONCUR_READ_ONLY
	 */

	public int getResultSetConcurrency() throws SQLException {
		return ResultSet.CONCUR_READ_ONLY;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Read only mode, no commit.
	 * </p>
	 *
	 * @return CLOSE_CURSORS_AT_COMMIT
	 */

	public int getResultSetHoldability() throws SQLException {
		return ResultSet.CLOSE_CURSORS_AT_COMMIT;
		// TODO
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Updates and deletes not supported.
	 * </p>
	 *
	 * @return TYPE_SCROLL_INSENSITIVE
	 */

	public int getResultSetType() throws SQLException {
		return this.resultSetType;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Result will be a ResultSet object.
	 * </p>
	 *
	 * @return -1
	 */

	public int getUpdateCount() throws SQLException {
		return -1;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * returns null
	 * </p>
	 *
	 * @return null
	 */

	public SQLWarning getWarnings() throws SQLException {
		return null;
		// TODO Implement Warning Handling
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns the value of boolean closed
	 * </p>
	 */
	public boolean isClosed() throws SQLException {
		return this.closed;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public boolean isPoolable() throws SQLException {
		throw new MongoSQLException("Not implemented." + "isPoolable()");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @return false
	 */
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * FeatureNotSupportedExceptionjpg
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */

	public void setCursorName(String arg0) throws SQLException {
		throw new UnsupportedOperationException("setCursorName(string)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Now Only setf this.EscapeProc to arg0
	 * </p>
	 */

	public void setEscapeProcessing(boolean arg0) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		this.EscapeProc = arg0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */

	public void setFetchDirection(int arg0) throws SQLException {
		throw new MongoSQLException("Not implemented." + "setFetchDirection(int)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Does nothing
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	public void setFetchSize(int arg0) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("Statement closed");
		}
		this.fetchSize = arg0;
	}

	/**
	 * <p>
	 * Sets the limit for the maximum number of bytes in a ResultSet column storing 
	 * character or binary values to the given number of bytes. This limit applies only 
	 * to BINARY, VARBINARY, LONGVARBINARY, CHAR, VARCHAR, and LONGVARCHAR fields. 
	 * If the limit is exceeded, the excess data is silently discarded. For maximum 
	 * portability, use values greater than 256.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	public void setMaxFieldSize(int arg0) throws SQLException {
		this.maxFieldSize = arg0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * arg0 == 0 ? arg0 : Integer.MAX_VALUE - 1
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	public void setMaxRows(int arg0) throws SQLException {
		this.resultMaxRowCount = arg0 != 0 ? arg0 : Integer.MAX_VALUE - 1;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	public void setPoolable(boolean arg0) throws SQLException {
		throw new MongoSQLException("Not implemented." + "setPoolable(bool)");
	}

	/** {@inheritDoc} */
	public void setQueryTimeout(int arg0) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		if (arg0 == 0) {
			this.querytimeout = Integer.MAX_VALUE;
		}
		else {
			this.querytimeout = arg0;
		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Always throws SQLException
	 * </p>
	 *
	 * @throws SQLException
	 */
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new MongoSQLException("not found");
	}
	
}
