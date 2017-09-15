package com.slemma.jdbc.query;

import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.AbstractMongoStatement;
import com.slemma.jdbc.MongoForwardOnlyResultSet;
import com.slemma.jdbc.MongoSQLException;
import com.slemma.jdbc.MongoScrollableResultSet;
import org.bson.Document;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Igor Shestakov.
 */
public class MongoQueryExecutor
{
	private final MongoClient mongoClient;

	public MongoQueryExecutor(MongoClient mongoClient)
	{
		this.mongoClient = mongoClient;
	}

	public java.sql.ResultSet run(String databaseName, MongoQuery query, AbstractMongoStatement statement, MongoExecutionOptions options) throws SQLException{
		return run(mongoClient.getDatabase(databaseName) , query, statement, options);
	}

	public java.sql.ResultSet run(MongoDatabase database, MongoQuery query, AbstractMongoStatement statement, MongoExecutionOptions options) throws SQLException
	{
		MongoResult mongoResult =  null;
		try
		{
			if (query instanceof CountMembersMixedQuery)
			{
				mongoResult = new MongoCountMembersFakeResult((CountMembersMixedQuery) query, database);
			} else if (query instanceof GetMembersMixedQuery) {
				Document queryResult = database.runCommand(query.getMqlCommand());
				mongoResult = new MongoGetMembersResult((GetMembersMixedQuery) query, queryResult, database, options);
			} else if (query instanceof MongoQuery) {
				query.injectBatchSize(options.getBatchSize(), true);

				Document queryResult = database.runCommand(query.getMqlCommand());
				if (statement.getFetchDirection() == ResultSet.TYPE_SCROLL_INSENSITIVE)
					mongoResult = new MongoBasicResult(queryResult, database, options);
				else
					mongoResult = new MongoForwardOnlyResult(queryResult, database, options);
			} else {
				throw new UnsupportedOperationException("Unhandled query type.");
			}
		}
		catch (MongoCommandException e)
		{
			throw new MongoSQLException(e);
		}

		if(statement.getResultSetType() == ResultSet.TYPE_SCROLL_INSENSITIVE) {
			return new MongoScrollableResultSet(mongoResult, statement);
		} else {
			return new MongoForwardOnlyResultSet(mongoResult, statement);
		}

	}
}
