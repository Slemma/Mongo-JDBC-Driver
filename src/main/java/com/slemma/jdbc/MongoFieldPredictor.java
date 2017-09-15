package com.slemma.jdbc;

import org.bson.Document;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Field metadata predictor (predict result data types)
 *
 * @author Igor Shestakov.
 */
public class MongoFieldPredictor
{
	private final ArrayList<MongoField> fields = new ArrayList<>();

	public MongoFieldPredictor(ArrayList<Document> documents, int samplingBatchSize)
	{
		if (documents == null)
			throw new IllegalArgumentException("Documents list must be not null");

		if (samplingBatchSize <= 0)
			throw new IllegalArgumentException("SamplingBatchSize must be >0");

		if (documents.size() > 0)
		{
			Document doc = documents.get(0);
			//init fields
			sampleMetadata(doc, fields, new ArrayList<String>());

			int cntMax = (samplingBatchSize <= documents.size()) ? samplingBatchSize : documents.size();
			//correction
			for (int i = 1; i < cntMax; i++)
			{
				doc = documents.get(i);
				ArrayList<MongoField> fMC = new ArrayList<>();
				sampleMetadata(doc, fMC, new ArrayList<String>());
				MongoField previousField = null;
				for (MongoField fieldC : fMC)
				{
					//ищем поле среди идентифицированных ранее
					MongoField field = null;
					for (MongoField f : fields)
					{
						if (f.getName().equals(fieldC.getName())) {
							field = f;
							break;
						}
					}

					if (field == null)
					{
						//ищем позицию для вставки
						int insertIndex = 0;
						if (previousField != null) {
							for (MongoField f : fields)
							{
								insertIndex++;
								if (f.getName().equals(previousField.getName())) {
									break;
								}
							}
							fields.add(insertIndex, fieldC);
						} else {
							fields.add(fieldC);
						}
					}
					else if (fieldC != null
							  && field.getType() != fieldC.getType()
							  && ConversionHelper.isSecondTypeMoreUniversality(field.getType(), fieldC.getType()))
					{
						field.setType(fieldC.getType());
					}

					previousField = fieldC;
				}
			}
		}
	}

	public ArrayList<MongoField> getFields()
	{
		return fields;
	}

	private void sampleMetadata(Document sampleDocument, ArrayList<MongoField> fieldsList, ArrayList<String> levelPath)
	{
		for (Map.Entry<String, Object> entry : sampleDocument.entrySet())
		{
			ArrayList<String> path = (ArrayList<String>) levelPath.clone();
			path.add(entry.getKey());
			if (entry.getValue() != null)
			{
				if (entry.getValue().getClass() == Document.class)
				{
					sampleMetadata((Document) entry.getValue(), fieldsList, path);
				}
				else
				{
					if (ConversionHelper.sqlTypeExists(entry.getValue().getClass()))
					{
						int dataType = ConversionHelper.lookup(entry.getValue().getClass());
						MongoField field = new MongoField(dataType, entry.getValue().getClass(), path);
						fieldsList.add(field);
					}
				}
			}
			else
			{
				MongoField field = new MongoField(Types.VARCHAR, String.class, path);
				fieldsList.add(field);
			}
		}
	}
}
