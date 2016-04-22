package com.blackypaw.mc.i18n;

import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Very basic implementation of a locale resolver: simply returns the locale it is given
 * during its construction for each and every player.
 *
 * @author BlackyPaw
 * @version 1.0
 */
class ConstantLocaleResolver implements LocaleResolver {

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
	public Locale resolveLocale( Player player ) {
		return this.locale;
	}

	@Override
	public boolean trySetPlayerLocale( Player player, Locale locale ) {
		// Cannot change player locales with constant locale resolver:
		return false;
	}

}
