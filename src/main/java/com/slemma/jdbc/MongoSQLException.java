package com.slemma.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

/**
 * @author Igor Shestakov.
 */
public class MongoSQLException extends SQLException
{
	public MongoSQLException() {
		super();
	}

	public MongoSQLException(String reason) {
		super(reason);
	}

	public MongoSQLException(String reason, String sqlState) {
		super(reason, sqlState);
	}

	public MongoSQLException(String reason, String sqlState, int vendorCode) {
		super(reason, sqlState, vendorCode);
	}
	
	public MongoSQLException(String reason, String sqlState, int vendorCode,
								 Throwable cause) {
		super(reason, sqlState, vendorCode, cause);
	}

	public MongoSQLException(String reason, String sqlState, Throwable cause) {
		super(reason, sqlState, cause);
	}

	public MongoSQLException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public MongoSQLException(Throwable cause) {
		super(cause);
	}
	
}
