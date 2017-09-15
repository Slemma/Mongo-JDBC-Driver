package com.slemma.jdbc.query;

import com.mongodb.client.MongoDatabase;
import com.slemma.jdbc.ConversionHelper;
import com.slemma.jdbc.MongoField;
import com.slemma.jdbc.MongoFieldPredictor;
import com.slemma.jdbc.MongoSQLException;
import org.bson.Document;

import java.util.*;

/**
 * Wrapper for mongo result
 *
 * @author Igor Shestakov.
 */
public class MongoGetMembersResult extends MongoAbstractResult implements MongoResult
{
	private GetMembersMixedQuery query;

	private static Object GetFieldValueFromDocument(Document doc, List<String> path)
	{
		List<String> pathClone = new LinkedList<String>(path);
		if (pathClone.size() > 1)
		{
			Document docProp = (Document) doc.get(pathClone.get(0));
			pathClone.remove(0);
			if (docProp == null)
				return null;
			else
				return GetFieldValueFromDocument(docProp, pathClone);
		}
		else
		{
			return doc.get(path.get(0));
		}
	}

	public MongoGetMembersResult(final GetMembersMixedQuery query, Document result, MongoDatabase database, MongoExecutionOptions options) throws MongoSQLException
	{
		super(
				  result,
				  database,
				  new DocumentTransformer()
				  {
					  @Override
					  public Document transform(Document sourceDocument)
					  {
						  Document sourceDoc = sourceDocument;
						  Document transformedDoc = new Document();

						  List<String> sourceFieldPath;
						  for (FieldExprDef fieldExprDef : query.getSelectedFields())
						  {
							  if (fieldExprDef.getExpression().indexOf(MongoField.FIELD_LVL_DELIMETER) == -1) {
								  sourceFieldPath = new ArrayList<>();
								  sourceFieldPath.add(fieldExprDef.getExpression());
							  }
							  else	{
								  sourceFieldPath = Arrays.asList(fieldExprDef.getExpression().split(MongoField.FIELD_LVL_DELIMETER_REGEX_FOR_SPLIT));
							  }
							  transformedDoc.append(fieldExprDef.getAlias(), GetFieldValueFromDocument(sourceDoc, sourceFieldPath));
						  }
						  return transformedDoc;
					  }
				  },
				  true,
				  options
		);

		this.query = query;

		if (this.getDocumentCount() > 0) {
			MongoFieldPredictor predictor = new MongoFieldPredictor(this.getDocumentList(), options.getSamplingBatchSize());
			this.fields = predictor.getFields();
		} else {
			this.fields = new ArrayList<>();
		}
	}
}
