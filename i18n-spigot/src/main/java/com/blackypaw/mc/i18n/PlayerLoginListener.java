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
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Locale;

/**
 * Internal helper. Used to resolve a player's locale as soon as possible.
 *
 * @author BlackyPaw
 * @version 1.0
 */
class PlayerLoginListener implements Listener {

	private I18NSpigotImpl i18n;

	/**
	 * Constructs a new player login listener.
	 *
	 * @param i18n The I18N instance
	 */
	public PlayerLoginListener( I18NSpigotImpl i18n ) {
		this.i18n = i18n;
	}

	/**
	 * Catches player logins as soon as possible by having the lowest priority possible.
	 *
	 * @param event The player login event
	 */
	@EventHandler( priority = EventPriority.LOWEST, ignoreCancelled = true )
	public void onPlayerLogin( PlayerLoginEvent event ) {
		Player player = event.getPlayer();
		Locale locale = this.i18n.getLocaleResolver().resolveLocale( player.getUniqueId() );
		this.i18n.storeLocale( player.getUniqueId(), locale );
	}

}
