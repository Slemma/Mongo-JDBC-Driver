package com.slemma.jdbc;

import com.mongodb.MongoClient;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class MongoDatabaseMetadata implements DatabaseMetaData
{

	/**
	 * Reference for the Connection object that made this DatabaseMetadata
	 * object
	 */
	MongoConnection Connection;

	/**
	 * Reference for the Logger Class
	 */
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoConnection.class.getName());

	/**
	 * We currently doesn't support multiple open resultsets.
	 */
	static boolean multipleOpenResultsSupported = false;

	/**
	 * Constructor that initializes variables
	 *
	 * @param mongoConnection
	 */
	public MongoDatabaseMetadata(MongoConnection mongoConnection)
	{
		this.Connection = mongoConnection;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Since bigquery doesn't support stored jobs, we'll return with a false.
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean allProceduresAreCallable() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Only the usable Tables name are returned.
	 * </p>
	 *
	 * @return true
	 */
	@Override
	public boolean allTablesAreSelectable() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Since commit is not supported, we return false, and no result sets will
	 * be closed, nor committed
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * This feature missing from the driver, so we return with a false
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * This feature missing from the driver.
	 * </p>
	 *
	 * @return true
	 */
	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Bigquery doesn't has a delete function, we can delete only tables.
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean deletesAreDetected(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * LONGVARCHAR and LONGVARBINARY are not supported.
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * returns an empty resultset
	 * </p>
	 *
	 * @return empty resultset
	 */
	@Override
	public ResultSet getAttributes(String catalog, String schemaPattern,
											 String typeNamePattern, String attributeNamePattern)
			  throws SQLException
	{
		logger.debug("Function call getAttributes catalog: " +
				  (catalog != null ? catalog : "null") + ", schemaPattern: " +
				  (schemaPattern != null ? schemaPattern : "null") + ", typeNamePattern:" +
				  (typeNamePattern != null ? typeNamePattern : "null") + ", attributeNamePattern: " +
				  (attributeNamePattern != null ? attributeNamePattern : "null"));
		logger.debug("not implemented yet returning empty resultset");
		String[] Col = new String[21];
		Col[0] = "TYPE_CAT";
		// String => type catalog (may be null)
		Col[1] = "TYPE_SCHEM";
		// String => type schema (may be null)
		Col[2] = "TYPE_NAME";
		// String => type name
		Col[3] = "ATTR_NAME";
		// String => attribute name
		Col[4] = "DATA_TYPE";
		// int => attribute type SQL type from java.sql.Types
		Col[5] = "ATTR_TYPE_NAME";
		// String => Data source dependent type name. For a UDT, the type name
		// is fully qualified. For a REF, the type name is fully qualified and
		// represents the target type of the reference type.
		Col[6] = "ATTR_SIZE";
		// int => column size. For char or date types this is the maximum number
		// of characters; for numeric or decimal types this is precision.
		Col[7] = "DECIMAL_DIGITS";
		// int => the number of fractional digits. Null is returned for data
		// types where DECIMAL_DIGITS is not applicable.
		Col[8] = "NUM_PREC_RADIX";
		// int => Radix (typically either 10 or 2)
		Col[9] = "NULLABLE";
		// int => whether NULL is allowed
		Col[10] = "REMARKS";
		// String => comment describing column (may be null)
		Col[11] = "ATTR_DEF";
		// String => default value (may be null)
		Col[12] = "SQL_DATA_TYPE";
		// int => unused
		Col[13] = "SQL_DATETIME_SUB";
		// int => unused
		Col[14] = "CHAR_OCTET_LENGTH";
		// int => for char types the maximum number of bytes in the column
		Col[15] = "ORDINAL_POSITION";
		// int => index of the attribute in the UDT (starting at 1)
		Col[16] = "IS_NULLABLE";
		// String => ISO rules are used to determine the nullability for a
		// attribute.
		Col[17] = "SCOPE_CATALOG";
		// String => catalog of table that is the scope of a reference attribute
		// (null if DATA_TYPE isn't REF)
		Col[18] = "SCOPE_SCHEMA";
		// String => schema of table that is the scope of a reference attribute
		// (null if DATA_TYPE isn't REF)
		Col[19] = "SCOPE_TABLE";
		// String => table name that is the scope of a reference attribute (null
		// if the DATA_TYPE isn't REF)
		Col[20] = "SOURCE_DATA_TYPE";
		// short => source type of a distinct type or user-generated Ref
		// type,SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT
		// or user-generated REF)
		return new DMDResultSet(new Object[0][21], Col,
				  DMDResultSet.DMDResultSetType.getAttributes);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * This feature is not yet impelemented.
	 * </p>
	 *
	 * @return an empty dataset
	 */
	@Override
	public ResultSet getBestRowIdentifier(String catalog, String schema,
													  String table, int scope, boolean nullable) throws SQLException
	{
		logger.debug("Function call getBestRowIdentifier(String,String,String,int,boolean)");
		logger.debug("Not implemented yet returning empty resultset");
		String[] Col = new String[8];
		Col[0] = "SCOPE";
		// short => actual scope of result
		Col[1] = "COLUMN_NAME";
		// String => column name
		Col[2] = "DATA_TYPE";
		// int => SQL data type from java.sql.Types
		Col[3] = "TYPE_NAME";
		// String => Data source dependent type name, for a UDT the type name is
		// fully qualified
		Col[4] = "COLUMN_SIZE";
		// int => precision
		Col[5] = "BUFFER_LENGTH";
		// int => not used
		Col[6] = "DECIMAL_DIGITS";
		// short => scale - Null is returned for data types where DECIMAL_DIGITS
		// is not applicable.
		Col[7] = "PSEUDO_COLUMN";
		// short => is this a pseudo column like an Oracle ROWID
		return new DMDResultSet(new Object[0][8], Col,
				  DMDResultSet.DMDResultSetType.getBestRowIdentifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet getCatalogs() throws SQLException
	{
		MongoClient mClient = ((MongoConnection) this.getConnection()).getMongoClient();
		MongoIterable<String> databaseNames = mClient.listDatabaseNames();
		ArrayList<String> dbNames = new ArrayList<>();
		for (String databaseName : databaseNames)
			dbNames.add(databaseName);

		if (dbNames.size() > 0)
			return new DMDResultSet(dbNames.toArray(), new String[]{"TABLE_CAT"},
					  DMDResultSet.DMDResultSetType.getCatalogs);
		else
			return new DMDResultSet(new String[]{null},
					  new String[]{"TABLE_CAT"},
					  DMDResultSet.DMDResultSetType.getCatalogs);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns a '.' as a catalog separator
	 * </p>
	 *
	 * @return .
	 */
	@Override
	public String getCatalogSeparator() throws SQLException
	{
		return ".";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns the default catalog term: "project"
	 * </p>
	 *
	 * @return project
	 */
	@Override
	public String getCatalogTerm() throws SQLException
	{
		return "database";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * returns the supported clients in a result set
	 * </p>
	 *
	 * @return supported clients
	 */
	@Override
	public ResultSet getClientInfoProperties() throws SQLException
	{
		return new DMDResultSet(new Object[][]{
				  {"iSQL", 64, "iSQL", "http://isql.sourceforge.net/"}}

				  , new String[]{"NAME", "MAX_LEN", "DEFAULT_VALUE", "DESCRIPTION"},
				  DMDResultSet.DMDResultSetType.getClientInfoProperties);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Since no Grantor, Grantee, and privilege, this won't be used, we'll
	 * return an empty resultset
	 * </p>
	 *
	 * @returns a Resultset with no Data
	 */
	@Override
	public ResultSet getColumnPrivileges(String catalog, String schema,
													 String table, String columnNamePattern) throws SQLException
	{
		String[] Col = new String[8];
		Col[0] = "TABLE_CAT"; // String => table catalog (may be null)
		Col[1] = "TABLE_SCHEM"; // String => table schema (may be null)
		Col[2] = "TABLE_NAME"; // String => table name
		Col[3] = "COLUMN_NAME"; // String => column name
		Col[4] = "GRANTOR"; // String => grantor of access (may be null)
		Col[5] = "GRANTEE"; // String => grantee of access
		Col[6] = "PRIVILEGE"; // String => name of access (SELECT, INSERT,
		// UPDATE, REFRENCES, ...)
		Col[7] = "IS_GRANTABLE";// String => "YES" if grantee is permitted to
		// grant to others;
		// "NO" if not; null if unknown
		return new DMDResultSet(new Object[][]{{null, null, table, "", null,
				  "", "", "NO"}}, Col, DMDResultSet.DMDResultSetType.getColumnPrivileges);
		// TODO we might need this more implemented.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet getColumns(String catalog, String schemaPattern,
										 String tableNamePattern, String columnNamePattern)
			  throws SQLException
	{
		int resulSetColumnCount = 6;

		logger.debug("Function call getColumns...");
		MongoClient mClient = ((MongoConnection) this.getConnection()).getMongoClient();

		List<String> dbNames = new ArrayList<>();

		if (StringUtils.isNotEmpty(catalog))
			dbNames.add(catalog);
		else
		{
			MongoIterable<String> databaseNames = mClient.listDatabaseNames();
			for (String databaseName : databaseNames)
				dbNames.add(databaseName);
		}


		List<MongoResult> mResults = new ArrayList<>();
		List<String[]> data = new ArrayList<String[]>();
		for (String dbName : dbNames)
		{
			MongoDatabase db = mClient.getDatabase(dbName);
			MongoIterable<String> collectionNames = db.listCollectionNames();
			for (String collectionName : collectionNames)
			{
				if (StringUtils.isNotEmpty(tableNamePattern) && !collectionName.equals(tableNamePattern))
					continue;

				String query = "{ " +
						  "\"find\" : \"" + collectionName + "\"" +
						  ", \"limit\" : 50" +
						  ", \"batchSize\" : 50" +
						  "}";

				Document command = Document.parse(query);
				MongoResult mResult = new MongoResult(db.runCommand(command), db);
				for (MongoField field : mResult.getFields())
				{
					String[] columnMetadata = new String[resulSetColumnCount];
					//TABLE_CAT
					columnMetadata[0] = dbName;
					//TABLE_SCHEM
					columnMetadata[1] = dbName;
					//TABLE_NAME
					columnMetadata[2] = collectionName;
					//COLUMN_NAME
					columnMetadata[3] = field.getName();
					//DATA_TYPE
					columnMetadata[4] = String.valueOf(field.getType());
					//TYPE_NAME
					columnMetadata[5] = field.getTypeName();

					data.add(columnMetadata);
				}
			}

		}

		if (data.size() > 0)
		{
				String[][] List = new String[data.size()][resulSetColumnCount];
				for (int i = 0; i < data.size(); i++) {
					List[i] = data.get(i);
				}
				return new DMDResultSet(List, new String[] {
						  "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME",
						  "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME",
//								 "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS",
//								 "NUM_PREC_RADIX", "NULLABLE", "REMARKS",
//								 "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB",
//								 "CHAR_OCTET_LENGTH", "ORDINAL_POSITION",
//								 "IS_NULLABLE", "SCOPE_CATLOG", "SCOPE_SCHEMA",
//								 "SCOPE_TABLE", "SOURCE_DATA_TYPE","IS_AUTOINCREMENT",
				}, DMDResultSet.DMDResultSetType.getColumns);
		}
		else
		{
			return new DMDResultSet(new String[][]{
					  {
								 null, null, null, null, null, null
//								, null, null, null, null, null, null
// 							, null, null, null, null, null, null
// 							, null, null, null,null, null
					  }},
					  new String[]{
								 "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME",
								 "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME",
//								 "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS",
//								 "NUM_PREC_RADIX", "NULLABLE", "REMARKS",
//								 "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB",
//								 "CHAR_OCTET_LENGTH", "ORDINAL_POSITION",
//								 "IS_NULLABLE", "SCOPE_CATLOG", "SCOPE_SCHEMA",
//								 "SCOPE_TABLE", "SOURCE_DATA_TYPE","IS_AUTOINCREMENT",
					  }, DMDResultSet.DMDResultSetType.getColumns);
		}

//		logger.debug("Function call getColumns"
//				  + "catalog: " + (catalog != null ? catalog : "null") +
//				  ", schemaPattern: " + (schemaPattern != null ? schemaPattern : "null" )+
//				  ", tableNamePattern:" +
//				  (tableNamePattern != null ? tableNamePattern : "null") +
//				  ", columnNamePattern: " +
//				  (columnNamePattern != null ? columnNamePattern : "null"));
//
//		((MongoConnection)this.getConnection()).getDatabase();
//		List<Table> Tables = null;
//		try {
//			Tables = BQSupportFuncts.getTables(this.Connection, catalog,
//					  schemaPattern, tableNamePattern);
////            if(Tables == null){ //Because of Crystal Reports It's not elegant, but hey it works!
////                Tables = BQSupportFuncts.getTables(this.Connection, schemaPattern,
////                        catalog, tableNamePattern);
////            }
//		}
//		catch (IOException e) {
//			throw new SQLException(e);
//		}
//
//		if (Tables != null) {
//			List<String[]> data = new ArrayList<String[]>();
//			for (int i = 0; i < Tables.size(); i++) {
//				String UparsedId = Tables.get(i).getId();
//				String ProjectId = BQSupportFuncts
//						  .getProjectIdFromAnyGetId(UparsedId).replace(":", "__").replace(".", "_");
//				String DatasetId = BQSupportFuncts
//						  .getDatasetIdFromTableDotGetId(UparsedId);
//				String TableId = BQSupportFuncts
//						  .getTableIdFromTableDotGetId(UparsedId);
//
//				List<TableFieldSchema> tblfldschemas = Tables.get(i)
//						  .getSchema().getFields();
//				if (tblfldschemas != null && tblfldschemas.size() != 0) {
//					int index = 1;
//					for (TableFieldSchema Column : tblfldschemas) {
//						if (columnNamePattern == null
//								  || BQSupportFuncts.matchPattern(
//								  Column.getName(), columnNamePattern)) {
//							String[] Col = new String[23];
//							// TABLE_CAT String => table catalog (may be null)
//							Col[0] = ProjectId;
//							// TABLE_SCHEM String => table schema (may be null)
//							Col[1] = DatasetId;
//							// TABLE_NAME String => table name
//							Col[2] = TableId;
//							// COLUMN_NAME String => column name
//							Col[3] = Column.getName();
//							// DATA_TYPE int => SQL type from java.sql.Types
//							Col[4] = String.valueOf(BQSupportFuncts
//									  .parseToSqlFieldType(Column.getType()));
//							// TYPE_NAME String => Data source dependent type
//							// name, for a UDT the type name is fully qualified
//							Col[5] = Column.getType();
//							// COLUMN_SIZE int => column size. (In Bigquery max
//							// colsize is 64kb and its not specifically
//							// determined for fields)
//							Col[6] = "0"; //String.valueOf(Column.size());
//							// BUFFER_LENGTH is not used.
//							Col[7] = null;
//							// DECIMAL_DIGITS int => the number of fractional
//							// digits. Null is returned for data types where
//							// DECIMAL_DIGITS is not applicable.
//							Col[8] = null;
//							// NUM_PREC_RADIX int => Radix (typically either 10
//							// or 2)
//							Col[9] = null;
//							// NULLABLE int => is NULL allowed.
//							Col[10] = Column.getMode();
//							// REMARKS String => comment describing column (may
//							// be null)
//							Col[11] = null;
//							// COLUMN_DEF String => default value for the
//							// column, which should be interpreted as a string
//							// when the value is enclosed in single quotes (may
//							// be null)
//							Col[12] = "String";
//							// SQL_DATA_TYPE int => unused
//							Col[13] = Column.getType();
//							// SQL_DATETIME_SUB int => unused
//							Col[14] = null;
//							// CHAR_OCTET_LENGTH int => for char types the
//							// maximum number of bytes in the column
//							Col[15] = String.valueOf(64 * 1024);
//							// ORDINAL_POSITION int => index of column in table
//							// (starting at 1)
//							Col[16] = String.valueOf(index);
//							// IS_NULLABLE String => ISO rules are used to
//							// determine the nullability for a column.
//							Col[17] = "";
//							// SCOPE_CATLOG String => catalog of table that is
//							// the scope of a reference attribute (null if
//							// DATA_TYPE isn't REF)
//							Col[18] = null;
//							// SCOPE_SCHEMA String => schema of table that is
//							// the scope of a reference attribute (null if the
//							// DATA_TYPE isn't REF)
//							Col[19] = null;
//							// SCOPE_TABLE String => table name that this the
//							// scope of a reference attribure (null if the
//							// DATA_TYPE isn't REF)
//							Col[20] = null;
//							// SOURCE_DATA_TYPE short => source type of a
//							// distinct type or user-generated Ref type, SQL
//							// type from java.sql.Types (null if DATA_TYPE isn't
//							// DISTINCT or user-generated REF)
//							Col[21] = null;
//							// IS_AUTOINCREMENT String => Indicates whether this
//							// column is auto incremented
//							Col[22] = "";
//							data.add(Col);
//						}
//						index++;
//					}
//				}
//			}
//			if (data.size() == 0) {
//				return null;
//			}
//			else {
//				String[][] List = new String[data.size()][23];
//				for (int i = 0; i < data.size(); i++) {
//					List[i] = data.get(i);
//				}
//				return new DMDResultSet(List, new String[] { "TABLE_CAT",
//						  "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME",
//						  "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE",
//						  "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX",
//						  "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE",
//						  "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH",
//						  "ORDINAL_POSITION", "IS_NULLABLE", "SCOPE_CATLOG",
//						  "SCOPE_SCHEMA", "SCOPE_TABLE", "SOURCE_DATA_TYPE",
//						  "IS_AUTOINCREMENT", }, DMDResultSet.DMDResultSetType.getColumns);
//			}
//		}
//		else {
//			return new DMDResultSet(new String[][] {
//					  { null, null, null, null, null, null, null, null, null, null,
//								 null, null, null, null, null, null, null, null, null, null,
//								 null, null, null} },
//					  new String[] { "TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME",
//								 "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME",
//								 "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS",
//								 "NUM_PREC_RADIX", "NULLABLE", "REMARKS",
//								 "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB",
//								 "CHAR_OCTET_LENGTH", "ORDINAL_POSITION",
//								 "IS_NULLABLE", "SCOPE_CATLOG", "SCOPE_SCHEMA",
//								 "SCOPE_TABLE", "SOURCE_DATA_TYPE",
//								 "IS_AUTOINCREMENT", }, DMDResultSet.DMDResultSetType.getColumns);
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.sql.Connection getConnection() throws SQLException
	{
		return this.Connection;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Cross Reference Not supported, we return an empty resulset
	 * </p>
	 *
	 * @return an empty resultset
	 */
	@Override
	public ResultSet getCrossReference(String parentCatalog,
												  String parentSchema, String parentTable, String foreignCatalog,
												  String foreignSchema, String foreignTable) throws SQLException
	{
		logger.debug("Function call getCrossReference(String,String,String,String,String,String)");
		String[] Col = new String[14];
		Col[0] = "PKTABLE_CAT";
		// String => parent key table catalog (may be null)
		Col[1] = "PKTABLE_SCHEM";
		// String => parent key table schema (may be null)
		Col[2] = "PKTABLE_NAME";
		// String => parent key table name
		Col[3] = "PKCOLUMN_NAME";
		// String => parent key column name
		Col[4] = "FKTABLE_CAT";
		// String => foreign key table catalog (may be null) being exported (may
		// be null)
		Col[5] = "FKTABLE_SCHEM";
		// String => foreign key table schema (may be null) being exported (may
		// be null)
		Col[6] = "FKTABLE_NAME";
		// String => foreign key table name being exported
		Col[7] = "FCOLUMN_NAME";
		// String => foreign key column name being exported
		Col[8] = "KEY_SEQ";
		// short => sequence number within foreign key( a value of 1 represents
		// the first column of the foreign key, a value of 2 would represent the
		// second column within the foreign key).
		Col[9] = "UPDATE_RULE";
		// short => What happens to foreign key when parent key is updated:
		Col[10] = "DELETE_RULE";
		// short => What happens to the foreign key when parent key is deleted.
		Col[11] = "FK_NAME";
		// String => foreign key name (may be null)
		Col[12] = "PK_NAME";
		// String => parent key name (may be null)
		Col[13] = "DEFERRABILITY";
		// short => can the evaluation of foreign key constraints be deferred
		// until commit
		return new DMDResultSet(new Object[0][14], Col,
				  DMDResultSet.DMDResultSetType.getCrossReference);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Current version is 2-1.3.3 Returns 2
	 * </p>
	 */
	@Override
	public int getDatabaseMajorVersion() throws SQLException
	{
		return 2;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Current version is 2-1.3.3 Returns 1
	 * </p>
	 */
	@Override
	public int getDatabaseMinorVersion() throws SQLException
	{
		return 1;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns "Google Big Query"
	 * </p>
	 */
	@Override
	public String getDatabaseProductName() throws SQLException
	{
		return "Google Big Query";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns "google bigquery v2-1.3.3-beta"
	 * </p>
	 */
	@Override
	public String getDatabaseProductVersion() throws SQLException
	{
		return "google bigquery v2-1.3.3-beta";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Commit is not supported, so we return with no transaction
	 * </p>
	 * <p/>
	 * return TRANSACTION_NONE
	 */
	@Override
	public int getDefaultTransactionIsolation() throws SQLException
	{
		return java.sql.Connection.TRANSACTION_NONE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDriverMajorVersion()
	{
		return MongoDriver.getMajorVersionAsStatic();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDriverMinorVersion()
	{
		return MongoDriver.getMinorVersionAsStatic();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns "Starschema.net:BigQuery JDBC driver"
	 * </p>
	 */
	@Override
	public String getDriverName() throws SQLException
	{
		return "Starschema.net:BigQuery JDBC driver";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDriverVersion() throws SQLException
	{
		return MongoDriver.getMajorVersionAsStatic() + "."
				  + MongoDriver.getMinorVersionAsStatic();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not working.
	 * </p>
	 *
	 * @return an empty resultset
	 */
	@Override
	public ResultSet getExportedKeys(String catalog, String schema, String table)
			  throws SQLException
	{
		logger.debug("Function call getExportedKeys(String,String,String)");
		String[] Col = new String[14];
		Col[0] = "PKTABLE_CAT";
		// String => parent key table catalog (may be null)
		Col[1] = "PKTABLE_SCHEM";
		// String => parent key table schema (may be null)
		Col[2] = "PKTABLE_NAME";
		// String => parent key table name
		Col[3] = "PKCOLUMN_NAME";
		// String => parent key column name
		Col[4] = "FKTABLE_CAT";
		// String => foreign key table catalog (may be null) being exported (may
		// be null)
		Col[5] = "FKTABLE_SCHEM";
		// String => foreign key table schema (may be null) being exported (may
		// be null)
		Col[6] = "FKTABLE_NAME";
		// String => foreign key table name being exported
		Col[7] = "FCOLUMN_NAME";
		// String => foreign key column name being exported
		Col[8] = "KEY_SEQ";
		// short => sequence number within foreign key( a value of 1 represents
		// the first column of the foreign key, a value of 2 would represent the
		// second column within the foreign key).
		Col[9] = "UPDATE_RULE";
		// short => What happens to foreign key when parent key is updated:
		Col[10] = "DELETE_RULE";
		// short => What happens to the foreign key when parent key is deleted.
		Col[11] = "FK_NAME";
		// String => foreign key name (may be null)
		Col[12] = "PK_NAME";
		// String => parent key name (may be null)
		Col[13] = "DEFERRABILITY";
		// short => can the evaluation of foreign key constraints be deferred
		// until commit
		return new DMDResultSet(new Object[0][14], Col,
				  DMDResultSet.DMDResultSetType.getExportedKeys);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns "( ) : . , \\ \" / ' ' * < = > + - % & | ^ << >> ~ != <> >= <= "
	 * </p>
	 */
	@Override
	public String getExtraNameCharacters() throws SQLException
	{
		return "( ) : . , \\ \" / ' ' * < = > + - % & | ^ << >> ~ != <> >= <= ";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws SQLException
	 */
	@Override
	public ResultSet getFunctionColumns(String catalog, String schemaPattern,
													String functionNamePattern, String columnNamePattern)
			  throws SQLException
	{
		logger.debug("Function call getFunctionColumns(String,String,String,String)");
		throw new SQLException("Not implemented."
				  + "getFunctionColumns(String,String,String,String)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @throws SQLException
	 */
	@Override
	public ResultSet getFunctions(String catalog, String schemaPattern,
											String functionNamePattern) throws SQLException
	{
		logger.debug("Function call getFunctions(String,String,String)");
		throw new SQLException("Not implemented."
				  + "getFunctions(String,String,String)");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns "\\\""
	 * </p>
	 */
	@Override
	public String getIdentifierQuoteString() throws SQLException
	{
		return "\\\"";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Since we doesn't support keys, it returns an empty Result Set
	 * </p>
	 *
	 * @return ResultSet
	 */
	@Override
	public ResultSet getImportedKeys(String catalog, String schema, String table)
			  throws SQLException
	{
		logger.debug("Function call getImportedKeys(String,String,String)");
		String[] Col = new String[14];
		Col[0] = "PKTABLE_CAT";
		// String => primary key table catalog being imported (may be null)
		Col[1] = "PKTABLE_SCHEM";
		// String => primary key table schema being imported (may be null)
		Col[2] = "PKTABLE_NAME";
		// String => primary key table name being imported
		Col[3] = "PKCOLUMN_NAME";
		// String => primary key column name being imported
		Col[4] = "FKTABLE_CAT";
		// String => foreign key table catalog (may be null)
		Col[5] = "FKTABLE_SCHEM ";
		// String => foreign key table schema (may be null)
		Col[6] = "FKTABLE_NAME";
		// String => foreign key table name
		Col[7] = "FKCOLUMN_NAME";
		// String => foreign key column name
		Col[8] = "KEY_SEQ";
		// short => sequence number within a foreign key( a value of 1
		// represents the first column
		// of the foreign key, a value of 2 would represent the second column
		// within the foreign key).
		Col[9] = "UPDATE_RULE";
		// short => What happens to a foreign key when the primary key is
		// updated:
		Col[10] = "DELETE_RULE";
		// short => What happens to the foreign key when primary is deleted.
		Col[11] = "FK_NAME";
		// String => foreign key name (may be null)
		Col[12] = "PK_NAME";
		// String => primary key name (may be null)
		Col[13] = "DEFERRABILITY";
		// short => can the evaluation of foreign key constraints be deferred
		// until commit
		return new DMDResultSet(new Object[0][14], Col,
				  DMDResultSet.DMDResultSetType.getImportedKeys);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Currently not supported.
	 * </p>
	 *
	 * @return An empty Result Set
	 */
	@Override
	public ResultSet getIndexInfo(String catalog, String schema, String table,
											boolean unique, boolean approximate) throws SQLException
	{
		logger.debug("Function call getIndexInfo(String,String,String,boolean,boolean) returning an empty resultset");
		String[] Col = new String[13];
		Col[0] = "TABLE_CAT";
		// String => table catalog (may be null)
		Col[1] = "TABLE_SCHEM";
		// String => table schema (may be null)
		Col[2] = "TABLE_NAME";
		// String => table name
		Col[3] = "NON_UNIQUE";
		// boolean => Can index values be non-unique. false when TYPE is
		// tableIndexStatistic
		Col[4] = "INDEX_QUALIFIER";
		// String => index catalog (may be null); null when TYPE is
		// tableIndexStatistic
		Col[5] = "INDEX_NAME";
		// String => index name; null when TYPE is tableIndexStatistic
		Col[6] = "TYPE";
		// short => index type:
		Col[7] = "ORDINAL_POSITION";
		// short => column sequence number within index; zero when TYPE is
		// tableIndexStatistic
		Col[8] = "COLUMN_NAME";
		// String => column name; null when TYPE is tableIndexStatistic
		Col[9] = "ASC_OR_DESC";
		// String => column sort sequence, "A" => ascending, "D" => descending,
		// may be null if sort sequence is not supported; null when TYPE is
		// tableIndexStatistic
		Col[10] = "CARDINALITY";
		// int => When TYPE is tableIndexStatistic, then this is the number of
		// rows in the table; otherwise, it is the number of unique values in
		// the index.
		Col[11] = "PAGES";
		// int => When TYPE is tableIndexStatisic then this is the number of
		// pages used for the table, otherwise it is the number of pages used
		// for the current index.
		Col[12] = "FILTER_CONDITION";
		// String => Filter condition, if any. (may be null)
		return new DMDResultSet(new Object[0][13], Col,
				  DMDResultSet.DMDResultSetType.getIndexInfo);
		// TODO implement it more
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getJDBCMajorVersion() throws SQLException
	{
		return MongoDriver.getMajorVersionAsStatic();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getJDBCMinorVersion() throws SQLException
	{
		return MongoDriver.getMinorVersionAsStatic();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxBinaryLiteralLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxCatalogNameLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxCharLiteralLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxColumnNameLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxColumnsInGroupBy() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxColumnsInIndex() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxColumnsInOrderBy() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxColumnsInSelect() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 0
	 * </p>
	 */
	@Override
	public int getMaxColumnsInTable() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known.
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxConnections() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxCursorNameLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * No index in bigquery, so limit is not known
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxIndexLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Procedures not supported currently
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxProcedureNameLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns 64 * 1024
	 * </p>
	 */
	@Override
	public int getMaxRowSize() throws SQLException
	{
		return 64 * 1024;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxSchemaNameLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxStatementLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxStatements() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxTableNameLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known;
	 * </p>
	 *
	 * @return 0;
	 */
	@Override
	public int getMaxTablesInSelect() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Limit is not known
	 * </p>
	 *
	 * @return 0
	 */
	@Override
	public int getMaxUserNameLength() throws SQLException
	{
		return 0;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns: <br>
	 * { fn ABS() }, <br>
	 * { fn ACOS() }, <br>
	 * { fn ACOSH() }, <br>
	 * { fn ASIN() }, <br>
	 * { fn ASINH() }, <br>
	 * { fn ATAN() }, <br>
	 * { fn ATANH() }, <br>
	 * { fn ATAN2() }, <br>
	 * { fn CEIL() }, <br>
	 * { fn COS() }, <br>
	 * { fn COSH() }, <br>
	 * { fn COT() }, <br>
	 * { fn DEGREES() }, <br>
	 * { fn FLOOR() }, <br>
	 * { fn LN() }, <br>
	 * { fn LOG() }, <br>
	 * { fn LOG2() }, <br>
	 * { fn LOG10() }, <br>
	 * { fn PI() }, <br>
	 * { fn POW() }, <br>
	 * { fn RADIANS() }, <br>
	 * { fn ROUND() }, <br>
	 * { fn SIN() }, <br>
	 * { fn SINH() }, <br>
	 * { fn SQRT() }, <br>
	 * { fn TAN() }, <br>
	 * { fn TANH() }
	 * </p>
	 */
	@Override
	public String getNumericFunctions() throws SQLException
	{
		return "{ fn  ABS() }," + "{ fn  ACOS() }," + "{ fn  ACOSH() },"
				  + "{ fn  ASIN() }," + "{ fn  ASINH() }," + "{ fn  ATAN() },"
				  + "{ fn  ATANH() }," + "{ fn  ATAN2() }," + "{ fn  CEIL() },"
				  + "{ fn  COS() }," + "{ fn  COSH() }," + "{ fn  COT() },"
				  + "{ fn  DEGREES() }," + "{ fn  FLOOR() }," + "{ fn  LN() },"
				  + "{ fn  LOG() }," + "{ fn  LOG2() }," + "{ fn  LOG10() },"
				  + "{ fn  PI() }," + "{ fn  POW() }," + "{ fn  RADIANS() },"
				  + "{ fn  ROUND() }," + "{ fn  SIN() }," + "{ fn  SINH() },"
				  + "{ fn  SQRT() }," + "{ fn  TAN() }," + "{ fn  TANH() }";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * No primary keys in BigQuery, so we return an empty Result Set
	 * </p>
	 *
	 * @return ResultSet
	 */
	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table)
			  throws SQLException
	{
		logger.debug("Function call getPrimaryKeys(String,String,String)");
		String[] Col = new String[6];
		Col[0] = "TABLE_CAT";
		// String => table catalog (may be null)
		Col[1] = "TABLE_SCHEM";
		// String => table schema (may be null)
		Col[2] = "TABLE_NAME";
		// String => table name
		Col[3] = "COLUMN_NAME";
		// String => column name
		Col[4] = "KEY_SEQ";
		// short => sequence number within primary key( a value of 1 represents
		// the first column of the primary key, a value of 2 would represent the
		// second column within the primary key).
		Col[5] = "PK_NAME";
		// String => primary key name (may be null)
		return new DMDResultSet(new Object[0][8], Col,
				  DMDResultSet.DMDResultSetType.getPrimaryKeys);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Procedures not supported, so we return with an empty ResultSet
	 * </p>
	 *
	 * @return empty resultset
	 */
	@Override
	public ResultSet getProcedureColumns(String catalog, String schemaPattern,
													 String procedureNamePattern, String columnNamePattern)
			  throws SQLException
	{
		logger.debug("Function call getProcedureColumns(String,String,String,String)");
		String[] Col = new String[20];
		Col[0] = "PROCEDURE_CAT";
		// String => procedure catalog (may be null)
		Col[1] = "PROCEDURE_SCHEM";
		// String => procedure schema (may be null)
		Col[2] = "PROCEDURE_NAME";
		// String => procedure name
		Col[3] = "COLUMN_NAME";
		// String => column/parameter name
		Col[4] = "COLUMN_TYPE";
		// Short => kind of column/parameter:
		Col[5] = "DATA_TYPE";
		// int => SQL type from java.sql.Types
		Col[6] = "TYPE_NAME";
		// String => SQL type name, for a UDT type the type name is fully
		// qualified
		Col[7] = "PRECISION";
		// int => precision
		Col[8] = "LENGTH";
		// int => length in bytes of data
		Col[9] = "SCALE";
		// short => scale - null is returned for data types where SCALE is not
		// applicable.
		Col[10] = "RADIX";
		// short => radix
		Col[11] = "NULLABLE";
		// short => can it contain NULL.
		Col[12] = "REMARKS";
		// String => comment describing parameter/column
		Col[13] = "COLUMN_DEF";
		// String => default value for the column, which should be interpreted
		// as a string when the value is enclosed in single quotes (may be null)
		Col[14] = "SQL_DATA_TYPE";
		// int => reserved for future use
		Col[15] = "SQL_DATETIME_SUB";
		// int => reserved for future use
		Col[16] = "CHAR_OCTET_LENGTH";
		// int => the maximum length of binary and character based columns.
		// For any other datatype the returned value is a NULL
		Col[17] = "ORDINAL_POSITION";
		// int => the ordinal position, starting from 1, for the input and
		// output
		// parameters for a procedure. A value of 0 is returned if this row
		// describes
		// the procedure's return value. For result set columns, it is the
		// ordinal
		// position of the column in the result set starting from 1. If there
		// are
		// multiple result sets, the column ordinal positions are implementation
		// defined.
		Col[18] = "IS_NULLABLE";
		// String => ISO rules are used to determine the nullability for a
		// column.
		Col[19] = "SPECIFIC_NAME";
		// String => the name which uniquely identifies this procedure within
		// its schema.
		return new DMDResultSet(new Object[0][20], Col,
				  DMDResultSet.DMDResultSetType.getProcedureColumns);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not yet impelmented
	 * https://developers.google.com/bigquery/docs/reference/v2/
	 * https://developers.google.com/bigquery/docs/query-reference
	 * </p>
	 *
	 * @return an empty Result Set
	 */
	@Override
	public ResultSet getProcedures(String catalog, String schemaPattern,
											 String procedureNamePattern) throws SQLException
	{
		logger.debug("Function call getProcedures(String,String,String)");
		String[] Col = new String[9];
		Col[0] = "PROCEDURE_CAT";
		// String => procedure catalog (may be null)
		Col[1] = "PROCEDURE_SCHEM";
		// String => procedure schema (may be null)
		Col[2] = "PROCEDURE_NAME";
		// String => procedure name
		Col[3] = ""; // reserved for future use
		Col[4] = ""; // reserved for future use
		Col[5] = ""; // reserved for future use
		Col[6] = "REMARKS";
		// String => explanatory comment on the procedure
		Col[7] = "PROCEDURE_TYPE";
		// short => kind of procedure:
		Col[8] = "SPECIFIC_NAME";
		// String => The name which uniquely identifies this procedure within
		// its schema.
		return new DMDResultSet(new Object[0][9], Col,
				  DMDResultSet.DMDResultSetType.getProcedures);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Procedures are not supported
	 * </p>
	 *
	 * @return ""
	 */
	@Override
	public String getProcedureTerm() throws SQLException
	{
		return "";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns ResultSet.CLOSE_CURSORS_AT_COMMIT
	 * </p>
	 */
	@Override
	public int getResultSetHoldability() throws SQLException
	{
		return ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns RowIdLifetime.ROWID_UNSUPPORTED
	 * </p>
	 */
	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException
	{
		return RowIdLifetime.ROWID_UNSUPPORTED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet getSchemas() throws SQLException
	{
		return new DMDResultSet(new String[][]{{null, null}},
				  new String[]{"TABLE_SCHEM", "TABLE_CATALOG"},
				  DMDResultSet.DMDResultSetType.getSchemas);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Similar to getSchemas, but we can filter the results with the catalog and
	 * schema
	 * <p/>
	 * </p>
	 *
	 * @param catalog       - the catalog, AKA the project ID
	 * @param schemaPattern - the schema name, AKA the dataset name
	 * @return the schemas
	 */
	@Override
	public ResultSet getSchemas(String catalog, String schemaPattern)
			  throws SQLException
	{
		return this.getSchemas();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns "schema"
	 * </p>
	 */
	@Override
	public String getSchemaTerm() throws SQLException
	{
		return "schema";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns ""
	 * </p>
	 */
	@Override
	public String getSearchStringEscape() throws SQLException
	{
		return "";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * </p>
	 */
	@Override
	public String getSQLKeywords() throws SQLException
	{
		return "PI,POW,RADIANS,ROUND,SIN,SINH,SQRT,TAN,TANH,BOOLEAN,"
				  + "HEX_STRING,STRING,IFNULL,IS_INF,IS_NAN,IS_EXPLICITLY_DEFINED,"
				  + "FORMAT_IP,PARSE_IP,FORMAT_PACKED_IP,PARSE_PACKED_IP,"
				  + "REGEXP_MATCH,REGEXP_EXTRACT,REGEXP_REPLACE,CONCAT,"
				  + "LENGTH,LOWER,LPAD,RIGHT,RPAD,SUBSTR,UPPER,"
				  + "FORMAT_UTC_USEC,NOW,PARSE_UTC_USEC,STRFTIME_UTC_USEC,"
				  + "UTC_USEC_TO_DAY,UTC_USEC_TO_HOUR,UTC_USEC_TO_MONTH,"
				  + "UTC_USEC_TO_WEEK,UTC_USEC_TO_YEAR,POSITION";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns DatabaseMetaData.sqlStateSQL
	 * </p>
	 */
	@Override
	public int getSQLStateType() throws SQLException
	{
		return DatabaseMetaData.sqlStateSQL;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns: <br>
	 * { fn CONCAT() },<br>
	 * { fn LEFT() },<br>
	 * { fn LENGTH() },<br>
	 * { fn LOWER() },<br>
	 * { fn LPAD() },<br>
	 * { fn RIGHT() },<br>
	 * { fn RPAD() },<br>
	 * { fn SUBSTR() },<br>
	 * { fn UPPER( },<br>
	 * </p>
	 */
	@Override
	public String getStringFunctions() throws SQLException
	{
		return "{ fn CONCAT() }," + "{ fn LEFT() }," + "{ fn LENGTH() },"
				  + "{ fn LOWER() }," + "{ fn LPAD() }," + "{ fn RIGHT() },"
				  + "{ fn RPAD() }," + "{ fn SUBSTR() }," + "{ fn UPPER( },";
		// expr CONTAINS 'str'
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented yet.
	 * </p>
	 *
	 * @return an empty ResultSet
	 */
	@Override
	public ResultSet getSuperTables(String catalog, String schemaPattern,
											  String tableNamePattern) throws SQLException
	{
		String[] Col = new String[4];
		Col[0] = "TABLE_CAT";
		// String => the type's catalog (may be null)
		Col[1] = "TABLE_SCHEM";
		// String => type's schema (may be null)
		Col[2] = "TABLE_NAME";
		// String => type name
		Col[3] = "SUPERTABLE_NAME";
		// String => the direct super type's
		return new DMDResultSet(new Object[0][4], Col,
				  DMDResultSet.DMDResultSetType.getSuperTables);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Super Types are not supported
	 * </p>
	 *
	 * @return empty ResultSet
	 */
	@Override
	public ResultSet getSuperTypes(String catalog, String schemaPattern,
											 String typeNamePattern) throws SQLException
	{
		String[] Col = new String[6];
		Col[0] = "TYPE_CAT";
		// String => the UDT's catalog (may be null)
		Col[1] = "TYPE_SCHEM";
		// String => UDT's schema (may be null)
		Col[2] = "TYPE_NAME";
		// String => type name of the UDT
		Col[3] = "SUPERTYPE_CAT";
		// String => the direct super type's catalog (may be null)
		Col[4] = "SUPERTYPE_SCHEM";
		// String => the direct super type's schema (may be null)
		Col[5] = "SUPERTYPE_NAME";
		// String => the direct super type's name
		return new DMDResultSet(new Object[0][6], Col,
				  DMDResultSet.DMDResultSetType.getSuperTypes);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns ""
	 * </p>
	 */
	@Override
	public String getSystemFunctions() throws SQLException
	{
		return "";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns an empty resultset
	 * </p>
	 */
	@Override
	public ResultSet getTablePrivileges(String catalog, String schemaPattern,
													String tableNamePattern) throws SQLException
	{
		logger.debug("Function call getTablePrivileges(String,String,String)");
		String[] Col = new String[8];
		Col[0] = "TABLE_CAT"; // String => table catalog (may be null)
		Col[1] = "TABLE_SCHEM"; // String => table schema (may be null)
		Col[2] = "TABLE_NAME"; // String => table name
		Col[3] = "COLUMN_NAME"; // String => column name
		Col[4] = "GRANTOR"; // String => grantor of access (may be null)
		Col[5] = "GRANTEE"; // String => grantee of access
		Col[6] = "PRIVILEGE"; // String => name of access (SELECT, INSERT,
		// UPDATE, REFRENCES, ...)
		Col[7] = "IS_GRANTABLE";// String => "YES" if grantee is permitted to
		// grant to others;
		// "NO" if not; null if unknown
		return new DMDResultSet(new Object[0][8], Col,
				  DMDResultSet.DMDResultSetType.getTablePrivileges);
		// TODO we might need this more implemented.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet getTables(String catalog, String schemaPattern,
										String tableNamePattern, String[] types) throws SQLException
	{
		if (catalog != null && (catalog == " " || catalog.length() == 0))
		{
			logger.debug("Catalog length was: " +
					  catalog.length() + " it's an empty string replacing it with 'null' ");
			catalog = null;
		}
		if (schemaPattern != null)
		{
			if (schemaPattern == " " || schemaPattern.length() == 0)
			{
				logger.debug("schemaPattern length was: " +
						  schemaPattern.length() + " it's an empty string replacing it with 'null' ");
				schemaPattern = null;
			}
		}
		String typesToLog = "";
		if (types != null)
		{
			for (String string : types)
			{
				typesToLog += string + " , ";
			}
		}
		MongoDatabase db = ((MongoConnection) this.getConnection()).getNativeDatabase();
		ListCollectionsIterable<Document> collections = db.listCollections();
		ArrayList<Document> tables = new ArrayList<>();
		for (Document collection : collections)
			tables.add(collection);

		if (tables.size() != 0)
		{
			String[][] data = new String[tables.size()][10];
			for (int i = 0; i < tables.size(); i++)
			{
				String tableName = tables.get(i).getString("name");
				data[i][0] = ((MongoConnection) this.getConnection()).getDatabase();
				data[i][1] = null;
				data[i][2] = tableName;
				data[i][3] = "TABLE";
			}
			return new DMDResultSet(data, new String[]{"TABLE_CAT",
					  "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE"},
					  DMDResultSet.DMDResultSetType.getTables);
		}
		else
		{
			return new DMDResultSet(new String[][]{
					  {null, null, null, null, null, null, null, null, null, null}},
					  new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME",
								 "TABLE_TYPE"}, DMDResultSet.DMDResultSetType.getTables);
		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns {@code List<String>}contaning only "TABLE"
	 * </p>
	 */
	@Override
	public ResultSet getTableTypes() throws SQLException
	{
		logger.debug("Function call getTableTypes()");
		List<String> Res = new ArrayList<String>();
		Res.add("TABLE");
		return new DMDResultSet(Res.toArray(), new String[]{"TABLE_TYPE"},
				  DMDResultSet.DMDResultSetType.getTableTypes);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns: <br>
	 * { fn FORMAT_UTC_USEC() }, <br>
	 * { fn NOW() }, <br>
	 * { fn PARSE_UTC_USEC() }, <br>
	 * { fn STRFTIME_UTC_USEC() }, <br>
	 * { fn UTC_USEC_TO_DAY() }, <br>
	 * { fn UTC_USEC_TO_HOUR() }, <br>
	 * { fn UTC_USEC_TO_MONTH() }, <br>
	 * { fn UTC_USEC_TO_WEEK() }, <br>
	 * { fn UTC_USEC_TO_YEAR() }
	 * </p>
	 */
	@Override
	public String getTimeDateFunctions() throws SQLException
	{
		return "{ fn FORMAT_UTC_USEC() }," + "{ fn NOW() },"
				  + "{ fn PARSE_UTC_USEC() }," + "{ fn STRFTIME_UTC_USEC() },"
				  + "{ fn UTC_USEC_TO_DAY() }," + "{ fn UTC_USEC_TO_HOUR() },"
				  + "{ fn UTC_USEC_TO_MONTH() }," + "{ fn UTC_USEC_TO_WEEK() },"
				  + "{ fn UTC_USEC_TO_YEAR() }";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not yet implemented, returning an empty Result Set
	 * </p>
	 *
	 * @return an empty ResultSet
	 */
	@Override
	public ResultSet getTypeInfo() throws SQLException
	{
		logger.debug("Function call getTypeInfo()");
		String[] Col = new String[18];
		Col[0] = "TYPE_NAME"; // String => Type name
		Col[1] = "DATA_TYPE"; // int => SQL data type from java.sql.Types
		Col[2] = "PRECISION"; // int => maximum precision
		Col[3] = "LITERAL_PREFIX"; // String => prefix used to quote a literal
		// (may be null)
		Col[4] = "LITERAL_SUFFIX"; // String => suffix used to quote a literal
		// (may be null)
		Col[5] = "CREATE_PARAMS"; // String => parameters used in creating the
		// type (may be null)
		Col[6] = "NULLABLE"; // short => can you use NULL for this type.
		Col[7] = "CASE_SENSITIVE"; // boolean=> is it case sensitive.
		Col[8] = "SEARCHABLE"; // short => can you use "WHERE" based on this
		// type:
		Col[9] = "UNSIGNED_ATTRIBUTE"; // boolean => is it unsigned.
		Col[10] = "FIXED_PREC_SCALE"; // boolean => can it be a money value.
		Col[11] = "AUTO_INCREMENT"; // boolean => can it be used for an
		// auto-increment value.
		Col[12] = "LOCAL_TYPE_NAME"; // String => localized version of type name
		// (may be null)
		Col[13] = "MINIMUM_SCALE"; // short => minimum scale supported
		Col[14] = "MAXIMUM_SCALE"; // short => maximum scale supported
		Col[15] = "SQL_DATA_TYPE"; // int => unused
		Col[16] = "SQL_DATETIME_SUB";// int => unused
		Col[17] = "NUM_PREC_RADIX"; // int => usually 2 or 10
		return new DMDResultSet(new Object[0][18], Col,
				  DMDResultSet.DMDResultSetType.getTypeInfo);
		// TODO we might need this more implemented
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not supported, so we're returning an empty ResultSet
	 * </p>
	 *
	 * @return an empty ResultSet
	 */
	@Override
	public ResultSet getUDTs(String catalog, String schemaPattern,
									 String typeNamePattern, int[] types) throws SQLException
	{
		logger.debug("Function call getUDTs(String,String,String,int[])");
		logger.debug("not implemented, returning an empty resultset");
		String[] Col = new String[7];
		Col[0] = "TYPE_CAT";
		// String => the type's catalog (may be null)
		Col[1] = "TYPE_SCHEM";
		// String => type's schema (may be null)
		Col[2] = "TYPE_NAME";
		// String => type name
		Col[3] = "CLASS_NAME";
		// String => Java class name
		Col[4] = "DATA_TYPE";
		// int => type value defined in java.sql.Types. One of JAVA_OBJECT,
		// STRUCT, or DISTINCT
		Col[5] = "REMARKS";
		// String => explanatory comment on the type
		Col[6] = "BASE_TYPE";
		// short => type code of the source type of a DISTINCT type or ...
		return new DMDResultSet(new Object[0][7], Col, DMDResultSet.DMDResultSetType.getUDTs);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns an URL to the Bigquery Homepage.
	 * </p>
	 */
	@Override
	public String getURL() throws SQLException
	{
		return "https://developers.google.com/bigquery/";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns ProjectId
	 * </p>
	 */
	@Override
	public String getUserName() throws SQLException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * returns an empty resultset
	 * </p>
	 */
	@Override
	public ResultSet getVersionColumns(String catalog, String schema,
												  String table) throws SQLException
	{
		logger.debug("Function call getVersionColumns(String,String,String)");
		logger.debug("not implemented, returning an empty resultset");
		String[] Col = new String[8];
		Col[0] = "SCOPE";
		// short => is not used
		Col[1] = "COLUMN_NAME";
		// String => column name
		Col[2] = "DATA_TYPE";
		// int => SQL data type from java.sql.Types
		Col[3] = "TYPE_NAME";
		// String => Data source-dependent type name
		Col[4] = "COLUMN_SIZE";
		// int => precision
		Col[5] = "BUFFER_LENGTH";
		// int => length of column value in bytes
		Col[6] = "DECIMAL_DIGITS";
		// short => scale - Null is returned for data types where DECIMAL_DIGITS
		// is not applicable.
		Col[7] = "PSEUDO_COLUMN";
		// short => whether this is pseudo column like an Oracle ROWID
		return new DMDResultSet(new Object[0][8], Col,
				  DMDResultSet.DMDResultSetType.getVersionColumns);
		// TODO we might need it
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean insertsAreDetected(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * Retrieves whether a catalog appears at the start of a fully qualified table name. If not, the catalog appears at the end.
	 * <h1>Implementation Details:</h1><br>
	 * <p/>
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean isCatalogAtStart() throws SQLException
	{
		logger.debug("Function call isCatalogAtStart(), return is true ");
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean isReadOnly() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not implemented
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean locatorsUpdateCopy() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException
	{
		return true; // TODO test it!
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean nullsAreSortedAtStart() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean nullsAreSortedHigh() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean nullsAreSortedLow() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean othersDeletesAreVisible(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean othersInsertsAreVisible(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean othersUpdatesAreVisible(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean ownDeletesAreVisible(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean ownInsertsAreVisible(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean ownUpdatesAreVisible(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * It doesn't support the ANSI92
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * It does not support the ANSI92
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean supportsANSI92FullSQL() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * It doesn't supports ANSI92
	 * </p>
	 *
	 * @return false
	 */
	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsBatchUpdates() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsColumnAliasing() throws SQLException
	{
		return true;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsConvert() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsConvert(int fromType, int toType)
			  throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException
	{
		// http://publib.boulder.ibm.com/infocenter/iseries/v5r3/index.jsp?topic=%2Fsqlp%2Frbafyexsub1.htm
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			  throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsDataManipulationTransactionsOnly()
			  throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException
	{
		return true;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsFullOuterJoins() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsGroupBy() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsGroupByUnrelated() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsLikeEscapeClause() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException
	{
		return true;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsMultipleOpenResults() throws SQLException
	{
		return multipleOpenResultsSupported; // In the current version.
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsMultipleResultSets() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsMultipleTransactions() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsNamedParameters() throws SQLException
	{
		return false; // Bigquery doesn't support stored jobs.
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsNonNullableColumns() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsOrderByUnrelated() throws SQLException
	{
		return false; // Tested
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsOuterJoins() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsPositionedDelete() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsPositionedUpdate() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsResultSetConcurrency(int type, int concurrency)
			  throws SQLException
	{
		if (ResultSet.TYPE_FORWARD_ONLY == type)
		{
			if (ResultSet.CONCUR_READ_ONLY == concurrency)
			{
				return true;
			}
			if (ResultSet.CONCUR_UPDATABLE == concurrency)
			{
				return false;
			}
		}

		if (ResultSet.TYPE_SCROLL_INSENSITIVE == type)
		{
			if (ResultSet.CONCUR_READ_ONLY == concurrency)
			{
				return true;
			}
			if (ResultSet.CONCUR_UPDATABLE == concurrency)
			{
				return false;
			}
		}

		if (ResultSet.TYPE_SCROLL_SENSITIVE == type)
		{
			if (ResultSet.CONCUR_READ_ONLY == concurrency)
			{
				return true;
			}
			if (ResultSet.CONCUR_UPDATABLE == concurrency)
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsResultSetHoldability(int holdability)
			  throws SQLException
	{
		return false; // Only read only functions atm.

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Our resultset is scroll insensitive
	 * </p>
	 *
	 * @return TYPE_SCROLL_INSENSITIVE
	 */
	@Override
	public boolean supportsResultSetType(int type) throws SQLException
	{
		if (type == java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE) return true;
		if (type == ResultSet.TYPE_FORWARD_ONLY) return true;
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSavepoints() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSelectForUpdate() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsStatementPooling() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsStoredProcedures() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSubqueriesInExists() throws SQLException
	{
		return false;

	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * expr IN(expr1, expr2, ...) Returns true if expr matches expr1, expr2,<br>
	 * or any value in the parentheses. The IN keyword is an efficient<br>
	 * shorthand for (expr = expr1 || expr = expr2 || ...). The expressions<br>
	 * used with the IN keyword must be constants and they must match the<br>
	 * data type of expr.<br>
	 * <br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSubqueriesInIns() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsTableCorrelationNames() throws SQLException
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsTransactionIsolationLevel(int level)
			  throws SQLException
	{
		if (java.sql.Connection.TRANSACTION_NONE == level)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsTransactions() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns true
	 * </p>
	 */
	@Override
	public boolean supportsUnion() throws SQLException
	{
		return true;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean supportsUnionAll() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * No wrappers in the current version
	 * </p>
	 *
	 * @throws SQLException
	 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		throw new SQLException("no object found that implements "
				  + iface.toString());
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean updatesAreDetected(int type) throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean usesLocalFilePerTable() throws SQLException
	{
		return false;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Returns false
	 * </p>
	 */
	@Override
	public boolean usesLocalFiles() throws SQLException
	{
		return false;
	}

	//------------------------- for Jdk1.7 -----------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean generatedKeyAlwaysReturned() throws SQLException
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException
	{
		return null;
	}

}
