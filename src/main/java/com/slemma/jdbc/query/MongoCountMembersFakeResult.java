package com.slemma.jdbc.query;

import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.MongoField;
import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.sql.Types;
import java.util.ArrayList;

/**
 * Wrapper for mongo result
 *
 * @author Igor Shestakov.
 */
public class MongoCountMembersFakeResult extends MongoAbstractResult implements MongoResult
{
	private CountMembersMixedQuery query;

	public MongoCountMembersFakeResult(final CountMembersMixedQuery query, MongoDatabase database) throws MongoSQLException
	{
		super(
				  new Document(
							 "result",
							 new ArrayList<Document>() {{ add(new Document(query.getResultFieldName(), 0)); }}
				  ),
				  database,
				  Integer.MAX_VALUE - 1,
				  null,
				  false
		);


		this.query = query;

//		fields = new ArrayList<>();
//		fields.add(new MongoField(Types.BIGINT, Long.class, new ArrayList<String>()
//		{{
//				add(query.getResultFieldName());
//			}}));
//
//		//create fake document
//		int docCount = (this.documentList != null) ? this.documentList.size() : 0;
//		Document fakeDoc = new Document(
//				  query.getResultFieldName()
//				  , 0
//		);
//		this.documentList = new ArrayList<Document>();
//		this.documentList.add(fakeDoc);
	}
}
