/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n.interceptor.v1_9_2;

import com.blackypaw.mc.i18n.I18NSpigotImpl;
import com.blackypaw.mc.i18n.InterceptorBase;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class InterceptorTitle extends InterceptorBase {
	
	public InterceptorTitle( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
		super( plugin, gson, i18n, ListenerPriority.LOWEST, PacketType.Play.Server.TITLE );
	}
	
	@Override
	public void onPacketSending( PacketEvent event ) {
		final Player          player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		
		EnumWrappers.TitleAction action = packet.getTitleActions().read( 0 );
		if ( action == EnumWrappers.TitleAction.TITLE || action == EnumWrappers.TitleAction.SUBTITLE ) {
			String message     = this.restoreTextFromChatComponent( packet.getChatComponents().read( 0 ) );
			String translation = this.translateMessageIfAppropriate( this.i18n.getLocale( player.getUniqueId() ), message );
			
			if ( message != translation ) {
				packet.getChatComponents().write( 0, WrappedChatComponent.fromText( translation ) );
			}
		}
	}
	
}
