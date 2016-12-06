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
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class InterceptorScoreboard extends InterceptorBase {
	
	public InterceptorScoreboard( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
		super( plugin, gson, i18n, ListenerPriority.LOWEST, PacketType.Play.Server.SCOREBOARD_OBJECTIVE, PacketType.Play.Server.SCOREBOARD_SCORE, PacketType.Play.Server.SCOREBOARD_TEAM );
	}
	
	@Override
	public void onPacketSending( PacketEvent event ) {
		if ( event.getPacketType() == PacketType.Play.Server.SCOREBOARD_OBJECTIVE ) {
			this.onScoreboardObjective( event );
		} else if ( event.getPacketType() == PacketType.Play.Server.SCOREBOARD_SCORE ) {
			this.onScoreboardScore( event );
		} else if ( event.getPacketType() == PacketType.Play.Server.SCOREBOARD_TEAM ) {
			this.onScoreboardTeam( event );
		}
	}
	
	private void onScoreboardObjective( PacketEvent event ) {
		final Player                  player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		
		int mode = packet.getIntegers().read( 0 );
		if ( mode == 0 || mode == 2 ) {
			String message     = packet.getStrings().read( 1 );
			String translation = this.translateMessageIfAppropriate( this.i18n.getLocale( player.getUniqueId() ), message );
			
			if ( message != translation ) {
				packet.getStrings().write( 1, translation );
			}
		}
	}
	
	private void onScoreboardScore( PacketEvent event ) {
		final Player          player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		
		String message     = packet.getStrings().read( 0 );
		String translation = this.translateMessageIfAppropriate( this.i18n.getLocale( player.getUniqueId() ), message );
		
		if ( message != translation ) {
			packet.getStrings().write( 0, translation );
		}
	}
	
	private void onScoreboardTeam( PacketEvent event ) {
		final Player                  player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		final Locale          locale = this.i18n.getLocale( player.getUniqueId() );
		
		int mode = packet.getIntegers().read( 1 );
		if ( mode == 0 || mode == 2 ) {
			String displayName = packet.getStrings().read( 1 );
			String prefix      = packet.getStrings().read( 2 );
			String suffix      = packet.getStrings().read( 3 );
			
			String translatedDisplayName = this.translateMessageIfAppropriate( locale, displayName );
			String translatedPrefix      = this.translateMessageIfAppropriate( locale, prefix );
			String translatedSuffix      = this.translateMessageIfAppropriate( locale, suffix );
			
			if ( displayName != translatedDisplayName ) {
				packet.getStrings().write( 1, translatedDisplayName );
			}
			
			if ( prefix != translatedPrefix ) {
				packet.getStrings().write( 2, translatedPrefix );
			}
			
			if ( suffix != translatedSuffix ) {
				packet.getStrings().write( 3, translatedSuffix );
			}
		}
		
		if ( mode == 0 || mode == 3 || mode == 4 ) {
			List<String> entries = (List<String>) packet.getSpecificModifier( Collection.class ).read( 0 );
			if ( entries.size() > 0 ) {
				for ( int i = 0; i < entries.size(); ++i ) {
					String entry           = entries.get( i );
					String translatedEntry = this.translateMessageIfAppropriate( locale, entry );
					
					if ( entry != translatedEntry ) {
						entries.set( i, translatedEntry );
					}
				}
			}
		}
	}
	
}
