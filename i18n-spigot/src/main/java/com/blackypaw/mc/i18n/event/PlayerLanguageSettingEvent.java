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
 * This event gets called after a player joined and transmitted his / her language
 * as set in the client's language settings menu or whenever a player has changed
 * his / her settings inside said menu.
 * <p>
 * Remark: this event might get called multiple times in a row with the same values
 * as the client might send the underlying packets multiple times or change other
 * settings contained in these packets. In order to reduce the overhead introduced
 * by I18N a little it will not check if a value is the same as when the event was
 * last called for a specific player, although dependant plugin may manually do so.
 * This behaviour is of course undesired and may be subject to change in future
 * versions.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public final class PlayerLanguageSettingEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final Locale language;

	/**
	 * Constructs a new player language setting event. You should never call the event
	 * manually in order to avoid confusing other plugins which might depend on the
	 * exact circumstances this event is usually encountered in.
	 *
	 * @param player   The player who is affected by the event
	 * @param language The language the player set
	 */
	public PlayerLanguageSettingEvent( Player player, Locale language ) {
		this.player = player;
		this.language = language;
	}

	/**
	 * Gets the player whose language setting was detected.
	 *
	 * @return The player whose language setting was detected
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Gets the language the player has set inside his / her client's language settings menu.
	 *
	 * @return The language the player has set
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
