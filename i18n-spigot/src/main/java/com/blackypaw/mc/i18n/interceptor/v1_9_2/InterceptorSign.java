/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n.interceptor.v1_9_2;

import com.blackypaw.mc.i18n.I18NSpigotImpl;
import com.blackypaw.mc.i18n.InterceptorBase;
import com.blackypaw.mc.i18n.chat.ChatComponent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Locale;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class InterceptorSign extends InterceptorBase {
	
	public InterceptorSign( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
		super( plugin, gson, i18n, ListenerPriority.LOWEST, PacketType.Play.Server.UPDATE_SIGN );
	}
	
	@Override
	public void onPacketSending( PacketEvent event ) {
		final Player          player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		
		// Translate all four lines if necessary:
		boolean                changed        = false;
		WrappedChatComponent[] chatComponents = packet.getChatComponentArrays().read( 0 );
		for ( int i = 0; i < chatComponents.length; ++i ) {
			WrappedChatComponent chat = chatComponents[i];
			if ( chat != null ) {
				String message    = this.restoreTextFromChatComponent( chat );
				String translated = this.translateMessageIfAppropriate( this.i18n.getLocale( player.getUniqueId() ), message );
				
				if ( message != translated ) {
					chatComponents[i] = WrappedChatComponent.fromText( translated );
					changed = true;
				}
			}
		}
		
		if ( changed ) {
			// Only write back when really needed:
			packet.getChatComponentArrays().write( 0, chatComponents );
		}
	}
	
}
