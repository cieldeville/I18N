/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.Locale;

/**
 * Generic interface for I18N implementations.
 * <p>
 * This interface serves as an entry point into the I18N library. One can grab an implementation
 * of this interface from one of the platform adapters found inside the I18N repository (e.g.
 * i18n-spigot).
 * <p>
 * The interface possesses a single generic argument type which is the type of key used to load and
 * store locales.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public interface I18N<KeyType> {
	
	/**
	 * Checks whether or not this I18N implementation supports injection functionalities.
	 * If so, casting it to an {@link InjectionAwareI18N} of the same KeyType is guaranteed to succeed.
	 *
	 * @return Whether or not this I18N implementation supports injection functionalities
	 *
	 * @see InjectionAwareI18N
	 */
	boolean isInjectionSupported();
	
	/**
	 * Creates a new localizer for application-purposes.
	 * <p>
	 * Creates a new localizer which will refer back to the given translation storage
	 * for retrieving translated strings. The returned localizer will be valid until
	 * it gets closed manually or this I18N instance gets diposed. When the latter one
	 * may happen is at the discretion of the providing platform adapter.
	 *
	 * @param storage The storage the created localizer should refer back to
	 *
	 * @return The created localizer instance
	 */
	Localizer createLocalizer( TranslationStorage storage );
	
	/**
	 * Gets the locale stored under the given key.
	 * <p>
	 * Depending on the implementation locales might actually get pre-loaded. One example for this
	 * is the Spigot implementation which will load locales as soon as the respective player logs in
	 * so that other plugins can make use of the locale immediately.
	 *
	 * @param key The key the requested locale is stored under
	 *
	 * @return The locale if found or null if no locale could be found under the given key
	 */
	Locale getLocale( KeyType key );
	
	/**
	 * Attempts to set the locale stored under the given key to the given new value.
	 * <p>
	 * As this operation might fail depending on the underlying locale resolver storing
	 * the locales this method is not guaranteed to succeed. The returned boolean
	 * value indicates whether or not the operation succeeded in which case there may
	 * be a platform-specific event being dispatched (e.g. PlayerSetLanguageEvent on the
	 * Spigot platform).
	 *
	 * @param key    The key whose locale to set
	 * @param locale The locale to set
	 *
	 * @return Whether or not the operation was successful
	 */
	boolean trySetLocale( KeyType key, Locale locale );
	
	/**
	 * Sets the locale resolver used by this I18N implementation programmatically.
	 * <p>
	 * Invoking this method will overwrite the locale resolver configured by the user
	 * or any other locale resolver set by other components previously. On some platforms
	 * overwriting the locale resolver more than once will generate a warning message
	 * in the application logfiles so that the user is notified of potential problems
	 * caused by overwriting other programmatically set locale resolvers.
	 * <p>
	 * All attempts to load / store a locale will be routed through the given locale resolver
	 * after this method returns. All cached or pre-loaded locales which have been loaded
	 * using a previous locale resolver will generally not be reloaded, though. Therefore
	 * this should usually be set at application startup.
	 *
	 * @param resolver The locale resolver to set
	 */
	void setLocaleResolver( LocaleResolver<KeyType> resolver );
	
	/**
	 * Checks whether or not translation storages are advised to fall back to translations
	 * in the fallback locale if no translation could be found for a specific other language.
	 * <p>
	 * This value cannot be set programmatically and is usually configured by the end-user.
	 *
	 * @return Whether or not translation storages should check for translations in the fallback locale if they are unavailable in other ones
	 */
	boolean shouldUseFallbackLocale();
	
	/**
	 * Gets the fallback locale to be used when a translation is unavailable for a specific language.
	 * <p>
	 * The fallback locale has also got other uses inside I18N, e.g. if a locale could not be loaded
	 * a locale resolver must return the fallback locale instead.
	 * <p>
	 * This value cannot be set programmatically and is usually configured by the end-user.
	 *
	 * @return The fallback locale
	 */
	Locale getFallbackLocale();
	
}
