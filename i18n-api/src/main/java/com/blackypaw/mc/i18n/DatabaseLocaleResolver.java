/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.UUID;

/**
 * LocaleResolver implementation which will query a MySQL database for player locales and optionally save
 * them back there if necessary. This implementation will be chosen if the DATABASE locale resolver has
 * been configured in the plugin's configuration. Note, that this class is not very performant regarding
 * queries and should thus not be used if a large number of queries is to be expected.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class DatabaseLocaleResolver implements LocaleResolver<UUID>, AutoCloseable {

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL      = "jdbc:mysql://%s:%d/%s";
	private static final String LOCALE_TABLE_NAME = "player_locales";

	private final Connection connection;
	private final String dbPrefix;
	private final String localeTable;
	
	private final I18N i18n;

	DatabaseLocaleResolver( I18N i18n, String dbHost, int dbPort, String dbUser, String dbPassword, String dbName, String dbPrefix ) throws SQLException {
		try {
			this.i18n = i18n;
			this.dbPrefix = dbPrefix;
			this.localeTable = this.dbPrefix + LOCALE_TABLE_NAME;

			// Load driver class:
			Class.forName( JDBC_DRIVER );

			// Open connection to database
			this.connection = DriverManager.getConnection( String.format( DB_URL, dbHost, dbPort, dbName ), dbUser, dbPassword );

			// Create required tables:
			try {
				this.createTables();
			} catch ( SQLException e ) {
				// Got to close the connection - memory leaks:
				try {
					this.connection.close();
				} catch ( SQLException ignored ) {
					// ._.
				}

				throw e;
			}
		} catch ( ClassNotFoundException e ) {
			throw new SQLException( "Failed to load MySQL driver class", e );
		}
	}

	@Override
	public Locale resolveLocale( UUID key ) {
		Locale locale;
		try {
			try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM `" + this.localeTable + "` WHERE `uuid`=? LIMIT 1;" ) ) {
				statement.setString( 1, key.toString() );
				try ( ResultSet result = statement.executeQuery() ) {
					if ( result.next() ) {
						// Found a result!
						locale = new Locale( result.getString( "locale" ) );
					} else {
						// No result found - insert fallback locale
						locale = this.i18n.getFallbackLocale();
						this.insertPlayerLocale( key, locale );
					}
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			return this.i18n.getFallbackLocale();
		}

		return locale;
	}

	@Override
	public boolean trySetPlayerLocale( UUID key, Locale locale ) {
		try {
			try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM `" + this.localeTable + "` WHERE `uuid`=? LIMIT 1;" ) ) {
				statement.setString( 1, key.toString() );
				try ( ResultSet result = statement.executeQuery() ) {
					if ( result.next() ) {
						// Row does already exist:
						int id = result.getInt( 1 );

						// Update:
						try ( PreparedStatement update = this.connection.prepareStatement( "UPDATE `" + this.localeTable + "` SET `locale`=? WHERE `id`=? LIMIT 1;" ) ) {
							update.setString( 1, locale.getLanguage() );
							update.setInt( 2, id );
							update.executeUpdate();
						}
					} else {
						// No duplicate row found - insert locale
						this.insertPlayerLocale( key, locale );
					}
				}
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Will be invoked if the plugin gets disabled. Closes the underlying MySQL connection.
	 */
	public void close() {
		try {
			this.connection.close();
		} catch ( SQLException ignored ) {
			// If we cannot close this connection we cannot do anything else about it
		}
	}

	/**
	 * Creates all required tables if they do not yet exist.
	 */
	private void createTables() throws SQLException {
		try ( Statement statement = this.connection.createStatement() ) {
			statement.executeUpdate( "CREATE TABLE IF NOT EXISTS `" + this.localeTable + "` (" +
			                         "`id` int(11) NOT NULL AUTO_INCREMENT," +
			                         "`uuid` VARCHAR(36) NOT NULL," +
			                         "`locale` VARCHAR(36) NOT NULL," +
			                         "PRIMARY KEY(`id`)" +
			                         ") ENGINE=InnoDB CHARACTER SET=utf8;" );
		}
	}

	/**
	 * Attempts to insert a new row into the locales table.
	 *
	 * @param key The key to which the given locale should be stored
	 * @param locale The locale to be stored
	 *
	 * @throws SQLException Thrown in case the underlying MySQL query failed
	 */
	private void insertPlayerLocale( UUID key, Locale locale ) throws SQLException {
		try ( PreparedStatement insert = this.connection.prepareStatement( "INSERT INTO `" + this.localeTable + "` (`uuid`, `locale`) VALUES(?, ?);" ) ) {
			locale = this.i18n.getFallbackLocale();
			insert.setString( 1, key.toString() );
			insert.setString( 2, locale.getLanguage() );
			insert.executeUpdate();
		}
	}

}
