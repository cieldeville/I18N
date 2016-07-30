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
 * @author BlackyPaw
 * @version 1.0
 */
class LocalizerSpigotImpl extends InjectionAwareLocalizer {
	
	private final int                id;
	private final TranslationStorage storage;
	
	private Map<Integer, InjectionHandle> injectionHandles;
	private int                           nextInjectionId;
	
	LocalizerSpigotImpl( int id, TranslationStorage storage ) {
		this.id = id;
		this.storage = storage;
		
		this.injectionHandles = new HashMap<>();
		this.nextInjectionId = 0;
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
	
	@Override
	public InjectionHandle inject( String key, Object... args ) {
		int injectionId = this.nextInjectionId++;
		InjectionHandle handle = new InjectionHandle( this, injectionId, key, args );
		this.injectionHandles.put( injectionId, handle );
		return handle;
	}
	
	@Override
	int getId() {
		return this.id;
	}
	
	@Override
	void disposeInjectionHandle( int id ) {
		this.injectionHandles.remove( id );
	}
	
	@Override
	InjectionHandle resolveInjectionHandle( int id ) {
		return this.injectionHandles.get( id );
	}
	
	@Override
	public void close() throws Exception {
		this.injectionHandles.clear();
		this.nextInjectionId = 0;
	}
	
}
