import com.slemma.jdbc.MongoConnection;
import com.slemma.jdbc.MongoSQLException;
import com.slemma.jdbc.query.MongoQuery;
import com.slemma.jdbc.query.MongoQueryParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Igor Shestakov.
 */


public class TestMixedQuery
{
	private static java.sql.Connection con = null;
	private final static Logger logger = LoggerFactory.getLogger(TestMixedQuery.class.getName());

	@Before
	public void checkConnection() {
		try {
			if (this.con == null || !this.con.isValid(0))
			{
				this.logger.info("Testing the JDBC driver");
				try {
					Class.forName("com.slemma.jdbc.MongoDriver");

//					this.con = DriverManager.getConnection("jdbc:mongodb:mql://192.168.99.100:27017/test");
//					this.con = DriverManager.getConnection("jdbc:mongodb:mql://test:test@127.0.0.1:27017/test?&authMechanism=SCRAM-SHA-1");
					this.con = DriverManager.getConnection("jdbc:mongodb:mql://admin:Qwerty123@127.0.0.1:27017/admin?&authMechanism=SCRAM-SHA-1");
				}
				catch (Exception e) {
					e.printStackTrace();
					this.logger.error("Error in connection" + e.toString());
					Assert.fail("Exception:" + e.toString());
				}
				this.logger.info(((MongoConnection) this.con).getUrl());
			}
		}
		catch (SQLException e) {
			logger.debug("Oops something went wrong", e);
		}
	}


	@Test
	public void countDimensionMembersQuery() {
		String query = "select count(DISTINCT factdata_view._id) as c0 from ({\n" +
				  "\"aggregate\":\"zips\"\n" +
				  ", \"pipeline\":[\n" +
				  "  { \"$group\": { \"_id\": \"$state\", \"totalPop\": { \"$sum\": \"$pop\" } } }\n" +
				  "  ,{ \"$match\": { \"totalPop\": { \"$gte\": 10000000 } } }\n" +
				  "  ]\n" +
				  "}) as factdata_view";

		MongoQuery mixedQuery = null;
		try
		{
			mixedQuery = MongoQueryParser.parse(query);
		}
		catch (MongoSQLException e)
		{
			Assert.fail(e.getMessage());
		}

		Assert.assertNotNull(mixedQuery);
		if (mixedQuery != null) {
			Assert.assertEquals(MongoQueryParser.cleanQueryString("{\n" +
					  "\"aggregate\":\"zips\"\n" +
					  ", \"pipeline\":[\n" +
					  "  { \"$group\": { \"_id\": \"$state\", \"totalPop\": { \"$sum\": \"$pop\" } } }\n" +
					  "  ,{ \"$match\": { \"totalPop\": { \"$gte\": 10000000 } } }\n" +
					  "  ]\n" +
					  "}"), mixedQuery.getMqlQueryString());
		}
	}

	@Test
	public void getDimensionMembersQuery() {
		String query = "select factdata_view._id as c0, factdata_view._id as c1 from ({\n" +
				  "\"aggregate\":\"zips\"\n" +
				  ", \"pipeline\":[\n" +
				  "  { \"$group\": { \"_id\": \"$state\", \"totalPop\": { \"$sum\": \"$pop\" } } }\n" +
				  "  ,{ \"$match\": { \"totalPop\": { \"$gte\": 10000000 } } }\n" +
				  "  ]\n" +
				  "}) as factdata_view group by c0, c1 order by c0 ASC";

		MongoQuery mixedQuery = null;
		try
		{
			mixedQuery = MongoQueryParser.parse(query);
		}
		catch (MongoSQLException e)
		{
			Assert.fail(e.getMessage());
		}
		Assert.assertNotNull(mixedQuery);
		if (mixedQuery != null) {
			Assert.assertEquals(MongoQueryParser.cleanQueryString("{\n" +
					  "\"aggregate\":\"zips\"\n" +
					  ", \"pipeline\":[\n" +
					  "  { \"$group\": { \"_id\": \"$state\", \"totalPop\": { \"$sum\": \"$pop\" } } }\n" +
					  "  ,{ \"$match\": { \"totalPop\": { \"$gte\": 10000000 } } }\n" +
					  "  ]\n" +
					  "}"), mixedQuery.getMqlQueryString());
		}
	}

	@Test
	public void mixedQueryDistinctCount() {
		ResultSet rs;
		final String query =
				  "select count(DISTINCT factdata_view._id) as c0 from ({\n" +
							 "\"aggregate\":\"zips\"\n" +
							 ", \"pipeline\":[\n" +
							 "  { \"$group\": { \"_id\": \"$state\", \"totalPop\": { \"$sum\": \"$pop\" } } }\n" +
							 "  ,{ \"$match\": { \"totalPop\": { \"$gte\": 10000000 } } }\n" +
							 "  ]\n" +
							 "}) as factdata_view"
				  ;
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			int rowCnt = Utils.printResultSet(rs);
			Assert.assertEquals(1,rowCnt);
		}
		catch (SQLException e) {
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}

	@Test
	public void mixedQueryGetDimensionMembers() {
		ResultSet rs;
		final String query =
				  "select factdata_view._id as c0, factdata_view._id as c1 from ({\n" +
							 "\"aggregate\":\"zips\"\n" +
							 ", \"pipeline\":[\n" +
							 "  { \"$group\": { \"_id\": \"$state\", \"totalPop\": { \"$sum\": \"$pop\" } } }\n" +
							 "  ,{ \"$match\": { \"totalPop\": { \"$gte\": 10000000 } } }\n" +
							 "  ]\n" +
							 "}) as factdata_view group by c0, c1 order by c0 ASC"
				  ;
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			int rowCnt = Utils.printResultSet(rs);
			Assert.assertEquals(7,rowCnt);
		}
		catch (SQLException e) {
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}

