package com.slemma.jdbc;

import com.slemma.jdbc.query.MongoExecutionOptions;
import com.slemma.jdbc.query.MongoQuery;
import com.slemma.jdbc.query.MongoQueryExecutor;
import com.slemma.jdbc.query.MongoQueryParser;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Igor Shestakov.
 */
public class MongoStatement extends AbstractMongoStatement implements java.sql.Statement
{
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoConnection.class.getName());

	/**
	 * Constructor for BQStatement object just initializes local variables
	 *
	 * @param mongoConnection
	 */
	public MongoStatement(MongoConnection mongoConnection) {
		this.connection = mongoConnection;
		//this.resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
		this.resultSetType = ResultSet.TYPE_FORWARD_ONLY;
		this.resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
	}

	/**
	 * Constructor for BQStatement object just initializes local variables
	 *
	 * @param mongoConnection
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @throws MongoSQLException
	 */
	public MongoStatement(MongoConnection mongoConnection,
							 int resultSetType, int resultSetConcurrency) throws MongoSQLException {
		if (resultSetConcurrency == ResultSet.CONCUR_UPDATABLE) {
			throw new MongoSQLException(
					  "The Resultset Concurrency can't be ResultSet.CONCUR_UPDATABLE");
		}

		this.connection = mongoConnection;
		this.resultSetType = resultSetType;
		this.resultSetConcurrency = resultSetConcurrency;

	}

	/** {@inheritDoc} */

	@Override
	public ResultSet executeQuery(String query) throws SQLException
	{
		if (this.isClosed()) {
			throw new MongoSQLException("This Statement is Closed");
		}

		this.starttime = System.currentTimeMillis();

//		MongoClient mongoClient = this.connection.getMongoClient();
//		MongoDatabase database = mongoClient.getDatabase(this.connection.getDatabase());
		MongoQuery mongoQuery = MongoQueryParser.parse(query);
		MongoQueryExecutor executor = new MongoQueryExecutor(this.connection.getMongoClient());

		Properties connectionProperties = this.connection.getProperties();
		MongoExecutionOptions eOptions = new MongoExecutionOptions.MongoExecutionOptionsBuilder()
				  .batchSize(MongoDriverProperty.BATCH_SIZE.getInt(connectionProperties))
				  .samplingBatchSize(this.getFetchSize() != 0 ? this.getFetchSize() : MongoDriverProperty.BATCH_SIZE.getInt(connectionProperties))
				  .maxRows(this.getMaxRows())
				  .createOptions();

		return executor.run(this.connection.getDatabase(), mongoQuery, this, eOptions);
	}

	//------------------------- for Jdk1.7 -----------------------------------


	@Override
	public void closeOnCompletion() throws SQLException {

	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return false;
	}
}
