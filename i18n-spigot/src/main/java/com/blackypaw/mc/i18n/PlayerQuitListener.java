/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Internal helper. Used to uncache player locales after players have left the server.
 *
 * @author BlackyPaw
 * @version 1.0
 */
class PlayerQuitListener implements Listener {

	private I18NSpigotImpl i18n;

	/**
	 * Constructs a new player quit listener.
	 *
	 * @param i18n The I18N instance
	 */
	public PlayerQuitListener( I18NSpigotImpl i18n ) {
		this.i18n = i18n;
	}

	/**
	 * Handles player kick events as late as possible so that all other plugins get a chance of
	 * translating messages before the player actually gets kicked.
	 *
	 * @param event The player kick event
	 */
	@EventHandler( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onPlayerKick( PlayerKickEvent event ) {
		this.onPlayerLeft( event.getPlayer() );
	}

	/**
	 * Handles player quit events as late as possible so that all other plugins get a chance of
	 * translating messages before the player actually quits the server.
	 *
	 * @param event The player quit event
	 */
	@EventHandler( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onPlayerQuit( PlayerQuitEvent event ) {
		this.onPlayerLeft( event.getPlayer() );
	}

	private void onPlayerLeft( Player player ) {
		this.i18n.unstoreLocale( player.getUniqueId() );
	}

}
