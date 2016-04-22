package com.blackypaw.mc.i18n;

import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Interface used for managing player locales. Several default implementations are included
 * but plugins can also put their own locale resolvers in place. See {@link I18NUtilities#setLocaleResolver(LocaleResolver)}
 * for further details on this topic.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public interface LocaleResolver {

	/**
	 * Resolves a player's locale synchronously. This method must never return null. Therefore it
	 * may return the fallback locale retrievable via {@link I18NUtilities#getFallbackLocale()} if
	 * it cannot resolve a given player's locale instead. This method must be thread-safe.
	 *
	 * @param player The player to resolve the locale for
	 *
	 * @return The player's locale
	 */
	Locale resolveLocale( Player player );

	/**
	 * Attempts to change the given player's locale. On success the method should return true, on failure
	 * it should return false. This method should be atomic, i.e. if one was to invoke {@link #resolveLocale(Player)}
	 * right after this method returned he should get the player's new locale.
	 *
	 * @param player The player to set the locale of
	 * @param locale The locale to set
	 *
	 * @return Whether or not the locale could be changed successfully
	 */
	boolean trySetPlayerLocale( Player player, Locale locale );

}
