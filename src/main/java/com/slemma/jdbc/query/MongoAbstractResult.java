package com.slemma.jdbc.query;

import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.MongoField;
import com.slemma.jdbc.MongoFieldPredictor;
import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Wrapper for mongo result
 *
 * @author Igor Shestakov.
 */
public abstract class MongoAbstractResult implements MongoResult
{
	protected MongoDatabase database;
	protected final Document result;
	protected final int maxRows;

	private final ArrayList<String> docKeys = new ArrayList<>();
	private final boolean isDistinct;
	private DocumentTransformer documentTransformer;

	protected ArrayList<Document> documentList;
	protected ArrayList<MongoField> fields;

	protected void addDocuments(ArrayList<Document> documentList){
		if (this.documentList == null)
			this.documentList = new ArrayList<>();
		if (this.documentTransformer != null || isDistinct) {
			for (Document document : documentList)
			{
				if (this.documentTransformer != null) {
					document = this.documentTransformer.transform(document);
				}

				if (isDistinct) {
					if (docKeys.indexOf(document.toString()) == -1) {
						docKeys.add(document.toString());
						this.documentList.add(document);
					}
				} else {
					this.documentList.add(document);
				}
			}
		} else {
			this.documentList.addAll(documentList);
		}
	}

	public MongoAbstractResult(Document result, MongoDatabase database, DocumentTransformer transformer, boolean isDistinct, MongoExecutionOptions options) throws MongoSQLException
	{
		this.result = result;
		this.maxRows = options.getMaxRows();
		this.isDistinct = isDistinct;
		this.documentTransformer = transformer;
		this.database = database;

		fetchData(options.getBatchSize());

		if (this.getDocumentCount() > 0) {
			MongoFieldPredictor predictor = new MongoFieldPredictor(this.getDocumentList(), options.getSamplingBatchSize());
			this.fields = predictor.getFields();
		} else {
			this.fields = new ArrayList<>();
		}
	}

	public boolean fetchData(int batchSize) throws MongoSQLException
	{
		//todo: refact
		if (documentList != null)
			return false;

		if (this.result.containsKey("result"))
			addDocuments((ArrayList<Document>) this.result.get("result"));
		else if (this.result.containsKey("cursor"))
		{
			Document cursor = (Document) this.result.get("cursor");

			MongoNamespace namespace = new MongoNamespace((String) cursor.get("ns"));

			if (cursor.containsKey("firstBatch"))
				addDocuments((ArrayList<Document>) cursor.get("firstBatch"));
			else
				throw new UnsupportedOperationException("Not implemented yet. Cursors without firstBatch.");

			//receive other batches
			Boolean stopFetch = false;
			Long nextBatch = cursor.getLong("id");
			if (maxRows != 0)
				stopFetch = (nextBatch == null || nextBatch == 0 || this.documentList.size() >= maxRows);
			else
				stopFetch = (nextBatch == null || nextBatch == 0);

			while (!stopFetch)
			{
				int nextBatchSize = this.documentList.size() + batchSize < maxRows ? batchSize : (maxRows - this.documentList.size());
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
						stopFetch = (nextBatch == null || nextBatch == 0 || this.documentList.size() >= maxRows);
					else
						stopFetch = (nextBatch == null || nextBatch == 0);
				}
				catch (Exception e)
				{
					throw new MongoSQLException("Error: " + e.getMessage() + "\n Query: " + getMoreCommandString);
				}
			}
		}
		else
			this.documentList = new ArrayList<Document>(Arrays.asList(this.result));

		if (maxRows!=0 && this.documentList.size() > maxRows)
			this.documentList.subList(maxRows, this.documentList.size()).clear();

		return true;
	}

	public ArrayList<Document> getDocumentList()
	{
		return this.documentList;
	}

	public Object[] asArray()
	{
		return this.getDocumentList().toArray();
	}

	public int getDocumentCount()
	{
		return this.getDocumentList().size();
	}

	public int getColumnCount()
	{
		return fields.size();
	}

	public ArrayList<MongoField> getFields()
	{
		return fields;
	}

	public MongoDatabase getDatabase()
	{
		return database;
	}

	public DocumentTransformer getDocumentTransformer()
	{
		return documentTransformer;
	}
}
