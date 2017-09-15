import com.slemma.jdbc.MongoConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author igorshestakov.
 */
public class TestPredictor
{
	private static java.sql.Connection con = null;
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
					this.con = DriverManager.getConnection("jdbc:mongodb:mql://admin:Qwerty123@127.0.0.1:27017/admin?&authMechanism=SCRAM-SHA-1");
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
	public void testFieldsPredictionWithNullsInFirstRows()
	{
		ResultSet rs;
		String query = "{ \"find\" : \"bios2\"}";
		try
		{
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			ResultSetMetaData rsMetadata = rs.getMetaData();
			Assert.assertEquals(13, rsMetadata.getColumnCount());

			Utils.printResultSet(rs);
		}
		catch (SQLException e)
		{
			this.logger.error("Exception: " + e.toString());
			Assert.fail("Exception: " + e.toString());
		}
		Assert.assertTrue(true);
	}

	@Test
	public void testFieldsPredictionOrder()
	{
		ResultSet rs;
		String query = "{ \"find\" : \"bios2\"}";
		try
		{
			Statement stmt = this.con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			Assert.assertNotNull(rs);

			ResultSetMetaData rsMetadata = rs.getMetaData();

			Assert.assertEquals("_id", rsMetadata.getColumnName(1));
			Assert.assertEquals("name.first", rsMetadata.getColumnName(2));
			Assert.assertEquals("name.aka", rsMetadata.getColumnName(3));
			Assert.assertEquals("name.last", rsMetadata.getColumnName(4));
			Assert.assertEquals("iso", rsMetadata.getColumnName(5));
			Assert.assertEquals("longitude", rsMetadata.getColumnName(6));
			Assert.assertEquals("latitude", rsMetadata.getColumnName(7));
			Assert.assertEquals("longitudeString", rsMetadata.getColumnName(8));
			Assert.assertEquals("latitudeString", rsMetadata.getColumnName(9));
			Assert.assertEquals("title", rsMetadata.getColumnName(10));
			Assert.assertEquals("birth", rsMetadata.getColumnName(11));
			Assert.assertEquals("death", rsMetadata.getColumnName(12));
			Assert.assertEquals("popularity", rsMetadata.getColumnName(13));

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
