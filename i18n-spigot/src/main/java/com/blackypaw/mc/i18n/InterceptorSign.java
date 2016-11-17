/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

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
class InterceptorSign extends InterceptorBase {
	
	InterceptorSign( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
		super( plugin, gson, i18n, ListenerPriority.LOWEST, PacketType.Play.Server.TILE_ENTITY_DATA, PacketType.Play.Server.MAP_CHUNK );
	}
	
	@Override
	public void onPacketSending( PacketEvent event ) {
		if ( event.getPacketType() == PacketType.Play.Server.TILE_ENTITY_DATA ) {
			this.onTileEntityData( event );
		} else if ( event.getPacketType() == PacketType.Play.Server.MAP_CHUNK ) {
			this.onMapChunk( event );
		}
	}
	
	private void onTileEntityData( PacketEvent event ) {
		final Player                  player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		final Locale          locale = this.i18n.getLocale( player.getUniqueId() );
		
		if ( packet.getIntegers().read( 0 ) == 0x09 ) {
			// Update Block Entity -> Set Sign Text:
			NbtBase<?> nbt = packet.getNbtModifier().read( 0 );
			if ( nbt.getType() != NbtType.TAG_COMPOUND ) {
				// Malformed Sign Entity:
				return;
			}
			
			boolean     changed  = false;
			NbtCompound compound = (NbtCompound) nbt;
			for ( int i = 1; i <= 4; ++i ) {
				final String key = "Text" + i;
				String message    = this.gson.fromJson( compound.getString( key ), ChatComponent.class ).getUnformattedText();
				String translated = this.translateMessageIfAppropriate( locale, message );
				
				if ( message != translated ) {
					if ( !changed ) {
						nbt = compound.deepClone();
						compound = (NbtCompound) nbt;
					}
					compound.put( key, WrappedChatComponent.fromText( translated ).getJson() );
					changed = true;
				}
			}
			
			if ( changed ) {
				packet.getNbtModifier().write( 0, nbt );
			}
		}
	}
	
	private void onMapChunk( PacketEvent event ) {
		final Player          player = event.getPlayer();
		final PacketContainer packet = event.getPacket();
		final Locale          locale = this.i18n.getLocale( player.getUniqueId() );
		
		List handleList = packet.getSpecificModifier( List.class ).read( 0 );
		for ( Object compoundHandle : handleList ) {
			NbtCompound compound = NbtFactory.fromNMSCompound( compoundHandle );
			if ( compound.getString( "id" ).equals( "minecraft:sign" ) ) {
				for ( int i = 1; i <= 4; ++i ) {
					final String key = "Text" + i;
					String message    = this.gson.fromJson( compound.getString( key ), ChatComponent.class ).getUnformattedText();
					String translated = this.translateMessageIfAppropriate( locale, message );
					
					if ( message != translated ) {
						compound.put( key, WrappedChatComponent.fromText( translated ).getJson() );
					}
				}
			}
		}
	}
	
}
