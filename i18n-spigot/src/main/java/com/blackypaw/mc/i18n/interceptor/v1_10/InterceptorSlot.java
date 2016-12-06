/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n.interceptor.v1_10;

import com.blackypaw.mc.i18n.I18NSpigotImpl;
import com.blackypaw.mc.i18n.InterceptorBase;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.google.gson.Gson;
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
public class InterceptorSlot extends InterceptorBase {
	
	public InterceptorSlot( Plugin plugin, Gson gson, I18NSpigotImpl i18n ) {
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
		final Player          player   = event.getPlayer();
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
		
		boolean     changed = false;
		ItemStack[] items   = packet.getItemArrayModifier().read( 0 );
		if ( items != null ) {
			for ( int i = 0; i < items.length; ++i ) {
				ItemStack stack = items[i];
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
						items = Arrays.copyOf( items, items.length );
					}
					
					// Got to clone the item stack here in order not to modify its original
					// reference as it might be in use by the actual inventory:
					stack = stack.clone();
					meta = stack.getItemMeta();
					meta.setDisplayName( translated );
					stack.setItemMeta( meta );
					items[i] = stack;
					
					changed = true;
				}
			}
			
			if ( changed ) {
				// Only write back when really needed:
				packet.getItemArrayModifier().write( 0, items );
			}
		}
	}
	
}
