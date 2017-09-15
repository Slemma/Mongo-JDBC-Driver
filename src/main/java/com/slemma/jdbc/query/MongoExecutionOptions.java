package com.slemma.jdbc.query;

/**
 * @author Igor Shestakov.
 */
public class MongoExecutionOptions
{
	private final int batchSize;
	private final int samplingBatchSize;
	private final int maxRows;

	public MongoExecutionOptions(final int batchSize, final int samplingBatchSize, final int maxRows)
	{
		this.batchSize = batchSize;
		this.samplingBatchSize = samplingBatchSize;
		this.maxRows = maxRows;
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public int getSamplingBatchSize()
	{
		return samplingBatchSize;
	}

	public int getMaxRows()
	{
		return maxRows;
	}


	public static class MongoExecutionOptionsBuilder
	{
		private int batchSize;
		private int samplingBatchSize;
		private int maxRows;

		public MongoExecutionOptionsBuilder()
		{
		}

		public MongoExecutionOptionsBuilder batchSize(int value)
		{
			this.batchSize = value;
			return this;
		}

		public MongoExecutionOptionsBuilder samplingBatchSize(int value)
		{
			this.samplingBatchSize = value;
			return this;
		}

		public MongoExecutionOptionsBuilder maxRows(int value)
		{
			this.maxRows = value;
			return this;
		}

		public MongoExecutionOptions createOptions()
		{
			return new MongoExecutionOptions(
					  this.batchSize,
					  this.samplingBatchSize,
					  this.maxRows
			);
		}
	}
}
