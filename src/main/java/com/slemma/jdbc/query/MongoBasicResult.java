package com.slemma.jdbc.query;

import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.ConversionHelper;
import com.slemma.jdbc.MongoField;
import com.slemma.jdbc.MongoFieldPredictor;
import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Wrapper for mongo result
 * @author Igor Shestakov.
 */
public class MongoBasicResult extends MongoAbstractResult implements MongoResult
{
	public MongoBasicResult(Document result, MongoDatabase database, int maxRows) throws MongoSQLException
	{
		super(result, database, maxRows, null, false);
	}
}
