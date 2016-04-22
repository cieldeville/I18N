package com.blackypaw.mc.i18n;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Localizer factory which keeps references to all localizers it instantiated for
 * lookup during translation steps.
 *
 * @author BlackyPaw
 * @version 1.0
 */
class LocalizerFactory {

	private Plugin plugin;
	private Map<Integer, Localizer> localizers;
	private int nextId;

	/**
	 * Constructs a new localizer factory that will be attached
	 * to the given plugin (required for packet interception).
	 *
	 * @param plugin The plugin to attach the factory to
	 */
	public LocalizerFactory( Plugin plugin ) {
		this.plugin = plugin;
		this.localizers = new HashMap<>();
		this.nextId = 0;
	}

	/**
	 * Creates a new localizer instance given the translation storage it should use for
	 * translating messages.
	 *
	 * @param storage The translation storage to use for translating messages
	 */
	public Localizer createInstance( TranslationStorage storage ) {
		int id = this.nextId++;
		Localizer localizer = new Localizer( id, storage );
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
	public Localizer findInstance( int id ) {
		return this.localizers.get( id );
	}

	/**
	 * Disposes the factory and all localizers it created.
	 */
	public void dispose() {
		// Disposes all localizers the factory created:
		for ( Localizer localizer : this.localizers.values() ) {
			localizer.dispose();
		}
	}
	
}
