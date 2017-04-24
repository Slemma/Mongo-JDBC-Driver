package com.slemma.jdbc.query;

import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.ConversionHelper;
import com.slemma.jdbc.MongoField;
import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Wrapper for mongo result
 * @author Igor Shestakov.
 */
public interface MongoResult
{
	ArrayList<Document> getDocumentList();

	Object[] asArray();

	int getDocumentCount();

	int getColumnCount();

	ArrayList<MongoField> getFields();

	MongoDatabase getDatabase();

	boolean fetchData(int batchSize) throws MongoSQLException;
}
