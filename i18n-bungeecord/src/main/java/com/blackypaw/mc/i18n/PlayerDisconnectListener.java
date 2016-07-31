/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class PlayerDisconnectListener implements Listener {
	
	private final I18NBungeeCordImpl i18n;
	
	public PlayerDisconnectListener( I18NBungeeCordImpl i18n ) {
		this.i18n = i18n;
	}
	
	@EventHandler( priority = EventPriority.HIGHEST )
	public void onPlayerDisconnect( PlayerDisconnectEvent event ) {
		PendingConnection player = event.getPlayer().getPendingConnection();
		this.i18n.unstoreLocale( player.getUniqueId() );
		this.i18n.unwatchConnection( player );
	}
	
}
