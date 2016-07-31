/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.Locale;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class LocalizerBungeeCordImpl extends Localizer {
	
	private final TranslationStorage storage;
	
	LocalizerBungeeCordImpl( TranslationStorage storage ) {
		this.storage = storage;
	}
	
	@Override
	public String translateDirect( Locale locale, String key, Object... args ) {
		return this.storage.translate( locale, key, args );
	}
	
	@Override
	public String translateDirect( Locale locale, int keyHash, Object... args ) {
		return this.storage.translate( locale, keyHash, args );
	}
	
	@Override
	public void close() throws Exception {
		// Nothing to clean up here
	}
	
}
