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

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.apache.commons.lang3.StringUtils;
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
public class MongoDriver implements java.sql.Driver
{
	private final static Logger logger = LoggerFactory.getLogger(MongoDriver.class.getName());

	private static Driver registeredDriver;
	/**
	 * Url prefix for using this driver
	 */
	private static final String PREFIX = "jdbc:mongodb:mql:";
	private static final String PROTOCOL = "mongodb";
	private static final String SUB_PROTOCOL = "mql";
	/**
	 * MAJOR version of the driver
	 */
	private static final int MAJOR_VERSION = 1;
	/**
	 * Minor version of the driver
	 */
	private static final int MINOR_VERSION = 0;
	/**
	 * Properties
	 **/
	private Properties props = null;

	/** Registers the driver with the drivermanager */
	static
	{
		try
		{
			register();
			Logger logger = LoggerFactory.getLogger(MongoDriver.class.getName());
			logger.debug("Registered the driver");

		}
		catch (Exception e)
		{
			throw new ExceptionInInitializerError(e);
		}

	}

	public static void register() throws SQLException
	{
		if (isRegistered())
		{
			throw new IllegalStateException(
					  "Driver is already registered. It can only be registered once.");
		}

		MongoDriver registeredDriver = new MongoDriver();
		DriverManager.registerDriver(registeredDriver);
		MongoDriver.registeredDriver = registeredDriver;

	}

	public static boolean isRegistered()
	{
		return registeredDriver != null;
	}

	public static void deregister() throws SQLException
	{
		if (!isRegistered())
		{
			throw new IllegalStateException(
					  "Driver is not registered (or it has not been registered using Driver.register() method)");
		}
		DriverManager.deregisterDriver(registeredDriver);
		registeredDriver = null;
	}

	/**
	 * Gets Major Version of the Driver as static
	 *
	 * @return Major Version of the Driver as static
	 */
	public static int getMajorVersionAsStatic()
	{
		return MongoDriver.MAJOR_VERSION;
	}

	/**
	 * Gets Minor Version of the Driver as static
	 *
	 * @return Minor Version of the Driver as static
	 */
	public static int getMinorVersionAsStatic()
	{
		return MongoDriver.MINOR_VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean acceptsURL(String url) throws SQLException
	{
		return url != null && url.toLowerCase().startsWith(PREFIX);
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * This method create a new MongoConnection and then returns it
	 * </p>
	 */
	@Override
	public Connection connect(String url, Properties info)
			  throws SQLException
	{
		if (!acceptsURL(url)) return null;

		url = url.trim();

		DriverPropertyInfo[] driverProps = this.getPropertyInfo(url, info);
		for (DriverPropertyInfo driverProp : driverProps)
		{

			if (!info.containsKey(driverProp.name))
			{
				info.setProperty(driverProp.name, driverProp.value);
			}
		}

		url = url.replace(this.PREFIX, "mongodb:");
		return new MongoConnection(url, info);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMajorVersion()
	{
		return MongoDriver.MAJOR_VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinorVersion()
	{
		return MongoDriver.MINOR_VERSION;
	}

	public static String getName()
	{
		return "com.slemma.mongo-jdbc  JDBC driver";
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Gets information about the possible properties for this driver.
	 * </p>
	 *
	 * @return a default DriverPropertyInfo
	 */
	@Override
	public java.sql.DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
	{
		Properties props = this.parseURL(url, info);

		MongoDriverProperty[] driverProperties = MongoDriverProperty.values();
		DriverPropertyInfo[] result = new DriverPropertyInfo[driverProperties.length];
		int index = 0;
		for (MongoDriverProperty p : MongoDriverProperty.values())
		{
			String propValue = props.getProperty(p.name);
			if (propValue == null)
				propValue = p.defaultValue;

			DriverPropertyInfo di = new DriverPropertyInfo(p.name, propValue);
			di.choices = p.choices;
			di.description = p.description;
			di.required = false;
			result[index++] = di;
		}

		return result;
	}

	/**
	 * <p>
	 * <h1>Implementation Details:</h1><br>
	 * Always returns false, since the driver is not jdbcCompliant
	 * </p>
	 */
	@Override
	public boolean jdbcCompliant()
	{
		return false;
	}

	/**
	 * Constructs a new DriverURL, splitting the specified URL into its
	 * component parts
	 *
	 * @param url      JDBC URL to parse
	 * @param defaults Default properties
	 * @return Properties with elements added from the url
	 * @throws java.sql.SQLException
	 */


	public static Properties parseURL(String url, Properties defaults) throws java.sql.SQLException
	{
		if (url == null)
			return null;

		Properties urlProps = new Properties(defaults);

		url = url.replace(PREFIX, "mongodb:");
		MongoClientURI mongoURI = new MongoClientURI(url, MongoClientOptions.builder());

		urlProps.setProperty(MongoDriverProperty.HOSTS.name, StringUtils.join(mongoURI.getHosts(), ","));
		urlProps.setProperty(MongoDriverProperty.DATABASE.name, mongoURI.getDatabase());
		urlProps.setProperty(MongoDriverProperty.USER.name, mongoURI.getUsername());
		urlProps.setProperty(MongoDriverProperty.PASSWORD.name, String.valueOf(mongoURI.getPassword()));

		return urlProps;
	}

	//------------------------- for Jdk1.7 -----------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
	{
		return null;
	}
}