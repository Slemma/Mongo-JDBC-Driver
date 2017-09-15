import com.slemma.jdbc.MongoConnection;
import com.slemma.jdbc.MongoDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Igor Shestakov.
 */
public class TestMetadata
{

	private static java.sql.Connection con = null;
	private static Driver driver = null;
	private static ResultSet Result = null;

	private final static Logger logger = LoggerFactory.getLogger(TestMetadata.class.getName());

	public void QueryLoad() {
		final String query =
				  "{" +
				  "\"aggregate\":\"zips\"" +
				  ", \"pipeline\":[" +
				  "{ \"$group\": { \"_id\": \"$state\", \"totalPop\": { \"$sum\": \"$pop\" } } },\n" +
				  "{ \"$match\": { \"totalPop\": { \"$gte\": 10000000 } } }" +
				  "]" +
				  "}"
				  ;
		this.logger.info("Running query:" + query);

		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setQueryTimeout(500);
			this.Result = stmt.executeQuery(query);
		}
		catch (SQLException e) {
			this.logger.error("SQL exception" + e.toString());
			Assert.fail("SQL exception" + e.toString());
		}
		Assert.assertNotNull(this.Result);
	}

	@Before
	public void checkConnection() {

		try {
			if (this.con == null || !this.con.isValid(0))
			{
				this.logger.info("Testing the JDBC driver");
				try {
					Class.forName("com.slemma.jdbc.MongoDriver");

//					String jdbcUrl = "jdbc:mongodb:mql://127.0.0.1:27017/test";
//					String jdbcUrl = "jdbc:mongodb:mql://test:test@127.0.0.1:27017/test?&authMechanism=SCRAM-SHA-1";
					String jdbcUrl = "jdbc:mongodb:mql://admin:Qwerty123@127.0.0.1:27017/admin?&authMechanism=SCRAM-SHA-1";
					this.con = DriverManager.getConnection(jdbcUrl);
					this.driver = DriverManager.getDriver(jdbcUrl);
				}
				catch (Exception e) {
					e.printStackTrace();
					this.logger.error("Error in connection" + e.toString());
					Assert.fail("General Exception:" + e.toString());
				}
				this.logger.info(((MongoConnection) this.con).getUrl());
			}
		}
		catch (SQLException e) {
			logger.debug("Oops something went wrong",e);
		}
		QueryLoad();
	}

	@Test
	public void checkDriverName() {
		assertEquals(MongoDriver.getName(), "com.slemma.mongo-jdbc  JDBC driver");
	}

	@Test
	public void printResultSet() {
		Utils.printResultSet(this.Result);
	}

	@Test
	public void connectionGetCatalog() {
		try {
			assertEquals(this.con.getCatalog(), "admin");
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

	@Test
	public void metadataGetMetadata() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			assertNotNull(metadata);
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

	@Test
	public void metadataGetURL() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			assertNotNull(metadata);
			String url = metadata.getURL();
			assertNotNull(url);
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

	@Test
	public void metadata() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			assertNotNull(metadata);
			System.out.println(metadata.getDatabaseProductVersion());
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

	@Test
	public void metadataGetUserName() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			assertNotNull(metadata);
			String userName = metadata.getUserName();
			assertNotNull(userName);
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

	@Test
	public void metadataGetCatalogSeparator() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			assertEquals(".", metadata.getCatalogSeparator());
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

	@Test
	public void metadataGetCatalogTerm() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			assertEquals("database", metadata.getCatalogTerm());
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

//	@Test
//	public void metadataGetCatalogs() {
//		try {
//			DatabaseMetaData metadata = this.con.getMetaData();
//			ResultSet catalogs = metadata.getCatalogs();
//			assertNotNull(catalogs);
//			while (catalogs.next()){
//				System.out.println(catalogs.getString(1));
//			}
//		}
//		catch (SQLException e) {
//			this.logger.error("SQL exception: " + e.toString());
//		}
//	}

	@Test
	public void metadataGetTables() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			ResultSet tables = metadata.getTables("test", null, null, null);
			assertNotNull(tables);
			Utils.printResultSet(tables);
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}


	@Test
	public void metadataGetColumnsForTable() {
		try {
			DatabaseMetaData metadata = this.con.getMetaData();
			String catalog = "test";
			String table = "zips";
			ResultSet columnsMetaDs = metadata.getColumns(catalog, null, table, null);

			assertNotNull(columnsMetaDs);

			if (columnsMetaDs.next()){
				Assert.assertEquals("_id",columnsMetaDs.getString("COLUMN_NAM"));
				Assert.assertEquals("VARCHAR",columnsMetaDs.getString("TYPE_NAME"));
			}
			else {
				Assert.fail("Incorrect resultSet");
			}
			if (columnsMetaDs.next()){
				Assert.assertEquals("city",columnsMetaDs.getString("COLUMN_NAM"));
				Assert.assertEquals("VARCHAR",columnsMetaDs.getString("TYPE_NAME"));
			}
			else {
				Assert.fail("Incorrect resultSet");
			}
			if (columnsMetaDs.next()){
				Assert.assertEquals("pop",columnsMetaDs.getString("COLUMN_NAM"));
				Assert.assertEquals("INTEGER",columnsMetaDs.getString("TYPE_NAME"));
			}
			else {
				Assert.fail("Incorrect resultSet");
			}
			if (columnsMetaDs.next()){
				Assert.assertEquals("state",columnsMetaDs.getString("COLUMN_NAM"));
				Assert.assertEquals("VARCHAR",columnsMetaDs.getString("TYPE_NAME"));
			}
			else {
				Assert.fail("Incorrect resultSet");
			}

			Utils.printResultSet(columnsMetaDs);
		}
		catch (SQLException e) {
			this.logger.error("SQL exception: " + e.toString());
		}
	}

	@Test
	public void metadataGetDriverProperties() {
		try
		{
			DriverPropertyInfo[] driverPropertyInfos = this.driver.getPropertyInfo("jdbc:mongodb:mql://admin:Qwerty123@127.0.0.1:27017/admin?&authMechanism=SCRAM-SHA-1", null);
			assertEquals(5, driverPropertyInfos.length);
			assertEquals("hosts", driverPropertyInfos[0].name);
			assertEquals("127.0.0.1:27017", driverPropertyInfos[0].value);
			assertEquals("database", driverPropertyInfos[1].name);
			assertEquals("admin", driverPropertyInfos[1].value);
			assertEquals("user", driverPropertyInfos[2].name);
			assertEquals("admin", driverPropertyInfos[2].value);
			assertEquals("password", driverPropertyInfos[3].name);
			assertEquals("Qwerty123", driverPropertyInfos[3].value);
			assertEquals("batch_size", driverPropertyInfos[4].name);
			assertEquals("1000", driverPropertyInfos[4].value);
		}
		catch (SQLException e)
		{
			Assert.fail("Exception: " + e.toString());
		}
	}
}
