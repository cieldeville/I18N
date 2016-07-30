/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Localizer factory which keeps references to all localizers it instantiated for
 * lookup during translation steps.
 *
 * @author BlackyPaw
 * @version 1.0
 */
class LocalizerFactory {

	private Logger                                logger;
	private Map<Integer, InjectionAwareLocalizer> localizers;
	private int                                   nextId;

	/**
	 * Constructs a new localizer factory that will use the
	 * given logger for logging messages.
	 *
	 * @param logger The logger to use
	 */
	LocalizerFactory( Logger logger ) {
		this.logger = logger;
		this.localizers = new HashMap<>();
		this.nextId = 0;
	}

	/**
	 * Creates a new localizer instance given the translation storage it should use for
	 * translating messages.
	 *
	 * @param storage The translation storage to use for translating messages
	 */
	InjectionAwareLocalizer createInstance( TranslationStorage storage ) {
		int                     id        = this.nextId++;
		InjectionAwareLocalizer localizer = new LocalizerSpigotImpl( id, storage );
		this.localizers.put( id, localizer );
		return localizer;
	}

	/**
	 * Attempts to find an instance given its unique ID as assigned by this factory.
	 *
	 * @param id The ID of the instance to find
	 *
	 * @return The instance if found or null otherwise
	 */
	InjectionAwareLocalizer findInstance( int id ) {
		return this.localizers.get( id );
	}

	/**
	 * Disposes the factory and all localizers it created.
	 */
	void dispose() {
		// Disposes all localizers the factory created:
		for ( Localizer localizer : this.localizers.values() ) {
			try {
				localizer.close();
			} catch ( Exception e ) {
				this.logger.log( Level.WARNING, "Failed to close localizer", e );
			}
		}
	}
	
}
