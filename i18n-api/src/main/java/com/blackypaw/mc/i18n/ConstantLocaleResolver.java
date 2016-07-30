/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.Locale;

/**
 * Very basic implementation of a locale resolver: simply returns the locale it is given
 * during its construction for each and every player.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class ConstantLocaleResolver<Key> implements LocaleResolver<Key> {

	private final Locale locale;

	/**
	 * Constructs a new constant locale resolver which will always return locale as any player's locale.
	 *
	 * @param locale The locale to return for all players
	 */
	public ConstantLocaleResolver( Locale locale ) {
		this.locale = locale;
	}

	@Override
	public Locale resolveLocale( Key key ) {
		return this.locale;
	}

	@Override
	public boolean trySetPlayerLocale( Key key, Locale locale ) {
		// Cannot change player locales with constant locale resolver:
		return false;
	}

}
