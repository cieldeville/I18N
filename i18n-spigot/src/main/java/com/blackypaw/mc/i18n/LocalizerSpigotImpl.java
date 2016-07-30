/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

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
	}
	
	@Override
	public String translateDirect( Locale locale, String key, Object... args ) {
		return null;
	}
	
	@Override
	public String translateDirect( Locale locale, int keyHash, Object... args ) {
		return null;
	}
	
	@Override
	public String inject( String key ) {
		return null;
	}
	
	@Override
	public InjectionHandle inject( String key, Object... args ) {
		return null;
	}
	
	@Override
	int getId() {
		return 0;
	}
	
	@Override
	void disposeInjectionHandle( int id ) {
		
	}
	
	@Override
	InjectionHandle resolveInjectionHandle( int id ) {
		return null;
	}
	
	@Override
	public void close() throws Exception {
		
	}
	
}
