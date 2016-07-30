/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import com.blackypaw.mc.i18n.event.PlayerLanguageSettingEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class InterceptorSettings extends InterceptorBase {
	
	InterceptorSettings( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
		super( plugin, gson, i18n, ListenerPriority.LOWEST, PacketType.Play.Client.SETTINGS );
	}
	
	@Override
	public void onPacketReceiving( PacketEvent event ) {
		final Player         player   = event.getPlayer();
		final Locale language = new Locale( event.getPacket().getStrings().read( 0 ).substring( 0, 2 ) );
		
		PlayerLanguageSettingEvent call = new PlayerLanguageSettingEvent( player, language );
		Bukkit.getServer().getPluginManager().callEvent( call );
	}
	
}
