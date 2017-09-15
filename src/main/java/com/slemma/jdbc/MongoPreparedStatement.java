package com.slemma.jdbc;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.query.MongoExecutionOptions;
import com.slemma.jdbc.query.MongoQuery;
import com.slemma.jdbc.query.MongoQueryExecutor;
import com.slemma.jdbc.query.MongoQueryParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;

/**
 * @author Igor Shestakov.
 */
public class MongoPreparedStatement extends AbstractMongoStatement implements PreparedStatement
{

	/** Storage for implementing PreparedStatement */
	String PrecompiledSQL = null;
	String RunnableStatement = null;

	/**
	 * Reference for the Container that contains parameters for
	 * preparedStatement
	 */
	String[] Parameters = null;

	/**
	 * Constructor for BQStatement object just initializes local variables as
	 * preparedStatement
	 *
	 * @param query
	 * @param mongoConnection
	 */
	public MongoPreparedStatement(String query, MongoConnection mongoConnection) {
		this.connection = mongoConnection;
		//this.resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;  //-scrollable
		this.resultSetType = ResultSet.TYPE_FORWARD_ONLY;
		this.resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
		this.PrecompiledSQL = query;
		if (!this.PrecompiledSQL.contains("?")) {
			this.RunnableStatement = this.PrecompiledSQL;
		}

		int count = 0;
		for (int i = 0; i < query.length(); i++) {
			if (query.charAt(i) == '?') {
				count++;
			}
		}

		if (count != 0) {
			this.Parameters = new String[count];
		}
	}

	/**
	 * Constructor for BQStatement object just initializes local variables as a
	 * PreparedStatement
	 *
	 * @param query
	 * @param mongoConnection
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @throws MongoSQLException
	 */
	public MongoPreparedStatement(String query, MongoConnection mongoConnection, int resultSetType,
										int resultSetConcurrency) throws MongoSQLException {
		if (resultSetConcurrency == ResultSet.CONCUR_UPDATABLE) {
			throw new MongoSQLException(
					  "The Resultset Concurrency can't be ResultSet.CONCUR_UPDATABLE");
		}

		this.connection = mongoConnection;
		this.resultSetType = resultSetType;
		this.resultSetConcurrency = resultSetConcurrency;
		this.PrecompiledSQL = query;

		int count = 0;
		for (int i = 0; i < query.length(); i++) {
			if (query.charAt(i) == '?') {
				count++;
			}
		}
		if (count != 0) {
			this.Parameters = new String[count];
		}

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	@Override
	public void addBatch() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "addBatch()");
	}

	@Override
	public void clearParameters() throws SQLException {

		int count = 0;
		for (int i = 0; i < this.PrecompiledSQL.length(); i++) {
			if (this.PrecompiledSQL.charAt(i) == '?') {
				count++;
			}
		}

		if (count != 0) {
			this.Parameters = new String[count];
		}
		else {
			this.Parameters = null;
		}

		this.RunnableStatement = this.PrecompiledSQL;
	}

	/**
	 * Reconstructs the sql string which gets queried when for example
	 * executequery is called this function is called every time a paramater is
	 * changed
	 */
	private void constructsql() {
		String[] data = this.PrecompiledSQL.split("\\?");
		String newsql = data[0];

		int countofsema = 0;
		for (int i = 0; i < this.Parameters.length; i++) {
			// if parameter is not yet set
			if (this.Parameters[i] == null) {
				// if there's data after the parameter
				if (++countofsema < data.length) {
					newsql += "?" + data[countofsema];
					// parameter is at the end
				}
				else {
					newsql += "?";
				}
			}
			else
				// there's data after the parameter
				if (++countofsema < data.length) {
					newsql += this.Parameters[i] + data[countofsema];
					// parameter is at the end
				}
				else {
					newsql += this.Parameters[i];
				}
		}
		this.RunnableStatement = newsql;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * </p>
	 */
	@Override
	public boolean execute() throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		this.resset = this.executeQuery();
		this.logger.info("Executing Query: " + this.RunnableStatement);
		if (this.resset != null) {
			return true;
		}
		else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public ResultSet executeQuery() throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}
		if (this.RunnableStatement == null) {
			throw new MongoSQLException("Parameters are not set");
		}
		if (this.RunnableStatement.contains("?")) {
			throw new MongoSQLException("Not all parameters set");
		}

		this.starttime = System.currentTimeMillis();
