/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * TranslationStorage implementation based on Java's .properties files. Supports
 * lazy loading and manual loading of translations.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class PropertyTranslationStorage extends TranslationStorageAdapter {

	private final File baseDirectory;

	/**
	 * Constructs a new property translation storage. The created instance will attempt
	 * to resolve translations given the directory all .properties files containing the
	 * locale specific translations are stored in. The files are expected to be named
	 * after the language code of the respective language, e.g. en.properties or
	 * de.properties.
	 * <p>
	 * Per default the created instance is not allowed to lazy load any translations.
	 *
	 * @param directory The directory all property files are stored in
	 */
	public PropertyTranslationStorage( File directory ) {
		super( false );
		this.baseDirectory = directory;
	}

	/**
	 * Constructs a new property translation storage. The created instance will attempt
	 * to resolve translations given the directory all .properties files containing the
	 * locale specific translations are stored in. The files are expected to be named
	 * after the language code of the respective language, e.g. en.properties or
	 * de.properties.
	 *
	 * @param directory The directory all property files are stored in
	 * @param lazyLoad Whether or not lazy loading of translations should be allowed
	 */
	public PropertyTranslationStorage( File directory, boolean lazyLoad ) {
		super( lazyLoad );
		this.baseDirectory = directory;
	}

	@Override
	public void loadLanguage( Locale locale ) throws IOException {
		// Clear any cached results:
		this.translations.remove( locale );

		try ( InputStream in = new BufferedInputStream( new FileInputStream( new File( this.baseDirectory, locale.getLanguage() + ".properties" ) ) ) ) {
			Properties properties = new Properties();
			properties.load( in );
			this.loadLanguage( locale, properties );
		}
	}

	/**
	 * Loads a language from a pre-loaded properties set.
	 *
	 * @param locale The locale of the language
	 * @param properties The properties to load all translation keys from
	 *
	 * @throws IOException Thrown in case the language could not be loaded
	 */
	public void loadLanguage( Locale locale, Properties properties ) throws IOException {
		Map<Integer, String> translation = new HashMap<>();
		Enumeration e = properties.propertyNames();
		while ( e.hasMoreElements() ) {
			String propertyName = (String) e.nextElement();
			int hash = FNVHash.hash1a32( propertyName );
			if ( translation.containsKey( hash ) ) {
				throw new IOException( "Colliding hash codes for distinct translation keys: '" + propertyName + "'" );
			}
			translation.put( hash, properties.getProperty( propertyName ) );
		}

		this.translations.put( locale, translation );
	}

}
