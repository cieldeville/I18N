/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Helper class which provides mappings from ISO 639 language codes to native language names
 * based on a property file to be put in classpath (loaded automatically).
 *
 * @author BlackyPaw
 * @version 1.0
 */
public final class ISO639 {

	private static final Map<String, String> englishNames = new HashMap<>();
	private static final Map<String, String> nativeNames = new HashMap<>();

	/**
	 * Gets the (english) name of the language which corresponds to the given ISO639 language code.
	 *
	 * @param isoCode The ISO639 language code of the language
	 *
	 * @return The language's (english) name or null if the language was not found
	 */
	public static String getName( String isoCode ) {
		return englishNames.get( isoCode );
	}

	/**
	 * Gets the native name of the language which corresponds to the given ISO639 language code.
	 *
	 * @param isoCode The ISO639 language code of the language
	 *
	 * @return The language's native name or null if the language was not found
	 */
	public static String getNativeName( String isoCode ) {
		return nativeNames.get( isoCode );
	}

	private ISO639() {
		throw new AssertionError( "Cannot instantiate ISO639!" );
	}

	static {
		try {
			try ( BufferedInputStream in = new BufferedInputStream( ISO639.class.getResourceAsStream( "/mappings/iso639_native.properties" ) ) ) {
				Properties properties = new Properties();
				properties.load( in );

				Enumeration propertyNames = properties.propertyNames();
				while ( propertyNames.hasMoreElements() ) {
					String propertyName = (String) propertyNames.nextElement();
					nativeNames.put( propertyName, properties.getProperty( propertyName ) );
				}
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		try {
			try ( BufferedInputStream in = new BufferedInputStream( ISO639.class.getResourceAsStream( "/mappings/iso639_english.properties" ) ) ) {
				Properties properties = new Properties();
				properties.load( in );

				Enumeration propertyNames = properties.propertyNames();
				while ( propertyNames.hasMoreElements() ) {
					String propertyName = (String) propertyNames.nextElement();
					englishNames.put( propertyName, properties.getProperty( propertyName ) );
				}
			}
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

}