//		MongoClient mongoClient = this.connection.getMongoClient();
//		MongoDatabase database = mongoClient.getDatabase(this.connection.getDatabase());
		MongoQuery mongoQuery = MongoQueryParser.parse(this.RunnableStatement);
		MongoQueryExecutor executor = new MongoQueryExecutor(this.connection.getMongoClient());

		Properties connectionProperties = this.connection.getProperties();
		MongoExecutionOptions eOptions = new MongoExecutionOptions.MongoExecutionOptionsBuilder()
				  .batchSize(MongoDriverProperty.BATCH_SIZE.getInt(connectionProperties))
				  .samplingBatchSize(this.getFetchSize() != 0 ? this.getFetchSize() : MongoDriverProperty.BATCH_SIZE.getInt(connectionProperties))
				  .maxRows(this.getMaxRows())
				  .createOptions();

		return executor.run(this.connection.getDatabase(), mongoQuery, this, eOptions);

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */
	@Override
	public int executeUpdate() throws SQLException {
		throw new UnsupportedOperationException("executeUpdate()");
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		logger.debug("function call: getParameterMetaData()");
		// TODO IMPLEMENT
		return null;
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			StringWriter writer = new StringWriter();

			try {
				org.apache.commons.io.IOUtils.copy(x, writer, "UTF-8");
			}
			catch (IOException e) {
				throw new MongoSQLException(e);
			}
			String theString = writer.toString();
			this.SetParameter(parameterIndex, "\"" + theString + "\"");
		}

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			StringWriter writer = new StringWriter();

			try {
				org.apache.commons.io.IOUtils.copy(x, writer, "UTF-8");
			}
			catch (IOException e) {
				throw new MongoSQLException(e);
			}
			String theString = writer.toString();
			this.SetParameter(parameterIndex, "\"" + theString + "\"");
		}

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			StringWriter writer = new StringWriter();

			try {
				org.apache.commons.io.IOUtils.copy(x, writer, "UTF-8");
			}
			catch (IOException e) {
				throw new MongoSQLException(e);
			}
			String theString = writer.toString();
			this.SetParameter(parameterIndex, "\"" + theString + "\"");
		}

	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, x.toString());
		}

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			  throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			  throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			  throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			  throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			  throws SQLException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, Boolean.toString(x));
		}

	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, Integer.toString(x & 0xff));
		}
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			char[] arr = new char[8 * 1024]; // 8K at a time
			StringBuffer buf = new StringBuffer();
			int numChars;

			try {
				while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
					buf.append(arr, 0, numChars);
				}
			}
			catch (IOException e) {
				throw new MongoSQLException(e);
			}
			this.SetParameter(parameterIndex, "\"" + buf.toString() + "\"");
		}
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			  throws SQLException {
		this.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
											 long length) throws SQLException {
		this.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			  throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			Date s = new Date(x.getTime()
					  + Calendar.getInstance().getTimeZone().getRawOffset());
			this.SetParameter(parameterIndex, "\"" + s.toString() + "\"");
		}

	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			Date s = new Date(x.getTime()
					  + ((cal == null) ? Calendar.getInstance().getTimeZone()
					  .getRawOffset() : cal.getTimeZone().getRawOffset()));
			this.SetParameter(parameterIndex, "\"" + s.toString() + "\"");
		}

	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, Double.toString(x));
		}
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, Float.toString(x));
		}

	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, Integer.toString(x));
		}

	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, Long.toString(x));
		}

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader)
			  throws SQLException {
		this.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader,
											  long length) throws SQLException {
		this.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			  throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setNString(int parameterIndex, String value)
			  throws SQLException {
		this.setString(parameterIndex, value);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, "NULL");
		}

	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, "NULL");
		}

	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else
			// TODO Test and implicitly expand
			if (x.getClass().equals(Integer.class)) {
				this.setInt(parameterIndex, Integer.class.cast(x));
			}
			else
			if (x.getClass().equals(String.class)) {
				this.setString(parameterIndex, String.class.cast(x));
			}
			else
			if (x.getClass().equals(Reader.class)) {
				this.setCharacterStream(parameterIndex,
						  Reader.class.cast(x));
			}
			else
			if (x.getClass().equals(Long.class)) {
				this.setLong(parameterIndex, Long.class.cast(x));
			}
			else
			if (x.getClass().equals(Float.class)) {
				this.setFloat(parameterIndex,
						  Float.class.cast(x));
			}
			else
			if (x.getClass().equals(Double.class)) {
				this.setDouble(parameterIndex,
						  Double.class.cast(x));
			}
			else
			if (x.getClass().equals(Date.class)) {
				this.setDate(parameterIndex,
						  Date.class.cast(x));
			}
			else
			if (x.getClass().equals(Time.class)) {
				this.setTime(parameterIndex,
						  Time.class.cast(x));
			}
			else
			if (x.getClass().equals(
					  Timestamp.class)) {
				this.setTimestamp(
						  parameterIndex,
						  Timestamp.class.cast(x));
			}
			else
			if (x.getClass().equals(
					  Byte.class)) {
				this.setByte(
						  parameterIndex,
						  Byte.class.cast(x));
			}
			else
			if (x.getClass().equals(
					  BigDecimal.class)) {
				this.setBigDecimal(
						  parameterIndex,
						  BigDecimal.class
									 .cast(x));
			}
			else
			if (x.getClass()
					  .equals(RowId.class)) {
				this.setRowId(
						  parameterIndex,
						  RowId.class
									 .cast(x));
			}
			else
			if (x.getClass()
					  .equals(Short.class)) {
				this.setShort(
						  parameterIndex,
						  Short.class
									 .cast(x));
			}
			else
			if (x.getClass()
					  .equals(SQLXML.class)) {
				this.setSQLXML(
						  parameterIndex,
						  SQLXML.class
									 .cast(x));
			}
			else
			if (x.getClass()
					  .equals(URL.class)) {
				this.setURL(
						  parameterIndex,
						  URL.class
									 .cast(x));
			}
			else
			if (x.getClass()
					  .equals(Boolean.class)) {
				this.setBoolean(
						  parameterIndex,
						  Boolean.class
									 .cast(x));
			}
			else
			if (x.getClass()
					  .equals(InputStream.class)) {
				this.setAsciiStream(
						  parameterIndex,
						  InputStream.class
									 .cast(x));
			}
			else {
				throw new MongoSQLException(
						  new SQLFeatureNotSupportedException());
			}

	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType)
			  throws SQLException {
		// setObject(parameterIndex, x);
		// TODO Implement
		throw new MongoSQLException(new SQLFeatureNotSupportedException());

	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType,
								 int scaleOrLength) throws SQLException {
		// setObject(parameterIndex, x);
		// TODO Implement
		throw new MongoSQLException(new SQLFeatureNotSupportedException());
	}

	/**
	 * Sets the given paramter for the prepared statement (its a string value to
	 * change the ? for)
	 */
	private void SetParameter(int paramterNumber, String value) {
		this.Parameters[paramterNumber - 1] = value;
		this.constructsql();
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		throw new MongoSQLException(new SQLFeatureNotSupportedException());
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, "\"" + x.toString() + "\"");
		}

	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		int myint = new Integer(x);
		this.setInt(parameterIndex, myint);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, "\"" + xmlObject.getString()
					  + "\"");
			// TODO Check conversion
		}

	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, "\"" + x + "\"");
		}
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			Time s = new Time(x.getTime()
					  + Calendar.getInstance().getTimeZone().getRawOffset());
			this.SetParameter(parameterIndex, "\"" + s.toString() + "\"");
		}

	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			Time s = new Time(x.getTime()
					  + ((cal == null) ? Calendar.getInstance().getTimeZone()
					  .getRawOffset() : cal.getTimeZone().getRawOffset()));
			this.SetParameter(parameterIndex, "\"" + s.toString() + "\"");
		}

	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			Timestamp s = new Timestamp(x.getTime()
					  + Calendar.getInstance().getTimeZone().getRawOffset());
			this.SetParameter(parameterIndex, "\"" + s.toString() + "\"");
		}

	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			Timestamp s = new Timestamp(x.getTime()
					  + ((cal == null) ? Calendar.getInstance().getTimeZone()
					  .getRawOffset() : cal.getTimeZone().getRawOffset()));
			this.SetParameter(parameterIndex, "\"" + s.toString() + "\"");
		}

	}

	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			  throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			StringWriter writer = new StringWriter();

			try {
				org.apache.commons.io.IOUtils.copy(x, writer, "UTF-8");
			}
			catch (IOException e) {
				throw new MongoSQLException(e);
			}
			String theString = writer.toString();
			this.SetParameter(parameterIndex, "\"" + theString + "\"");
		}

	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		if (this.isClosed()) {
			throw new MongoSQLException("The Statement is Closed");
		}
		if (this.Parameters == null) {
			throw new MongoSQLException("Index is not valid");
		}
		// The First Parameter is 1
		if (parameterIndex < 1 || parameterIndex > this.Parameters.length) {
			throw new MongoSQLException("Index is not valid");
		}
		else {
			this.SetParameter(parameterIndex, "\"" + x.toString() + "\"");
		}

	}

	@Override
	public String toString()
	{
		if (this.RunnableStatement != null)
			return this.RunnableStatement;

		if (this.PrecompiledSQL != null)
			return this.RunnableStatement;

		return super.toString();
	}

	//------------------------- for Jdk1.7 -----------------------------------

	/** {@inheritDoc} */
	@Override
	public void closeOnCompletion() throws SQLException {

	}

	/** {@inheritDoc} */
	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return false;
	}
	
}
