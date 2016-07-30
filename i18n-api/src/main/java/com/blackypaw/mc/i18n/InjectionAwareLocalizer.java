/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

/**
 * A localizer may be used to get encoded strings which will be translated into actual
 * chat messages on a per-player basis. All encoded strings returned by a localizer are
 * valid until it gets closed or the I18N instance it is bound to gets closed.
 * will get disposed).
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class InjectionAwareLocalizer extends Localizer {
	
	/**
	 * Prepares an encoded injection string which will be converted to the respective translations
	 * of the message identified by the given translation key for each and every player.
	 * <p>
	 * This localizers must not be disposed whilst the returned encoded injection string is still
	 * being used. Otherwise the string cannot be translated anymore.
	 * <p>
	 * In contrast to {@link #inject(String, Object...)} this method does only return the already
	 * encoded injection string instead of a full-blown injection handle. This is due to the fact
	 * that there is no need to cache additional arguments in this version what simplifies scope
	 * handling. Therefore when using this method one does not need to care about manually
	 * disposing an injected string after it has been used and all packets it is used in have been
	 * sent.
	 *
	 * @param key The translation key of the message to be injected
	 *
	 * @return The encoded injection string
	 */
	public abstract String inject( String key );
	
	/**
	 * Prepares an injection handle capable of translating into respective translation
	 * messages based on player locales optionally inserting additional arguments.
	 * <p>
	 * As long as neither the returned injection handle nor this localizer have been
	 * disposed, text in any packet that is equal to the encoded form of the returned
	 * injection handle will be translated appropriately for each and every player on
	 * the server.
	 * <p>
	 * Basically the injection system works just like a regular atomic table in which
	 * the user is free of inserting objects but for whose deletion he is responsible,
	 * too. This is due to the fact that there is no reliable way of detecting when
	 * an injected string will no longer be sent over wire.
	 * <p>
	 * If you do not need to inject additional arguments you should consider using
	 * {@link InjectionAwareLocalizer#inject(String)} instead as it will free you from the burden
	 * of having to dispose any handles manually.
	 *
	 * @param key The translation key of the message to be injected
	 * @param args Additional arguments to be injected
	 *
	 * @return The created injection handle
	 */
	public abstract InjectionHandle inject( String key, Object... args );
	
	/**
	 * Resolves an injection handle given its ID.
	 *
	 * @param id The ID of the injection handle
	 *
	 * @return The injection handle or null if not found
	 */
	abstract InjectionHandle resolveInjectionHandle( int id );
	
	/**
	 * Gets the localizer's unique ID.
	 *
	 * @return The localizer's unique ID
	 */
	abstract int getId();
	
	/**
	 * Disposes an injection handle given its ID.
	 *
	 * @param id The ID of the injection handle
	 */
	abstract void disposeInjectionHandle( int id );
	
}
