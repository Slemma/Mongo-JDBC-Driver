import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.slemma.jdbc.MongoConnection;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static java.util.Arrays.asList;

/**
 * @author Igor Shestakov.
 */
public class TestTz
{

	private static MongoConnection con = null;
	private final static Logger logger = LoggerFactory.getLogger(TestResultSet.class.getName());

	@Before
	public void checkConnection()
	{
		try
		{
			if (this.con == null || !this.con.isValid(0))
			{
				this.logger.info("Testing the JDBC driver");
				try
				{
					Class.forName("com.slemma.jdbc.MongoDriver");
//					this.con = DriverManager.getConnection("jdbc:mongodb:mql://192.168.99.100:27017/test");
//					this.con = DriverManager.getConnection("jdbc:mongodb:mql://test:test@127.0.0.1:27017/test?&authMechanism=SCRAM-SHA-1");
					this.con = (MongoConnection)DriverManager.getConnection("jdbc:mongodb:mql://admin:Qwerty123@127.0.0.1:27017/admin?&authMechanism=SCRAM-SHA-1");
				}
				catch (Exception e)
				{
					e.printStackTrace();
					this.logger.error("Error in connection" + e.toString());
					Assert.fail("Exception:" + e.toString());
				}
				this.logger.info(((MongoConnection) this.con).getUrl());
			}
		}
		catch (SQLException e)
		{
			logger.debug("Oops something went wrong", e);
		}
	}

	@Test
	public void findWithDateTest()
	{

		ResultSet rs;
		String query = "{ \"find\" : \"bios\"}";
		try
		{
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			ResultSetMetaData rsMetadata = rs.getMetaData();
			Assert.assertEquals(rsMetadata.getColumnTypeName(6).toUpperCase(), "TIMESTAMP");
			Assert.assertEquals(rsMetadata.getColumnTypeName(7).toUpperCase(), "TIMESTAMP");

			Assert.assertTrue(rs.next());
			Assert.assertEquals(rs.getString(6), "1924-12-03 05:00:00.000+0000");
			Assert.assertEquals(rs.getString(7), "2007-03-17 04:00:00.000+0000");

			this.con.setTimeZone("CET");
			stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertTrue(rs.next());
			Assert.assertEquals(rs.getString(6), "1924-12-03 06:00:00.000+0100");
			Assert.assertEquals(rs.getString(7), "2007-03-17 05:00:00.000+0100");

			Utils.printResultSet(rs);
		}
		catch (SQLException e)
		{
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}
}
