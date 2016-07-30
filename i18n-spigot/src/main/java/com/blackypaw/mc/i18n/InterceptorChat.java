/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.ComponentConverter;
import com.google.gson.Gson;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class InterceptorChat extends InterceptorBase {
	
	InterceptorChat( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
		super( plugin, gson, i18n, ListenerPriority.LOWEST, PacketType.Play.Server.CHAT );
	}
	
	@Override
	public void onPacketSending( PacketEvent event ) {
		final Player                  player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		
		String message     = this.restoreTextFromChatComponent( packet.getChatComponents().read( 0 ) );
		String translation = this.translateMessageIfAppropriate( this.i18n.getPlayerLocale( player.getUniqueId() ), message );
		
		if ( message != translation ) {
			// Yes, the test of instance equality is what I want to do here
			// as it saves me time determining whether the message was actually
			// translated or not:
			
			// Issue #4:
			//  https://github.com/BlackyPaw/I18N/issues/4
			//
			// Do this ugly conversion thing here in order to enforce usage of JSON color tags
			// over the legacy chat format. Invoking WrappedChatComponent.fromText( ... ) for
			// example, would result in a JSON such as {"text":"§cExample"} which can, if long enough
			// and automatically put onto a new line by Minecraft, lose its formatting on the addtional
			// line of chat. This issue does only arise for chat currently as it is the only place where
			// multiple lines are even supported. In case this issue should arise again somewhere else
			// one will simply have to do this conversion there, too:
			packet.getChatComponents().write( 0, ComponentConverter.fromBaseComponent( TextComponent.fromLegacyText( translation ) ) );
		}
	}
	
}