	@Test
	public void mixedQueryGetDimensionMembers2() {
		ResultSet rs;
		final String query =
				  "select factdata_view.name.last as c0, factdata_view.name.last as c1 " +
							 "from ({\"find\":\"bios\"}) as factdata_view group by c0, c1 order by c0 ASC"
				  ;
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			int rowCnt = Utils.printResultSet(rs);
		}
		catch (SQLException e) {
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}

	@Test
	public void mixedQueryGetDimensionMembersCheckDistinct() {
		ResultSet rs;
		try {
			String query =
					  "select factdata_view.name.last as c0, factdata_view.name.last as c1 " +
								 "from ({\"find\":\"bios\"}) as factdata_view group by c0, c1 order by c0 ASC"
					  ;
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			int rowCnt = Utils.printResultSet(rs);
			Assert.assertEquals(10, rowCnt);

			query =
					  "select factdata_view.name.first as c0, factdata_view.name.first as c1 " +
								 "from ({\"find\":\"bios\"}) as factdata_view group by c0, c1 order by c0 ASC"
					  ;

			stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			rowCnt = Utils.printResultSet(rs);
			Assert.assertEquals(9, rowCnt);

		}
		catch (SQLException e) {
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}

	@Test
	public void mixedQueryGetDimensionMembers3() {
		ResultSet rs;
		final String query =
				  "select factdata_view._id as c0, factdata_view._id as c1 " +
							 "from ({\"find\":\"bios\"}) as factdata_view group by c0, c1 order by c0 ASC"
				  ;
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			ResultSetMetaData rsMetadata = rs.getMetaData();
			System.out.println("Columns metadata:");
			for (int i=1; i <= rsMetadata.getColumnCount(); i++) {
				System.out.println("Label: " + rsMetadata.getColumnLabel(i) + "; Data type: " + rsMetadata.getColumnTypeName(i));
			}

			//_id must have String type
			Assert.assertEquals(12, rsMetadata.getColumnType(1));

			int rowCnt = Utils.printResultSet(rs);
		}
		catch (SQLException e) {
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}

	@Test
	//Google maps
	public void mixedQueryGetDimensionMembers4() {
		ResultSet rs;
		final String query =
				  "select factdata_view._id as c0, factdata_view._id as c1, factdata_view.popularity as c2, factdata_view.popularity as c3  " +
							 "from ({\"find\":\"bios\"}) as factdata_view group by c0, c1, c2, c3 order by c0 ASC"
				  ;
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			ResultSetMetaData rsMetadata = rs.getMetaData();
			System.out.println("Columns metadata:");
			for (int i=1; i <= rsMetadata.getColumnCount(); i++) {
				System.out.println("Label: " + rsMetadata.getColumnLabel(i) + "; Data type: " + rsMetadata.getColumnTypeName(i));
			}

			int rowCnt = Utils.printResultSet(rs);

			//_id must have String type
			Assert.assertEquals(12, rsMetadata.getColumnType(1));

		}
		catch (SQLException e) {
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}


	@Test
	//Google maps
	public void mixedQueryGetDimensionMembers5() {
		ResultSet rs;
		final String query =
				  "select factdata_view._id.Title as c0, factdata_view._id.Title as c1, factdata_view.Latitude as c2, factdata_view.Longitude as c3 from ({\n" +
							 "    \"aggregate\": \"bios2\",\n" +
							 "    \"pipeline\": [\n" +
							 "       {\"$match\": {\n" +
							 "            \"$and\":[{\"longitude\": {\"$ne\" : \"\"}}, {\"latitude\": {\"$ne\" : \"\"}},{\"latitude\": {\"$ne\" : null}}, {\"longitude\": {\"$ne\" : null}}] \n" +
							 "            }\n" +
							 "        },         \n" +
							 "        {\"$group\": {\n" +
							 "            \"_id\": {\n" +
							 "                \"Title\": \"$name.last\",\n" +
							 "                \"LatitudeData\": \"$latitude\",\n" +
							 "                \"LongitudeData\": \"$longitude\"\n" +
							 "            },\n" +
							 "            \"cnt\": {\"$sum\": 1 }\n" +
							 "            }\n" +
							 "        },\n" +
							 "        {\"$project\": {\n" +
							 "            \"id\": 1,\n" +
							 "            \"Latitude\": \"$_id.LatitudeData\",\n" +
							 "            \"Longitude\": \"$_id.LongitudeData\",\n" +
							 "             \"cnt\": 1  \n" +
							 "            }\n" +
							 "        }\n" +
							 "     ]\n" +
							 "}) as factdata_view group by c0, c1, c2, c3 order by c0 ASC"
				  ;
		try {
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			ResultSetMetaData rsMetadata = rs.getMetaData();
			System.out.println("Columns metadata:");
			for (int i=1; i <= rsMetadata.getColumnCount(); i++) {
				System.out.println("Label: " + rsMetadata.getColumnLabel(i) + "; Data type: " + rsMetadata.getColumnTypeName(i));
			}

			int rowCnt = Utils.printResultSet(rs);

			//_id must have String type
			Assert.assertEquals(12, rsMetadata.getColumnType(1));

		}
		catch (SQLException e) {
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}
}
