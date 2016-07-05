/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter class for simplifying translation storage implementations. Classes
 * derived from this adapter class will always ship with support for lazy-loading
 * and full fallback locale support.
 *
 * @author BlackyPaw
 * @version 1.0
 */
abstract class TranslationStorageAdapter extends TranslationStorage {

	protected Map<Locale, Map<Integer, String>> translations;

	protected TranslationStorageAdapter( boolean lazyLoad ) {
		super( lazyLoad );
		this.translations = new HashMap<>();
	}

	@Override
	public void loadLanguage( Locale locale, Map<String, String> translations ) throws IOException {
		Map<Integer, String> hashedTranslations = new HashMap<>( translations.size() );
		for ( Map.Entry<String, String> translation : translations.entrySet() ) {
			int hash = FNVHash.hash1a32( translation.getKey() );
			if ( hashedTranslations.containsKey( hash ) ) {
				throw new IOException( "Colliding hash codes for distinct translation keys: '" + translation.getKey() + "'" );
			}
			hashedTranslations.put( hash, translation.getValue() );
		}
		this.translations.put( locale, hashedTranslations );
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
