/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import org.yaml.snakeyaml.Yaml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Translation storage implementation supporting .yml files based off the
 * SnakeYaml parser already shipped as a dependency by Bukkit. Supports
 * lazy-loading and manual loading of translations.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class YamlTranslationStorage extends TranslationStorageAdapter {

	private final File baseDirectory;

	/**
	 * Constructs a new Yaml translation storage. The created instance will attempt
	 * to resolve translations given the directory all .yml files containing the
	 * locale specific translations are stored in. The files are expected to be named
	 * after the language code of the respective language, e.g. en.yml or
	 * de.yml.
	 * <p>
	 * Per default the created instance is not allowed to lazy load any translations.
	 *
	 * @param directory The directory all property files are stored in
	 */
	public YamlTranslationStorage( File directory ) {
		super( false );
		this.baseDirectory = directory;
	}

	/**
	 * Constructs a new Yaml translation storage. The created instance will attempt
	 * to resolve translations given the directory all .yml files containing the
	 * locale specific translations are stored in. The files are expected to be named
	 * after the language code of the respective language, e.g. en.yml or
	 * de.yml.
	 *
	 * @param directory The directory all property files are stored in
	 * @param lazyLoad Whether or not lazy loading of translations should be allowed
	 */
	public YamlTranslationStorage( File directory, boolean lazyLoad ) {
		super( lazyLoad );
		this.baseDirectory = directory;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void loadLanguage( Locale locale ) throws IOException {
		// Clear any cached results:
		this.translations.remove( locale );

		final File translationFile = new File( this.baseDirectory, locale.getLanguage() + ".yml" );

		try ( InputStream in = new BufferedInputStream( new FileInputStream( translationFile ) ) ) {
			Yaml yaml = new Yaml();
			Object parsed = yaml.load( in );

			if ( parsed instanceof Map ) {
				Map<String, Object> translationParsed = (Map<String, Object>) parsed;
				Map<Integer, String> translation = new HashMap<>();

				this.addTranslationKeyMap( translation, translationParsed, new StringBuilder() );

				this.translations.put( locale, translation );
			} else {
				throw new IOException( "Failed to load YAML translation file '" + translationFile + "': YAML data could not be converted to a Map. Please review the file and ensure that it does only contain key-value pairs" );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	private void addTranslationKey( Map<Integer, String> translation, Map.Entry<String, Object> entry, StringBuilder keyBuilder ) throws IOException {
		int rollback = keyBuilder.length();

		keyBuilder.append( entry.getKey() );
		Object value = entry.getValue();
		if ( value instanceof String ) {
			// Direct translation key:
			String translationKey = keyBuilder.toString();
			int hash = FNVHash.hash1a32( translationKey );
			if ( translation.containsKey( hash ) ) {
				throw new IOException( "Colliding hash codes for distinct translation keys: '" + translationKey + "'; please rename the translation key" );
			}
			translation.put( hash, (String) value );
		} else if ( value instanceof List ) {
			// A list of sub-translation keys:
			keyBuilder.append( '.' );

			for ( Object field : (List) value ) {
				// List elements must be object maps:
				if ( field instanceof Map ) {
					this.addTranslationKeyMap( translation, (Map<String, Object>) field, keyBuilder );
				}
			}
		} else if ( value instanceof Map ) {
			keyBuilder.append( '.' );
			this.addTranslationKeyMap( translation, (Map<String, Object>) value, keyBuilder );
		}

		keyBuilder.setLength( rollback );
	}

	private void addTranslationKeyMap( Map<Integer, String> translation, Map<String, Object> map, StringBuilder keyBuilder ) throws IOException {
		for ( Map.Entry<String, Object> translationEntry : map.entrySet() ) {
			this.addTranslationKey( translation, translationEntry, keyBuilder );
		}
	}

}
