package com.slemma.jdbc;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ConversionHelper defines mappings from common Java types to
 * corresponding SQL types.
 */
public class ConversionHelper
{
	private static final Map<Class, Integer> javaToSqlRules;
	private static final Map<Integer, Integer> typeUniversality;
//	public  static final SimpleDateFormat TIMESTAMP_TZ_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
	public  static final SimpleDateFormat TIMESTAMP_TZ_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	static
	{
		javaToSqlRules = new HashMap<Class, Integer>();
		javaToSqlRules.put(String.class, Types.VARCHAR);
		javaToSqlRules.put(byte[].class, Types.BINARY);
		javaToSqlRules.put(boolean.class, Types.BOOLEAN);
		javaToSqlRules.put(Boolean.class, Types.BOOLEAN);
		javaToSqlRules.put(char.class, Types.CHAR);
		javaToSqlRules.put(Character.class, Types.CHAR);
		javaToSqlRules.put(short.class, Types.SMALLINT);
		javaToSqlRules.put(Short.class, Types.SMALLINT);
		javaToSqlRules.put(int.class, Types.INTEGER);
		javaToSqlRules.put(Integer.class, Types.INTEGER);
		javaToSqlRules.put(long.class, Types.BIGINT);
		javaToSqlRules.put(Long.class, Types.BIGINT);
		javaToSqlRules.put(float.class, Types.REAL);
		javaToSqlRules.put(Float.class, Types.REAL);
		javaToSqlRules.put(double.class, Types.DOUBLE);
		javaToSqlRules.put(Double.class, Types.DOUBLE);
		javaToSqlRules.put(java.sql.Date.class, Types.DATE);
		javaToSqlRules.put(Time.class, Types.TIME);
		javaToSqlRules.put(java.util.Date.class, Types.TIMESTAMP);
		javaToSqlRules.put(Timestamp.class, Types.TIMESTAMP);
		javaToSqlRules.put(org.bson.types.ObjectId.class, Types.VARCHAR);

		typeUniversality = new HashMap<Integer, Integer>();
		typeUniversality.put(Types.BINARY, 0);
		typeUniversality.put(Types.BOOLEAN, 1);
		typeUniversality.put(Types.SMALLINT, 2);
		typeUniversality.put(Types.BIGINT, 3);
		typeUniversality.put(Types.INTEGER, 4);
		typeUniversality.put(Types.REAL, 5);
		typeUniversality.put(Types.DOUBLE, 6);
		typeUniversality.put(Types.TIME, 7);
		typeUniversality.put(Types.DATE, 8);
		typeUniversality.put(Types.TIMESTAMP, 9);
		typeUniversality.put(Types.CHAR, 10);
		typeUniversality.put(Types.VARCHAR, 11);
	}

	public static boolean sqlTypeExists(Class javaClass)
	{
		return (javaToSqlRules.get(javaClass) != null) ? true : false;
	}

	public static int lookup(Class javaClass)
	{
		return javaToSqlRules.get(javaClass);
	}

	public static boolean isSecondTypeMoreUniversality(int type1, int type2) {
		int type1Priority = typeUniversality.get(type1);
		int type2Priority = typeUniversality.get(type2);

		return (type2Priority > type1Priority) ? true : false;
	}

	public static String getSqlTypeName(int type)
	{
		switch (type)
		{
			case Types.BIT:
				return "BIT";
			case Types.TINYINT:
				return "TINYINT";
			case Types.SMALLINT:
				return "SMALLINT";
			case Types.INTEGER:
				return "INTEGER";
			case Types.BIGINT:
				return "BIGINT";
			case Types.FLOAT:
				return "FLOAT";
			case Types.REAL:
				return "REAL";
			case Types.DOUBLE:
				return "DOUBLE";
			case Types.NUMERIC:
				return "NUMERIC";
			case Types.DECIMAL:
				return "DECIMAL";
			case Types.CHAR:
				return "CHAR";
			case Types.VARCHAR:
				return "VARCHAR";
			case Types.LONGVARCHAR:
				return "LONGVARCHAR";
			case Types.DATE:
				return "DATE";
			case Types.TIME:
				return "TIME";
			case Types.TIMESTAMP:
				return "TIMESTAMP";
			case Types.BINARY:
				return "BINARY";
			case Types.VARBINARY:
				return "VARBINARY";
			case Types.LONGVARBINARY:
				return "LONGVARBINARY";
			case Types.NULL:
				return "NULL";
			case Types.OTHER:
				return "OTHER";
			case Types.JAVA_OBJECT:
				return "JAVA_OBJECT";
			case Types.DISTINCT:
				return "DISTINCT";
			case Types.STRUCT:
				return "STRUCT";
			case Types.ARRAY:
				return "ARRAY";
			case Types.BLOB:
				return "BLOB";
			case Types.CLOB:
				return "CLOB";
			case Types.REF:
				return "REF";
			case Types.DATALINK:
				return "DATALINK";
			case Types.BOOLEAN:
				return "BOOLEAN";
			case Types.ROWID:
				return "ROWID";
			case Types.NCHAR:
				return "NCHAR";
			case Types.NVARCHAR:
				return "NVARCHAR";
			case Types.LONGNVARCHAR:
				return "LONGNVARCHAR";
			case Types.NCLOB:
				return "NCLOB";
			case Types.SQLXML:
				return "SQLXML";
		}

		return "?";
	}


	/**
	 * Retrieves the value  as a <code>java.sql.Date</code> object
	 * in the Java programming language.
	 * This method uses the given calendar to construct an appropriate millisecond
	 * value for the date if the underlying database does not store
	 * timezone information.
	 **/

	public static java.sql.Date getValueAsDate(Object value, Calendar cal) {
		if (value == null)
			return null;

		//TODO: support timezone
		if (value.getClass() == java.util.Date.class) {
			java.util.Date utilDate = (java.util.Date) value;
			return new java.sql.Date(utilDate.getTime());
		} else
			return (java.sql.Date)value;
	}

	public static java.sql.Date getValueAsDate(Object value) {
		return getValueAsDate(value, null);
	}

	public static java.sql.Timestamp getValueAsTimestamp(Object value, Calendar cal) {
		if (value == null)
			return null;
		if (value.getClass() == java.util.Date.class)
			return new Timestamp(((java.util.Date) value).getTime());
		else
			throw new IllegalArgumentException("Unsupported value type");
	}

	public static java.sql.Timestamp getValueAsTimestamp(Object value) {
		return getValueAsTimestamp(value, getCalendar("UTC"));
	}

	public static java.sql.Timestamp getValueAsTimestamp(Object value, String timeZone) {
		Calendar cal = ConversionHelper.getCalendar(timeZone);
		return getValueAsTimestamp(value, cal);
	}

	public static Calendar getCalendar(String timeZone)
	{
		return Calendar.getInstance(TimeZone.getTimeZone(timeZone), new Locale("ru"));
	}

}
