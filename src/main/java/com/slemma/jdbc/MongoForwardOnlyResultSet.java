package com.slemma.jdbc;

import com.slemma.jdbc.query.MongoResult;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Igor Shestakov.
 */
public class MongoForwardOnlyResultSet extends BaseResultSet implements java.sql.ResultSet
{
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoForwardOnlyResultSet.class.getName());

	/**
	 * Constructor for the forward only resultset
	 *
	 * @param mongoResult
	 * @param mongoStatementRoot
	 * @throws SQLException - if we fail to get the results
	 */
	public MongoForwardOnlyResultSet(MongoResult mongoResult
			  , AbstractMongoStatement mongoStatementRoot) throws SQLException
	{
		super(mongoResult, mongoStatementRoot);
		logger.debug("Created forward only resultset TYPE_FORWARD_ONLY");
	}
}
