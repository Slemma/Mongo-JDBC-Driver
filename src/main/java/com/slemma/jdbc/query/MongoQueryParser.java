package com.slemma.jdbc.query;

import com.slemma.jdbc.MongoSQLException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Igor Shestakov.
 */
public class MongoQueryParser
{
	private static final Pattern fieldSelectionPattern = Pattern.compile("(.*)\\s+as\\s+(\\w*)");
	private static final Pattern countMembersPattern = Pattern.compile("^select\\s+count\\s*\\(\\s*distinct\\s+(.+?)\\.(.+)\\s*\\)\\s+as\\s+(.+)\\s+from\\s+\\((\\{(.)*\\})\\)\\s+as\\s+.*$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	private static final Pattern dimMembersPattern = Pattern.compile("^select(.+?)from\\s\\((\\{.+\\})\\)\\sas\\s+(.+)\\s+group by.*$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	public static String cleanQueryString(String query){
		return query.trim().replace("\n", " ");
	}

	public static MongoQuery parse(String query) throws MongoSQLException
	{
		String cleanedQuery = cleanQueryString(query);

		if (cleanedQuery.isEmpty() || cleanedQuery.equals("{}"))
			throw new MongoSQLException(String.format("No such command: '%s'", cleanedQuery));

		Matcher m;

		//try match count query
		m = countMembersPattern.matcher(cleanedQuery);
		if (m.matches()) {
			String sourceField = m.group(2);
			String resultField = m.group(3);
			String mqlQuery = m.group(4);
			return new CountMembersMixedQuery(sourceField, resultField, mqlQuery);
		}

		//try get dimension members query
		m = dimMembersPattern.matcher(cleanedQuery);
		if (m.matches()) {
			String selectedFieldsString = m.group(1).trim();
			String mqlQuery = m.group(2);
			String tableName = m.group(3);

			ArrayList<FieldExprDef> fDefList = new ArrayList<>();
			String[] fields = selectedFieldsString.split(",");
			for (String fieldString : fields)
			{
				Matcher fieldMatcher = fieldSelectionPattern.matcher(fieldString.trim());
				if (fieldMatcher.matches()) {
					String sourceField = fieldMatcher.group(1).replace(tableName + ".","");
					String resultFieldName = fieldMatcher.group(2);
					FieldExprDef fDef =  new FieldExprDef(sourceField, resultFieldName);
					fDefList.add(fDef);
				}
			}

			if (fDefList.size()>0)
				return new GetMembersMixedQuery(fDefList, mqlQuery);
		}

		return new MongoQuery(query);
	}
}
