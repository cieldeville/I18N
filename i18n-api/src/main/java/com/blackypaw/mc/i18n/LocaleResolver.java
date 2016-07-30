/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.Locale;

/**
 * Interface used for managing locales and their storage. Several default implementations are included
 * but implementations can also put their own locale resolvers in place. See {@link I18N#setLocaleResolver(LocaleResolver)}
 * for further details on this topic.
 * <p>
 * If a locale resolver implements the AutoCloseable interface all I18N implementations are required to invoke
 * its .close() method before disposal.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public interface LocaleResolver<Key> {

	/**
	 * Resolves the locale belonging to the given key synchronously. This method must never return null. Therefore it
	 * may return the fallback locale retrievable via {@link I18N#getFallbackLocale()} if
	 * it cannot resolve the locale of a given key instead. This method must be thread-safe.
	 *
	 * @param key The key to resolve the locale for
	 *
	 * @return The locale belonging the the given key
	 */
	Locale resolveLocale( Key key );

	/**
	 * Attempts to change the locale stored under key. On success the method should return true, on failure
	 * it should return false. This method should be atomic, i.e. if one was to invoke {@link #resolveLocale(Key)}
	 * right after this method returns one should get the new locale already.
	 *
	 * @param key The key to set the locale of
	 * @param locale The locale to set
	 *
	 * @return Whether or not the locale could be changed successfully
	 */
	boolean trySetPlayerLocale( Key key, Locale locale );

}
