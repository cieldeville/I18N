/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

/**
 * An injection handle is a structure used as an index into an atomic-like lookup table. As long
 * as neither the handle itself nor the localizer that created it get disposed, any message equal
 * to the encoded string of this handle will be translated with the message identified by the
 * translation key specified during creation of the handle. Also, unlike a direct injection string
 * as produced by {@link InjectionAwareLocalizer#inject(String)} strings injected via injection handles can
 * optionally have one or more additional arguments which will be inserted into the translation.
 * It is the user's obligation to dispose the handle when no longer needed and to ensure that all
 * packets containing the handle's encoded string have been sent out before the handle gets
 * disposed. Otherwise the error message "ENOINJHD" (abbreviation for "Error - no injection handle"
 * will be sent to the user).
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class InjectionHandle {

	private final InjectionAwareLocalizer localizer;
	private final int                     id;
	private final String                  key;
	private final Object[]                args;

	private final String encoded;

	InjectionHandle( InjectionAwareLocalizer localizer, int id, String key, Object... args ) {
		this.localizer = localizer;
		this.id = id;
		this.key = key;
		this.args = args;

		this.encoded = this.encode();
	}

	/**
	 * Gets the encoded injection string of this injection handle. This string is what should
	 * be handed to any function requiring an actual string value such as {@link org.bukkit.entity.Player#sendMessage(String)}.
	 *
	 * @return The encoded string value of this injection handle
	 */
	public String getEncoded() {
		return this.encoded;
	}

	/**
	 * Gets the translation key of the message represented by this injection handle.
	 *
	 * @return The translation key of the message represented by this injection handle
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * Gets any additional arguments to be inserted into the translation.
	 *
	 * @return Any additional arguments to be inserted into the translation
	 */
	public Object[] getArgs() {
		return this.args;
	}

	/**
	 * Disposes this injection handle. Any attempts at translating the encoded string value of this
	 * injection handle will fail after this method has been called. Ensures that no further references
	 * to this injection handle will be held internally so that a user may decide when to gargabe-collect
	 * an injection handle.
	 */
	public void dispose() {
		this.localizer.disposeInjectionHandle( this.id );
	}

	/**
	 * Constructs the encoded string for the handle.
	 *
	 * @return The encoded string for the handle
	 */
	private String encode() {
		int localizerId = localizer.getId();
		int injectionId = this.id;

		char[] encoded = new char[12];
		encoded[0] = 'i';
		encoded[1] = 'n';
		encoded[2] = 'j';
		encoded[3] = 'c';

		// We can safely convert into char directly as values between 0 - 255
		// will fall onto the Basic Multilingual Plane of the Unicode standard
		// and will thus be equal to their actual integer value:
		encoded[4] = (char) ( (localizerId >>> 24) & 0xFF );
		encoded[5] = (char) ( (localizerId >>> 16) & 0xFF );
		encoded[6] = (char) ( (localizerId >>>  8) & 0xFF );
		encoded[7] = (char) ( (localizerId) & 0xFF );

		encoded[8]  = (char) ( (injectionId >>> 24) & 0xFF );
		encoded[9]  = (char) ( (injectionId >>> 16) & 0xFF );
		encoded[10] = (char) ( (injectionId >>>  8) & 0xFF );
		encoded[11] = (char) ( (injectionId) & 0xFF );

		// Characters 12 - 15 unused but reserved

		return new String( encoded );
	}

}
