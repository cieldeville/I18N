/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A localizer may be used to get encoded strings which will be translated into actual
 * chat messages on a per-player basis. All encoded strings returned by a localizer are
 * valid until it gets disposed or the plugin gets unloaded (in which case all localizers
 * will get disposed).
 *
 * @author BlackyPaw
 * @version 1.0
 */
public final class Localizer {

	private final int id;
	private final TranslationStorage storage;

	private Map<Integer, InjectionHandle> injectionHandles;
	private int nextInjectionId;

	/**
	 * Constructs a new localizer given its unique ID on factory scope and the translation storage
	 * it should use for translating messages.
	 *
	 * @param id The ID of the localizer
	 * @param storage The storage to be used for translating messages
	 */
	Localizer( int id, TranslationStorage storage ) {
		this.id = id;
		this.storage = storage;

		this.injectionHandles = new HashMap<>();
		this.nextInjectionId = 0;
	}

	/**
	 * Translates a message directly using the translation storage the localizer was given
	 * during its creation. See {@link TranslationStorage#translate(Locale, String, Object...)}
	 * for a more thorough explanation.
	 *
	 * @param locale The language to translate into
	 * @param key The translation key of the message to be translated
	 * @param args Optional arguments to be inserted into the translation
	 *
	 * @return The translated string
	 */
	public String translateDirect( Locale locale, String key, Object... args ) {
		return this.storage.translate( locale, key, args );
	}

	/**
	 * See {@link #translateDirect(Locale, String, Object...)}
	 *
	 * @param locale The language to translate into
	 * @param keyHash The hash of the translation key of the message to be translated
	 * @param args Optional arguments to be inserted into the translation
	 *
	 * @return The translated string
	 */
	public String translateDirect( Locale locale, int keyHash, Object... args ) {
		return this.storage.translate( locale, keyHash, args );
	}

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
	public String inject( String key ) {
		int localizerId = this.id;
		int keyHash     = FNVHash.hash1a32( key );

		char[] encoded = new char[12];
		encoded[0] = 't';
		encoded[1] = 'r';
		encoded[2] = 'n';
		encoded[3] = 's';

		// We can safely convert into char directly as values between 0 - 255
		// will fall onto the Basic Multilingual Plane of the Unicode standard
		// and will thus be equal to their actual integer value:
		encoded[4] = (char) ( (localizerId >>> 24) & 0xFF );
		encoded[5] = (char) ( (localizerId >>> 16) & 0xFF );
		encoded[6] = (char) ( (localizerId >>>  8) & 0xFF );
		encoded[7] = (char) ( (localizerId) & 0xFF );

		encoded[8]  = (char) ( (keyHash >>> 24) & 0xFF );
		encoded[9]  = (char) ( (keyHash >>> 16) & 0xFF );
		encoded[10] = (char) ( (keyHash >>>  8) & 0xFF );
		encoded[11] = (char) ( (keyHash) & 0xFF );

		// Characters 12 - 15 unused but reserved

		return new String( encoded );
	}

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
	 * {@link Localizer#inject(String)} instead as it will free you from the burden
	 * of having to dispose any handles manually.
	 *
	 * @param key The translation key of the message to be injected
	 * @param args Additional arguments to be injected
	 *
	 * @return The created injection handle
	 */
	public InjectionHandle inject( String key, Object... args ) {
		int injectionId = this.nextInjectionId++;
		InjectionHandle handle = new InjectionHandle( this, injectionId, key, args );
		this.injectionHandles.put( injectionId, handle );
		return handle;
	}

	/**
	 * Disposes the localizer thus making it unavailable
	 * for any further localization tasks.
	 */
	public void dispose() {
		this.injectionHandles.clear();
		this.nextInjectionId = 0;
	}

	/**
	 * Gets the localizer's unique ID.
	 *
	 * @return The localizer's unique ID
	 */
	int getId() {
		return this.id;
	}

	/**
	 * Disposes an injection handle given its ID.
	 *
	 * @param id The ID of the injection handle
	 */
	void disposeInjectionHandle( int id ) {
		this.injectionHandles.remove( id );
	}

	/**
	 * Resolves an injection handle given its ID.
	 *
	 * @param id The ID of the injection handle
	 *
	 * @return The injection handle or null if not found
	 */
	InjectionHandle resolveInjectionHandle( int id ) {
		return this.injectionHandles.get( id );
	}

}
