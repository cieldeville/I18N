/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Locale;

/**
 * This event gets called whenever a player successfully set his or her language.
 * Can be used to resend scoreboards or other components so that the player will
 * see them in his / her new language.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public final class PlayerSetLanguageEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final Locale language;

	/**
	 * Constructs a new player set language event. You should never call the event
	 * yourself as that might cause troubles when multiple plugins are running on
	 * your server.
	 *
	 * @param player The player who set his language
	 * @param language The language he / she set
	 */
	public PlayerSetLanguageEvent( Player player, Locale language ) {
		this.player = player;
		this.language = language;
	}

	/**
	 * Gets the player who set his / her language.
	 *
	 * @return The player who set his / her language
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Gets the language the player set.
	 *
	 * @return The language the player set
	 */
	public Locale getLanguage() {
		return this.language;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
