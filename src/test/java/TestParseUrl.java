import com.slemma.jdbc.MongoConnection;
import com.slemma.jdbc.MongoDriver;
import com.slemma.jdbc.MongoDriverProperty;
import com.slemma.jdbc.MongoSQLException;
import com.slemma.jdbc.query.MongoQuery;
import com.slemma.jdbc.query.MongoQueryParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Igor Shestakov.
 */


public class TestParseUrl
{
	private final static Logger logger = LoggerFactory.getLogger(TestMixedQuery.class.getName());

	@Test
	public void parseURL1() {
		try
		{
			Properties properties = MongoDriver.parseURL("jdbc:mongodb:mql://admin:Qwerty123@127.0.0.1:27017/admin?&authMechanism=SCRAM-SHA-1", null);
			assertEquals(4, properties.size());
		}
		catch (SQLException e)
		{
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testDriverProperty1() {
		try
		{
			int defaultBatchSize = MongoDriverProperty.BATCH_SIZE.getInt(new Properties());
			assertEquals(1000, defaultBatchSize);
		}
		catch (SQLException e)
		{
			Assert.fail(e.getMessage());
		}
	}


}
