package com.slemma.jdbc;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * The connection class which builds the connection between MongoDb and the Driver
 *
 * @author Igor Shestakov
 */
public class MongoConnection implements Connection
{
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoConnection.class.getName());

	/**
	 * Variable to store auto commit mode
	 */
	private boolean autoCommitEnabled = false;

	/**
	 * The Mongo client.
	 */
	private MongoClient mongoClient = null;

	private String url = null;

	private final String database;
	private String timeZone = "UTC";

	private Properties properties;

	/**
	 * List to contain sql warnings in
	 */
	private List<SQLWarning> SQLWarningList = new ArrayList<SQLWarning>();

	/**
	 * Extracts the JDBC URL then makes a connection to the MongoDb.
	 *
	 * @param url  the JDBC connection URL
	 * @param info connection properties
	 * @throws SQLException
	 */
	public MongoConnection(String url, Properties info) throws SQLException
	{
		this.url = url;
		this.properties = info;

		MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
		Set<String> propNames = info.stringPropertyNames();
		String optionsString = "";
		for (String propName : propNames)
		{
			if (info.get(propName) != null && !info.get(propName).equals(""))
			{
				if (propName.equals("connectTimeout")
				// если явно опеределены serverSelectionTimeoutMS или connectTimeoutMS, то connectTimeout не используем
						  && !(info.contains("serverSelectionTimeoutMS") || info.contains("connectTimeoutMS"))) {
					int connectTimeout = (Integer.valueOf((String)info.get(propName))).intValue() * 1000;
					optionsBuilder = optionsBuilder.serverSelectionTimeout(connectTimeout);
					optionsBuilder = optionsBuilder.connectTimeout(connectTimeout);
				}
				else if (propName.equals("ssl"))
				{
					optionsBuilder.sslEnabled(Boolean.valueOf((String) info.get(propName)));
				}
				else if (propName.equals("sslInvalidHostNameAllowed"))
				{
					optionsBuilder.sslInvalidHostNameAllowed(Boolean.valueOf((String)info.get(propName)));
				}
//				else if (propName.equals("serverSelectionTimeoutMS"))
//				{
//					optionsBuilder.serverSelectionTimeout(Integer.valueOf((String) info.get(propName)));
//				}
				else if (propName.equals("heartbeatFrequencyMS"))
				{
					optionsBuilder.heartbeatFrequency(Integer.valueOf((String)info.get(propName)));
				}
				else if (propName.equals("localThresholdMS"))
				{
					optionsBuilder.localThreshold(Integer.valueOf((String) info.get(propName)));
				}
//				else if (propName.equals("connectTimeoutMS"))
//				{
//					optionsBuilder.connectTimeout(Integer.valueOf((String) info.get(propName)));
//				}
				else if (propName.equals("socketTimeoutMS"))
				{
					optionsBuilder.socketTimeout(Integer.valueOf((String)info.get(propName)));
				}
				else if (propName.equals("timeZone"))
				{
					this.timeZone = (String) info.get(propName);
				}
				//excluded properties
				else if (!(Arrays.asList("hosts", "user", "password", "database").contains(propName)))
				{
					optionsString += String.format("&%s=%s", propName, info.get(propName));
				}
			}
		}

		if (url.contains("?"))
			url += optionsString;
		else
			url += "?" + optionsString.substring(1, optionsString.length());
		MongoClientURI mongoURI = new MongoClientURI(url, optionsBuilder);
		this.mongoClient = new MongoClient(mongoURI);
		if (mongoURI.getDatabase() != null)
			this.database = mongoURI.getDatabase();
		else
			this.database = info.getProperty("database");

		//check connection
		Document pingCommand = new Document("ping", "1");
		mongoClient.getDatabase(this.database).runCommand(pingCommand);
	}

	public MongoClient getMongoClient()
	{
		return mongoClient;
	}

	public String getDatabase()
	{
		return database;
	}

	public MongoDatabase getNativeDatabase()
	{
		return mongoClient.getDatabase(this.database);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Uses SQLWarningList.clear() to clear all warnings
	 * </p>
	 */
	@Override
	public void clearWarnings() throws SQLException
	{
//		if (this.isclosed)
//		{
//			throw new MongoSQLException("Connection is closed.");
//		}
//		this.SQLWarningList.clear();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * </p>
	 */
	@Override
	public void close() throws SQLException
	{
		mongoClient.close();

//		if (!this.isclosed)
//		{
//			this.bigquery = null;
//			this.isclosed = true;
//		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Throws Exception
	 * </p>
	 *
	 * @throws SQLException <p>
	 *                      There is no Commit in MongoDb + Connection Status
	 *                      </p>
	 */
	@Override
	public void commit() throws SQLException
	{
//		if (this.isclosed)
//		{
//			throw new MongoSQLException(
//					  "There's no commit in MongoDb.\nConnection Status: Closed.");
//		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "createArrayOf(String typeName, Object[] elements)");
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
	public Blob createBlob() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "createBlob()");
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
	public Clob createClob() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "createClob()");
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
	public NClob createNClob() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "createNClob()");
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
	public SQLXML createSQLXML() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "createSQLXML()");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Creates a new BQStatement object with the projectid in this Connection
	 * </p>
	 *
	 * @return a new BQStatement object with the projectid in this Connection
	 * @throws SQLException if the Connection is closed
	 */
	@Override
	public Statement createStatement() throws SQLException
	{
		return new MongoStatement(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			  throws SQLException
	{
		if (this.isClosed())
		{
			throw new MongoSQLException("The Connection is Closed");
		}
		logger.debug("Creating statement with resultsettype: " + resultSetType
				  + " concurrency: " + resultSetConcurrency);
		return new MongoStatement(this, resultSetType, resultSetConcurrency);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Statement createStatement(int resultSetType,
												int resultSetConcurrency, int resultSetHoldability)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "createStaement(int,int,int)");
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
	public Struct createStruct(String typeName, Object[] attributes)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "createStruct(string,object[])");
	}

	public String getUrl()
	{
		return url;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Getter for autoCommitEnabled
	 * </p>
	 *
	 * @return auto commit state;
	 */
	@Override
	public boolean getAutoCommit() throws SQLException
	{
		return this.autoCommitEnabled;
	}
	
	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Return projectid
	 * </p>
	 *
	 * @return projectid Contained in this Connection instance
	 */
	@Override
	public String getCatalog() throws SQLException
	{
		return this.getDatabase();
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
	public Properties getClientInfo() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "getClientInfo()");
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
	public String getClientInfo(String name) throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * There's no commit.
	 * </p>
	 *
	 * @return CLOSE_CURSORS_AT_COMMIT
	 */
	@Override
	public int getHoldability() throws SQLException
	{
		return ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Return a new BQDatabaseMetadata object constructed from this Connection
	 * instance
	 * </p>
	 *
	 * @return a new BQDatabaseMetadata object constructed from this Connection
	 * instance
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException
	{
		return new MongoDatabaseMetadata(this);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Transactions are not supported.
	 * </p>
	 *
	 * @return TRANSACTION_NONE
	 */
	@Override
	public int getTransactionIsolation() throws SQLException
	{
		return java.sql.Connection.TRANSACTION_NONE;
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
	public Map<String, Class<?>> getTypeMap() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "getTypeMap()");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * If SQLWarningList is empty returns null else it returns the first item
	 * Contained inside <br>
	 * Subsequent warnings will be chained to this SQLWarning.
	 * </p>
	 *
	 * @return SQLWarning (The First item Contained in SQLWarningList) + all
	 * others chained to it
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException
	{
//		if (this.isclosed)
//		{
//			throw new MongoSQLException("Connection is closed.");
//		}
		if (this.SQLWarningList.isEmpty())
		{
			return null;
		}

		SQLWarning forreturn = this.SQLWarningList.get(0);
		this.SQLWarningList.remove(0);
		if (!this.SQLWarningList.isEmpty())
		{
			for (SQLWarning warning : this.SQLWarningList)
			{
				forreturn.setNextWarning(warning);
			}
		}
		return forreturn;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * returns the status of isclosed boolean
	 * </p>
	 */
	@Override
	public boolean isClosed() throws SQLException
	{
//		return this.isclosed;
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * The driver is read only at this stage.
	 * </p>
	 *
	 * @return true
	 */
	@Override
	public boolean isReadOnly() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * </p>
	 *
	 * @throws SQLException
	 */
	@Override
	public boolean isValid(int timeout) throws SQLException
	{
//		if (this.isclosed)
//		{
//			return false;
//		}
		if (timeout < 0)
		{
			throw new MongoSQLException(
					  "Timeout value can't be negative. ie. it must be 0 or above; timeout value is: "
								 + String.valueOf(timeout));
		}
//		try
//		{
//			this.mongoClient.datasets().list(this.projectId.replace("__", ":").replace("_", ".")).execute();
//		}
//		catch (IOException e)
//		{
//			return false;
//		}
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false to everything
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException
	{
		// TODO Implement
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * We returns the original sql statement
	 * </p>
	 *
	 * @return sql - the original statement
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException
	{
		logger.debug("Function called nativeSQL() " + sql);
		return sql;
		// TODO
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
	public CallableStatement prepareCall(String sql) throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "prepareCall(string)");
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
	public CallableStatement prepareCall(String sql, int resultSetType,
													 int resultSetConcurrency) throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "prepareCall(String,int,int)");
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
	public CallableStatement prepareCall(String sql, int resultSetType,
													 int resultSetConcurrency, int resultSetHoldability)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "prepareCall(string,int,int,int)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Creates and returns a PreparedStatement object
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		this.logger.debug(sql);
		return new MongoPreparedStatement(sql, this);
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
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "prepareStatement(string,int)");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
															int resultSetConcurrency) throws SQLException
	{
		this.logger.debug(sql);
		return new MongoPreparedStatement(sql, this, resultSetType, resultSetConcurrency);
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
	public PreparedStatement prepareStatement(String sql, int resultSetType,
															int resultSetConcurrency, int resultSetHoldability)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "prepareStatement(String,int,int,int)");
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
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "prepareStatement(String,int[])");
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
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			  throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "prepareStatement(String,String[])");
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
	public void releaseSavepoint(Savepoint savepoint) throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "releaseSavepoint(Savepoint)");
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
	public void rollback() throws SQLException
	{
		logger.debug("function call: rollback() not implemented ");
		//throw new MongoSQLException("Not implemented." + "rollback()");
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
	public void rollback(Savepoint savepoint) throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "rollback(savepoint)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Setter for autoCommitEnabled
	 * </p>
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException
	{
		this.autoCommitEnabled = autoCommit;
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
	public void setCatalog(String catalog) throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "setCatalog(catalog)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws SQLClientInfoException
	 */
	@Override
	public void setClientInfo(Properties properties)
			  throws SQLClientInfoException
	{
		SQLClientInfoException e = new SQLClientInfoException();
		e.setNextException(new MongoSQLException(
				  "Not implemented. setClientInfo(properties)"));
		throw e;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws SQLClientInfoException
	 */
	@Override
	public void setClientInfo(String name, String value)
			  throws SQLClientInfoException
	{
		SQLClientInfoException e = new SQLClientInfoException();
		e.setNextException(new MongoSQLException(
				  "Not implemented. setClientInfo(properties)"));
		throw e;
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
	public void setHoldability(int holdability) throws SQLException
	{
//		if (this.isclosed)
//		{
//			throw new MongoSQLException("Connection is closed.");
//		}
		throw new MongoSQLException("Not implemented." + "setHoldability(int)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * </p>
	 *
	 * @throws MongoSQLException
	 */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException
	{
		if (this.isClosed())
		{
			throw new MongoSQLException("This Connection is Closed");
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
	public Savepoint setSavepoint() throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "setSavepoint()");
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
	public Savepoint setSavepoint(String name) throws SQLException
	{
		throw new MongoSQLException("Not implemented." + "setSavepoint(String)");
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
	public void setTransactionIsolation(int level) throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "setTransactionIsolation(int)");
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
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException
	{
		throw new MongoSQLException("Not implemented."
				  + "setTypeMap(Map<String, Class<?>>");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Always throws SQLException
	 * </p>
	 *
	 * @return nothing
	 * @throws SQLException Always
	 */
	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException
	{
		throw new MongoSQLException("Not found");
	}

	//------------------------- for Jdk1.7 -----------------------------------

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int getNetworkTimeout() throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	public Properties getProperties()
	{
		return properties;
	}

	public String getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(String value)
	{
		this.timeZone = value;
	}

	public class AbortCommand implements Runnable
	{
		public void run()
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void abort(Executor executor) throws SQLException
	{
		if (isClosed())
		{
			return;
		}

		//SQL_PERMISSION_ABORT.checkGuard(this);

		AbortCommand command = new AbortCommand();
		if (executor != null)
		{
			executor.execute(command);
		}
		else
		{
			command.run();
		}
	}

	@Override
	public String getSchema() throws SQLException
	{
		return null;
	}

	@Override
	public void setSchema(String schema) throws SQLException
	{

	}
}