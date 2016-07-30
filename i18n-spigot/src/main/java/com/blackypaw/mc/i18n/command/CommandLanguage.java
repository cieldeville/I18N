/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n.command;

import com.blackypaw.mc.i18n.I18N;
import com.blackypaw.mc.i18n.ISO639;
import com.blackypaw.mc.i18n.Localizer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

/**
 * Command executor for the /language command.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class CommandLanguage implements CommandExecutor {
	
	private final I18N<UUID> i18n;
	private final Localizer  commonLocalizer;
	private final boolean    showNativeNames;
	
	public CommandLanguage( final I18N<UUID> i18n, final Localizer commonLocalizer, boolean showNativeNames ) {
		this.i18n = i18n;
		this.commonLocalizer = commonLocalizer;
		this.showNativeNames = showNativeNames;
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if ( !( sender instanceof Player ) ) {
			sender.sendMessage( ChatColor.RED + "This command may only be used by players!" );
			return true;
		}
		
		Player player = (Player) sender;
		Locale locale = this.i18n.getPlayerLocale( player.getUniqueId() );
		
		if ( args.length > 1 ) {
			player.sendMessage( this.commonLocalizer.translateDirect( locale, "com.blackypaw.mc.i18n.command.language.syntax" ) );
			return true;
		}
		
		if ( args.length == 0 ) {
			// Display current locale:
			player.sendMessage( this.commonLocalizer.translateDirect( locale, "com.blackypaw.mc.i18n.command.language.display", this.getLanguageName( locale.getLanguage() ) ) );
		} else if ( args.length == 1 ) {
			// Try to set current locale:
			
			// Match ISO language code:
			String iso = args[0];
			if ( !iso.matches( "^[a-zA-Z]{2}$" ) ) {
				player.sendMessage( this.commonLocalizer.translateDirect( locale, "com.blackypaw.mc.i18n.command.language.fail" ) );
				return true;
			}
			
			Locale newLocale = new Locale( iso );
			if ( !this.i18n.trySetPlayerLocale( player.getUniqueId(), newLocale ) ) {
				player.sendMessage( this.commonLocalizer.translateDirect( locale, "com.blackypaw.mc.i18n.command.language.fail" ) );
				return true;
			}
			
			player.sendMessage( this.commonLocalizer.translateDirect( newLocale, "com.blackypaw.mc.i18n.command.language.set", this.getLanguageName( newLocale.getLanguage() ) ) );
		}
		return true;
	}
	
	private String getLanguageName( String isoCode ) {
		String name;
		if ( this.showNativeNames ) {
			name = ISO639.getNativeName( isoCode );
		} else {
			name = ISO639.getName( isoCode );
		}
		
		if ( name == null ) {
			name = "Unknown";
		}
		return name;
	}
	
}
