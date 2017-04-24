package com.slemma.jdbc.query;

import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.util.Map;

/**
 * @author Igor Shestakov.
 */
public class MongoQuery
{

	private final String mqlQueryString;
	private final Document mqlCommand;

	public MongoQuery(String mqlQuery) throws MongoSQLException
	{
		this.mqlQueryString = mqlQuery;

		try
		{
			this.mqlCommand = Document.parse(mqlQuery);
		}
		catch (Exception e)
		{
			throw new MongoSQLException("Invalid query: " + e.getMessage() + "\n Query: " + mqlQuery);
		}
	}

	public String getMqlQueryString()
	{
		return mqlQueryString;
	}

	public Document getMqlCommand()
	{
		return mqlCommand;
	}

	public void injectBatchSize(int value, boolean replaceIfExistingGreater)
	{
//		boolean batchSizeExist = false;
//		for (Map.Entry<String, Object> cEntry : mqlCommand.entrySet())
//		{
//			if (cEntry.getKey().equalsIgnoreCase("batchsize"))
//			{
//				batchSizeExist = true;
//				int existingBatchSizeValue = (int)cEntry.getValue();
//				if (replaceIfExistingGreater && (value<existingBatchSizeValue))
//					cEntry.setValue(value);
//			}
//		}
//
//		if (!batchSizeExist)
//			mqlCommand.put("batchSize", value);
	}
}
