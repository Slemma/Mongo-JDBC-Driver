package com.slemma.jdbc;

import com.slemma.jdbc.query.MongoResult;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MongoScrollableResultSet extends BaseResultSet implements
		  java.sql.ResultSet
{
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MongoScrollableResultSet.class.getName());

	/**
	 * to set the maxFieldSize
	 */
	private int maxFieldSize = 0;

//	private MongoResult mongoResult = null;
	/**
	 * This Reference is for storing the Reference for the Statement which
	 * created this Resultset
	 */
	private MongoStatement Statementreference = null;

	public MongoScrollableResultSet(MongoResult mongoResult,
										  MongoPreparedStatement mongoPreparedStatement) throws SQLException
	{
		super(mongoResult, mongoPreparedStatement);
		try {
			if (mongoPreparedStatement != null)
				maxFieldSize = mongoPreparedStatement.getMaxFieldSize();
		} catch (SQLException e) {
			// Should not happen.
		}
		logger.debug("Created Scrollable resultset TYPE_SCROLL_INSENSITIVE");
	}

	/**
	 * Constructor of MongoResultset, that initializes all private variables
	 *
	 * @param mongoResult
	 * @param mongoStatementRoot
	 */
	public MongoScrollableResultSet(MongoResult mongoResult,
											  AbstractMongoStatement mongoStatementRoot) throws SQLException
	{
		super(mongoResult, mongoStatementRoot);
		logger.debug("Created Scrollable resultset TYPE_SCROLL_INSENSITIVE");
		this.mongoResult = mongoResult;

		try {
			maxFieldSize = mongoStatementRoot.getMaxFieldSize();
		} catch (SQLException e) {
			// Should not happen.
		}
	}


	/** {@inheritDoc} */
	@Override
	public boolean absolute(int row) throws SQLException
	{
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		throwIfClosed();
		if (this.RowsofResult == null) {
			return false;
		}
		if (row > 0) {
			if (row <= this.RowsofResult.length) {
				this.Cursor = row - 1;
				return true;
			}
			else {
				// An attempt to position the cursor beyond the first/last row
				// in the result set leaves the cursor before the first row or
				// after the last row.
				this.Cursor = this.RowsofResult.length;
				// false if the cursor is before the first row or after the last
				// row
				return false;
			}
		}
		// If the given row number is negative, the cursor moves to an absolute
		// row position with respect to the end of the result set.
		else
		if (row < 0) {
			if (Math.abs(row) <= this.RowsofResult.length) {
				this.Cursor = this.RowsofResult.length + row;
				return true;
			}
			else {
				// An attempt to position the cursor beyond the first/last
				// row
				// in the result set leaves the cursor before the first row
				// or
				// after the last row.
				this.Cursor = -1;
				// false if the cursor is before the first row or after the
				// last
				// row
				return false;
			}
		}
		// if 0
		else {
                /*
                 * //Check if cursor is before first of after last row if
                 * (this.Cursor == RowsofResult.size() || this.Cursor == -1)
                 * return
                 * false; else return true;
                 */
			if (this.Cursor == this.RowsofResult.length
					  || this.Cursor == -1) {
				return false;
			}
			else {
				this.Cursor = -1;
			}
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void afterLast() throws SQLException {
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		throwIfClosed();
		if (this.RowsofResult == null) {
			return;
		}
		if (this.RowsofResult.length > 0) {
			this.Cursor = this.RowsofResult.length;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void beforeFirst() throws SQLException {
		throwIfClosed();
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		if (this.RowsofResult == null) {
			return;
		}
		if (this.RowsofResult.length > 0) {
			this.Cursor = -1;
		}
	}


	/** {@inheritDoc} */
	@Override
	public boolean first() throws SQLException {
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		throwIfClosed();
		if (this.RowsofResult == null || this.RowsofResult.length == 0) {
			return false;
		}
		else {
			this.Cursor = 0;
			return true;
		}
	}


	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Always returns ResultSet.TYPE_SCROLL_INSENSITIVE
	 * </p>
	 *
	 * @return ResultSet.TYPE_SCROLL_INSENSITIVE
	 */
	@Override
	public int getType() throws SQLException {
		throwIfClosed();
		return ResultSet.TYPE_SCROLL_INSENSITIVE;
	}


	/** {@inheritDoc} */
	@Override
	public boolean isFirst() throws SQLException {
		throwIfClosed();
		if (this.Cursor == 0 && this.RowsofResult != null
				  && this.RowsofResult.length != 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLast() throws SQLException {
		throwIfClosed();
		if (this.RowsofResult != null
				  && this.Cursor == this.RowsofResult.length - 1
				  && this.RowsofResult.length - 1 >= 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean last() throws SQLException {
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		throwIfClosed();
		if (this.RowsofResult == null || this.RowsofResult.length == 0) {
			return false;
		}
		else {
			this.Cursor = this.RowsofResult.length - 1;
			return true;
		}
	}


	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not supported.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void moveToCurrentRow() throws SQLException {
		throw new UnsupportedOperationException("moveToCurrentRow()");
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Not supported.
	 * </p>
	 *
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException("moveToInsertRow()");
	}

	/** {@inheritDoc} */
	@Override
	public boolean next() throws SQLException {
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		throwIfClosed();
		if (this.RowsofResult == null) {
			return false;
		}
		if (this.RowsofResult.length > this.Cursor + 1) {
			this.Cursor++;
			return true;
		}
		else {
			this.Cursor = this.RowsofResult.length;
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean previous() throws SQLException {
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		throwIfClosed();
		if (this.RowsofResult == null) {
			return false;
		}
		if (this.Cursor > 0) {
			this.Cursor--;
			return true;
		}
		else {
			this.Cursor = -1;
			return false;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean relative(int rows) throws SQLException {
		if (this.getType() == ResultSet.TYPE_FORWARD_ONLY) {
			throw new SQLException(
					  "The Type of the Resultset is TYPE_FORWARD_ONLY");
		}
		throwIfClosed();
		if (this.RowsofResult == null) {
			return false;
		}
		if (rows == 0) {
			if (this.RowsofResult.length != 0
					  && this.Cursor < this.RowsofResult.length
					  && this.Cursor > -1) {
				return true;
			}
			else {
				return false;
			}
		}
		else
		if (rows < 0) {
			if (this.Cursor + rows < 0) {
				this.Cursor = -1;
				return false;
			}
			else {
				this.Cursor = this.Cursor + rows;
				return true;
			}
		}
		else
		if (rows + this.Cursor > (this.RowsofResult.length - 1)) {
			this.Cursor = this.RowsofResult.length;
			return false;
		}
		else {
			this.Cursor += rows;
			return true;
		}
	}

	/**
	 * @throws SQLException
	 *             if Cursor is not in a valid Position
	 */
	public void ThrowIfCursorNotValidException() throws SQLException {
		if (this.RowsofResult == null || this.RowsofResult.length == 0) {
			throw new SQLException("There are no rows in this Resultset"
					  + String.valueOf(this.Cursor) + "RowsofResult.length"
					  + String.valueOf(this.RowsofResult.length));
		}
		else
		if (this.Cursor >= this.RowsofResult.length || this.Cursor <= -1) {
			throw new SQLException(
					  "Cursor is not in a valid Position. Cursor Position is:"
								 + String.valueOf(this.Cursor)
								 + "RowsofResult.length"
								 + String.valueOf(this.RowsofResult.length));
		}
	}


	/** {@inheritDoc} */
	@Override
	public boolean wasNull() throws SQLException {
		return this.wasnull;
	}
}
