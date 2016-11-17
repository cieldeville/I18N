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
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class InterceptorSlot extends InterceptorBase {
	
	InterceptorSlot( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
		super( plugin, gson, i18n, ListenerPriority.LOWEST, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS );
	}
	
	@Override
	public void onPacketSending( PacketEvent event ) {
		if ( event.getPacketType() == PacketType.Play.Server.SET_SLOT ) {
			this.onSetSlot( event );
		} else if ( event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS ) {
			this.onWindowItems( event );
		}
	}
	
	private void onSetSlot( PacketEvent event ) {
		final Player                  player   = event.getPlayer();
		final PacketContainer packet   = event.getPacket();
		final Locale          language = this.i18n.getLocale( player.getUniqueId() );
		
		ItemStack stack = packet.getItemModifier().read( 0 );
		if ( stack != null ) {
			ItemMeta meta = stack.getItemMeta();
			if ( meta == null ) {
				return;
			}
			String message = meta.getDisplayName();
			if ( message == null ) {
				return;
			}
			
			//self.getLogger().info( "#SetSlot: Message of Item = " + message );
			String translated = this.translateMessageIfAppropriate( language, message );
			
			if ( message != translated ) {
				// Only write back when really needed:
				
				// Got to clone here as otherwise we might be modifying an instance that
				// is actually also used by the inventory:
				stack = stack.clone();
				meta = stack.getItemMeta();
				meta.setDisplayName( translated );
				stack.setItemMeta( meta );
				packet.getItemModifier().write( 0, stack );
			}
		}
	}
	
	private void onWindowItems( PacketEvent event ) {
		final Player          player   = event.getPlayer();
		final PacketContainer packet   = event.getPacket();
		final Locale          language = this.i18n.getLocale( player.getUniqueId() );
		
		final EquivalentConverter<ItemStack> converter = BukkitConverters.getItemStackConverter();
		
		boolean     changed = false;
		List        items   = packet.getSpecificModifier( List.class ).read( 0 );
		if ( items != null ) {
			for ( int i = 0; i < items.size(); ++i ) {
				// Convert NMS ItemStacks to Bukkit:
				ItemStack stack = converter.getSpecific( items.get( i ) );
				if ( stack == null ) {
					continue;
				}
				ItemMeta meta = stack.getItemMeta();
				if ( meta == null ) {
					continue;
				}
				String message = meta.getDisplayName();
				if ( message == null ) {
					continue;
				}
				
				//self.getLogger().info( "#WindowItems: Message of Item = " + message );
				String translated = this.translateMessageIfAppropriate( language, message );
				
				if ( message != translated ) {
					// Got to localize the item's display name:
					
					if ( !changed ) {
						// Construct a shallow clone of the array as we do NOT want
						// to overwrite the original stack with the contents we modified:
						items = new ArrayList( items );
					}
					
					// Got to clone the item stack here in order not to modify its original
					// reference as it might be in use by the actual inventory:
					stack = stack.clone();
					meta = stack.getItemMeta();
					meta.setDisplayName( translated );
					stack.setItemMeta( meta );
					
					// Convert Bukkit ItemStack to NMS:
					items.set( i, converter.getGeneric( converter.getSpecificType(), stack ) );
					
					changed = true;
				}
			}
			
			if ( changed ) {
				// Only write back when really needed:
				packet.getSpecificModifier( List.class ).write( 0, items );
			}
		}
	}
	
}
