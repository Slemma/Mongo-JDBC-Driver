package com.slemma.jdbc.query;

import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Wrapper for mongo result
 * @author Igor Shestakov.
 */
public class MongoForwardOnlyResult extends MongoAbstractResult implements MongoResult
{
	int documentCount;
	boolean firstBatchFetched;
	boolean lastBatch;

	public MongoForwardOnlyResult(Document result, MongoDatabase database, MongoExecutionOptions options) throws MongoSQLException
	{
		super(result, database, null, false, options);
	}

	@Override
	protected void addDocuments(ArrayList<Document> documentList)
	{
		if (this.documentList != null)
			this.documentList.clear();
		documentCount += documentList.size();
		super.addDocuments(documentList);
	}

	@Override
	public boolean fetchData(int batchSize) throws MongoSQLException
	{
		if (this.lastBatch)
			return false;

		if (this.result.containsKey("result"))
		{
			addDocuments((ArrayList<Document>) this.result.get("result"));
			this.firstBatchFetched = true;
			this.lastBatch = true;
		}
		else if (this.result.containsKey("cursor"))
		{
			Document cursor = (Document) this.result.get("cursor");

			MongoNamespace namespace = new MongoNamespace((String) cursor.get("ns"));

			if (!this.firstBatchFetched && cursor.containsKey("firstBatch"))
			{
				addDocuments((ArrayList<Document>) cursor.get("firstBatch"));
				this.firstBatchFetched = true;

				Long nextBatch = cursor.getLong("id");
				if (maxRows != 0)
					this.lastBatch = (nextBatch == null || nextBatch == 0 || documentCount >= maxRows);
				else
					this.lastBatch = (nextBatch == null || nextBatch == 0);
			}
			else
			{
				//receive next batch
				Long nextBatch = cursor.getLong("id");

				int nextBatchSize = documentCount + batchSize < maxRows ? batchSize : (maxRows - documentCount);
				String getMoreCommandString = "{\n" +
						  "   \"getMore\": " + nextBatch + ",\n" +
						  "   \"collection\": \"" + namespace.getCollectionName() + "\",\n" +
						  "   \"batchSize\": " + nextBatchSize + "\n" +
						  "}\n";

				Document docCommand;
				try
				{
					docCommand = Document.parse(getMoreCommandString);
					Document nextBatchData = database.runCommand(docCommand);
					Document nextCursor = (Document) nextBatchData.get("cursor");
					nextBatch = nextCursor.getLong("id");
					addDocuments((ArrayList<Document>) nextCursor.get("nextBatch"));

					if (maxRows != 0)
						this.lastBatch = (nextBatch == null || nextBatch == 0 || documentCount >= maxRows);
					else
						this.lastBatch = (nextBatch == null || nextBatch == 0);
				}
				catch (Exception e)
				{
					throw new MongoSQLException("Error: " + e.getMessage() + "\n Query: " + getMoreCommandString);
				}
			}
		}
		else
		{
			addDocuments(new ArrayList<Document>(Arrays.asList(this.result)));
			this.firstBatchFetched = true;
			this.lastBatch = true;
		}

		if (maxRows !=0 && documentCount > maxRows)
			this.documentList.subList(this.documentList.size() - (documentCount - maxRows), this.documentList.size()).clear();

		return true;
	}

}
