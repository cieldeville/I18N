package com.blackypaw.mc.i18n;

import java.io.IOException;
import java.util.Locale;

/**
 * TranslationStorages function as lookup tables for actual translation strings in different
 * languages. Some implementations may support lazy loading, i.e. loading languages 'on-the-fly'
 * when required.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class TranslationStorage {

	protected static final String ELOCNL   = "ELOCNL";
	protected static final String ENOTRANS = "ENOTRANS";
	protected static final String ENOLCLZR = "ENOLCLZR";
	protected static final String ENOINJHD = "ENOINJHD";

	/**
	 * Specifies whether or not the translation storage is allowed to attempt to load
	 * languages if they are requested but not yet loaded.
	 */
	protected final boolean lazyLoad;

	/**
	 * Constructs a new translation storage. If lazyLoad is set to true the translation
	 * storage is allowed to load translations if they are requested but not yet loaded.
	 * Note, that the implementation is not obligated to actually try to lazy load any
	 * requested translations.
	 *
	 * @param lazyLoad Whether or not lazy loading should be allowed
	 */
	protected TranslationStorage( boolean lazyLoad ) {
		this.lazyLoad = lazyLoad;
	}

	/**
	 * Checks whether or not this translation storage is allowed to lazy load translations.
	 *
	 * @return Whether or not this translation storage is allowed to lazy load translations
	 */
	public boolean isLazyLoadAllowed() {
		return this.lazyLoad;
	}

	/**
	 * Attempts to load the language represented by the given locale. If no translation
	 * exists for the requested language or the translation could not be loaded an
	 * IOException will be thrown.
	 * <p>
	 * If the language has already been loaded before the results of this invocation will
	 * overwrite any cached translations.
	 *
	 * @param locale The language to load
	 *
	 * @throws IOException Thrown if the translation was not found or could not be loaded
	 */
	public abstract void loadLanguage( Locale locale ) throws IOException;

	/**
	 * Translates a message.
	 * <p>
	 * Given the language to translate into, the translation key of the message to translate
	 * and optionally an array of arguments to be inserted into the translation this method
	 * will attempt to translate a message for the given locale.
	 * <p>
	 * The given arguments will replace special identifiers inside the actual translation.
	 * The first argument will replace {0}, the second one will replace {1} and so on.
	 * Arguments are converted into strings via their {@link Object#toString()} method.
	 * <p>
	 * If the translation fails for some reason, the function will still return a valid string
	 * which will be no longer than 16 characters so that it won't cause any troubles with
	 * mechanisms like scoreboards. The following abbreviations may be returned:
	 * <ol>
	 *     <li>ELOCNL - The specified locale has not yet been loaded (will only be returned if lazy load is disabled)</li>
	 *     <li>ENOTRANS - There is no translation for the given translation key in the specified locale</li>
	 * </ol>
	 *
	 * @param locale The language to translate into
	 * @param key The translation key of the message to be translated
	 * @param args Optional arguments to be inserted into the translation
	 *
	 * @return The translated string
	 */
	public String translate( Locale locale, String key, Object... args ) {
		String translation = this.getRawTranslation( locale, key );

		if ( args.length > 0 ) {
			for ( int i = 0; i < args.length; ++i ) {
				translation = translation.replaceAll( "\\{" + i + "\\}", args[i].toString() );
			}
		}

		return translation;
	}

	/**
	 * See {@link #translate(Locale, String, Object...)}.
	 *
	 * @param locale The language to translate into
	 * @param keyHash The translation key of the message to be translated
	 * @param args Optional arguments to be inserted into the translation
	 *
	 * @return The translated string
	 */
	public String translate( Locale locale, int keyHash, Object... args ) {
		String translation = this.getRawTranslation( locale, keyHash );

		if ( args.length > 0 ) {
			for ( int i = 0; i < args.length; ++i ) {
				translation = translation.replaceAll( "\\{" + i + "\\}", args[i].toString() );
			}
		}

		return translation;
	}

	/**
	 * Gets the raw translation of the given translation key for the specified locale.
	 * "Raw" hereby means that no argument post-procession will be performed.
	 * <p>
	 * May return the special strings documented in {@link #translate(Locale, String, Object...)}.
	 *
	 * @param locale The locale to translate into
	 * @param key The translation key of the message to be translated.
	 *
	 * @return The raw translation of the given translation key for the specified locale
	 */
	protected abstract String getRawTranslation( Locale locale, String key );

	/**
	 * See {@link #getRawTranslation(Locale, String)}.
	 *
	 * @param locale The locale to translate into
	 * @param keyHash The hash value of the translation key of the message to be translated.
	 *
	 * @return The raw translation of the given translation key for the specified locale
	 */
	protected abstract String getRawTranslation( Locale locale, int keyHash );

}
