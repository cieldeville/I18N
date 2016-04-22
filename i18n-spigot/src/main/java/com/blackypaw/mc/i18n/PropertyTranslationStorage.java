/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import com.sun.imageio.plugins.common.I18N;

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
 * lazy loading.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class PropertyTranslationStorage extends TranslationStorage {

	private final File baseDirectory;

	private Map<Locale, Map<Integer, String>> translations;

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
		this.translations = new HashMap<>();
	}

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
	 * @param lazyLoad Whether or not lazy loading of translations should be allowed
	 */
	public PropertyTranslationStorage( File directory, boolean lazyLoad ) {
		super( lazyLoad );
		this.baseDirectory = directory;
		this.translations = new HashMap<>();
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

	@Override
	protected String getRawTranslation( Locale locale, String key ) {
		return this.getRawTranslation( locale, FNVHash.hash1a32( key ) );
	}

	@Override
	protected String getRawTranslation( Locale locale, int keyHash ) {
		Map<Integer, String> translation = this.translations.get( locale );

		if ( translation == null ) {
			if ( this.lazyLoad ) {
				try {
					this.loadLanguage( locale );
				} catch ( IOException ignored ) {
					// Ignored - returned string will indicate error anyways
				}

				translation = this.translations.get( locale );
				if ( translation == null ) {
					// Last chance - maybe the fallback locale may be used:
					if ( !I18NUtilities.shouldUseFallbackLocale() || ( translation = this.translations.get( I18NUtilities.getFallbackLocale() ) ) == null ) {
						// No translation available:
						return TranslationStorage.ENOTRANS;
					}
				}
			} else {
				// Last chance - maybe the fallback locale may be used:
				if ( !I18NUtilities.shouldUseFallbackLocale() || ( translation = this.translations.get( I18NUtilities.getFallbackLocale() ) ) == null ) {
					// Locale not loaded:
					return TranslationStorage.ELOCNL;
				}
			}
		}

		String raw = translation.get( keyHash );
		if ( raw == null ) {
			// Last chance - maybe there is a translation in the fallback locale:
			if ( I18NUtilities.shouldUseFallbackLocale() && !locale.equals( I18NUtilities.getFallbackLocale() ) ) {
				translation = this.translations.get( I18NUtilities.getFallbackLocale() );
				if ( translation != null ) {
					raw = translation.get( keyHash );
					if ( raw == null ) {
						// No translation available:
						return TranslationStorage.ENOTRANS;
					}
				} else {
					// No translation available:
					return TranslationStorage.ENOTRANS;
				}
			} else {
				// No translation available:
				return TranslationStorage.ENOTRANS;
			}
		}

		return raw;
	}

}
