/**
 * MongoDb JDBC Driver
 * Copyright (C) 2016, Slemma.
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * MongoDriver - This class implements the java.sql.Driver interface
 * <p/>
 * The driver URL is:
 * <p/>
 * Url format: jdbc:mongodb:mql://<MONGO URI without prefix>
 * <p/>
 * <p/>
 * Any Java program can use this driver for JDBC purpose by specifying this URL
 * format.
 * </p>
 */

package com.slemma.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This Class implements the java.sql.Driver interface
 *
 * @author Igor Shestakov
 */
public enum MongoDriverProperty
{
	//Driver parameters
	HOSTS("hosts", null, "Hosts", true, null),
//	PORT("port", "27017", "Port", true, null),
	DATABASE("database", null, "Database name", true, null),
	USER("user", null, "Username to authenticate as", false, null),
	PASSWORD("password", null, "Password to use for authentication", false, null),
	BATCH_SIZE("batch_size", "1000", "Batch size", false, null),
	SERVER_SELECTION_TIMEOUT_MS("serverSelectionTimeoutMS", null, "serverSelectionTimeoutMS=ms: How long the driver will wait for server selection to succeed before throwing an exception", false, null),
	LOCAL_THRESHOLD_MS("localThresholdMS", null, "localThresholdMS=ms: When choosing among multiple MongoDB servers to send a request, the driver will only send that request to a server whose ping time is less than or equal to the server with the fastest ping time plus the local threshold", false, null),
	HEARTBEAT_FREQUENCY_MS("heartbeatFrequencyMS", null, "heartbeatFrequencyMS=ms: The frequency that the driver will attempt to determine the current state of each server in the cluster", false, null),
	REPLICA_SET("replicaSet", null, "replicaSet=name: Implies that the hosts given are a seed list, and the driver will attempt to find all members of the set", false, null),
	INVALID_HOSTNAME_ALLOWED("sslInvalidHostNameAllowed", null, "sslInvalidHostNameAllowed=true|false: Whether to allow invalid host names for SSL connections", false, new String[]{"true","false"}),
	CONNECT_TIMEOUT_MS("connectTimeoutMS", null, "connectTimeoutMS=ms: How long a connection can take to be opened before timing out", false, null),
	SOCKET_TIMEOUT_MS("socketTimeoutMS", null, "socketTimeoutMS=ms: How long a send or receive on a socket can take before timing out", false, null);

	public final String name;
	public final String defaultValue;
	public final boolean required;
	public final String[] choices;
	public final String description;

	private MongoDriverProperty(String name, String value)
	{
		this(name, value, null, false, null);
	}

	private MongoDriverProperty(String name, String defaultValue, String description, boolean required, String[] choices)
	{
		this.name = name;
		this.defaultValue = defaultValue;
		this.description = description;
		this.required = required;
		this.choices = choices;
	}

	public final String getName()
	{
		return name;
	}

	/**
	 * Returns the value of the connection parameters according to the given {@code Properties} or the
	 * default value
	 *
	 * @param properties properties to take actual value from
	 * @return evaluated value for this connection parameter
	 */
	public String get(Properties properties)
	{
		return properties.getProperty(name, defaultValue);
	}

	/**
	 * Set the value for this connection parameter in the given {@code Properties}
	 *
	 * @param properties properties in which the value should be set
	 * @param value      value for this connection parameter
	 */
	public void set(Properties properties, String value)
	{
		if (value == null)
		{
			properties.remove(name);
		}
		else
		{
			properties.setProperty(name, value);
		}
	}

	/**
	 * Return the boolean value for this connection parameter in the given {@code Properties}
	 *
	 * @param properties properties to take actual value from
	 * @return evaluated value for this connection parameter converted to boolean
	 */
	public boolean getBoolean(Properties properties)
	{
		return Boolean.valueOf(get(properties));
	}

	/**
	 * Return the int value for this connection parameter in the given {@code Properties}.
	 * Prefer the use of {@link #getInt(Properties)} anywhere you can throw an {@link SQLException}
	 *
	 * @param properties properties to take actual value from
	 * @return evaluated value for this connection parameter converted to int
	 * @throws NumberFormatException if it cannot be converted to int.
	 */
	public int getIntNoCheck(Properties properties)
	{
		String value = get(properties);
		return Integer.parseInt(value);
	}

	/**
	 * Return the int value for this connection parameter in the given {@code Properties}
	 *
	 * @param properties properties to take actual value from
	 * @return evaluated value for this connection parameter converted to int
	 * @throws MongoSQLException if it cannot be converted to int.
	 */
	public int getInt(Properties properties) throws MongoSQLException
	{
		String value = get(properties);
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			throw new MongoSQLException(
					  String.format("%s parameter value must be an integer but was: %s", getName(), value)
			);
		}
	}

	/**
	 * Return the {@code Integer} value for this connection parameter in the given {@code Properties}
	 *
	 * @param properties properties to take actual value from
	 * @return evaluated value for this connection parameter converted to Integer or null
	 * @throws MongoSQLException
	 */
	public Integer getInteger(Properties properties) throws MongoSQLException
	{
		String value = get(properties);
		if (value == null)
		{
			return null;
		}
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException nfe)
		{
			throw new MongoSQLException(
					  String.format("%s parameter value must be an integer but was: %s", getName(), value)
			);
		}
	}

	/**
	 * Set the boolean value for this connection parameter in the given {@code Properties}
	 *
	 * @param properties properties in which the value should be set
	 * @param value      boolean value for this connection parameter
	 */
	public void set(Properties properties, boolean value)
	{
		properties.setProperty(name, Boolean.toString(value));
	}

	/**
	 * Set the int value for this connection parameter in the given {@code Properties}
	 *
	 * @param properties properties in which the value should be set
	 * @param value      int value for this connection parameter
	 */
	public void set(Properties properties, int value)
	{
		properties.setProperty(name, Integer.toString(value));
	}

	/**
	 * Test whether this property is present in the given {@code Properties}
	 *
	 * @return true if the parameter is specified in the given properties
	 */
	public boolean isPresent(Properties properties)
	{
		return get(properties) != null;
	}
}