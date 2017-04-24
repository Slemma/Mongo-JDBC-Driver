package com.slemma.jdbc.query;

import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.ConversionHelper;
import com.slemma.jdbc.MongoField;
import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Wrapper for mongo result
 * @author Igor Shestakov.
 */
public class MongoCountMembersResult extends MongoAbstractResult implements MongoResult
{
	private CountMembersMixedQuery query;

	public MongoCountMembersResult(final CountMembersMixedQuery query, Document result, MongoDatabase database) throws MongoSQLException
	{
		super(result, database, Integer.MAX_VALUE-1, null, false);

		this.query = query;

		fields = new ArrayList<>();
		fields.add(new MongoField(Types.BIGINT, Long.class, new ArrayList<String>(){{add(query.getResultFieldName());}}));

		//create fake document
		int docCount = (this.documentList != null) ? this.documentList.size() : 0;
		Document fakeDoc = new Document(
				  query.getResultFieldName()
				  , docCount
		);
		this.documentList = new ArrayList<Document>();
		this.documentList.add(fakeDoc);
	}
}
