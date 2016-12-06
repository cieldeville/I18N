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
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class InterceptorBase extends PacketAdapter {
	
	protected final Gson           gson;
	protected final I18NSpigotImpl i18n;
	
	protected InterceptorBase( Plugin plugin, Gson gson, I18NSpigotImpl i18n, ListenerPriority priority, PacketType... types ) {
		super( plugin, priority, types );
		this.gson = gson;
		this.i18n = i18n;
	}
	
	protected String restoreTextFromChatComponent( WrappedChatComponent component ) {
		return this.gson.fromJson( component.getJson(), ChatComponent.class ).getUnformattedText();
	}
	
	protected String translateMessageIfAppropriate( Locale locale, String message ) {
		if ( message.startsWith( "trns" ) ) {
			// Immediate translation:
			return this.translateMessageImmediate( locale, message );
		} else if ( message.startsWith( "injc" ) ) {
			// Injection handle translation:
			return this.translateMessageInjected( locale, message );
		}
		// No translation injection detected:
		return message;
	}
	
	protected String translateMessageImmediate( Locale locale, String message ) {
		int localizerId = 0;
		localizerId |= ( ( (int) message.charAt( 4 ) ) & 0xFF ) << 24;
		localizerId |= ( ( (int) message.charAt( 5 ) ) & 0xFF ) << 16;
		localizerId |= ( ( (int) message.charAt( 6 ) ) & 0xFF ) << 8;
		localizerId |= ( ( (int) message.charAt( 7 ) ) & 0xFF );
		
		int keyHash = 0;
		keyHash |= ( ( (int) message.charAt( 8 ) ) & 0xFF ) << 24;
		keyHash |= ( ( (int) message.charAt( 9 ) ) & 0xFF ) << 16;
		keyHash |= ( ( (int) message.charAt( 10 ) ) & 0xFF ) << 8;
		keyHash |= ( ( (int) message.charAt( 11 ) ) & 0xFF );
		
		Localizer localizer = this.i18n.getLocalizerFactory().findInstance( localizerId );
		if ( localizer != null ) {
			return localizer.translateDirect( locale, keyHash );
		} else {
			return TranslationStorage.ENOLCLZR;
		}
	}
	
	protected String translateMessageInjected( Locale locale, String message ) {
		int localizerId = 0;
		localizerId |= ( ( (int) message.charAt( 4 ) ) & 0xFF ) << 24;
		localizerId |= ( ( (int) message.charAt( 5 ) ) & 0xFF ) << 16;
		localizerId |= ( ( (int) message.charAt( 6 ) ) & 0xFF ) << 8;
		localizerId |= ( ( (int) message.charAt( 7 ) ) & 0xFF );
		
		int injectionId = 0;
		injectionId |= ( ( (int) message.charAt( 8 ) ) & 0xFF ) << 24;
		injectionId |= ( ( (int) message.charAt( 9 ) ) & 0xFF ) << 16;
		injectionId |= ( ( (int) message.charAt( 10 ) ) & 0xFF ) << 8;
		injectionId |= ( ( (int) message.charAt( 11 ) ) & 0xFF );
		
		InjectionAwareLocalizer localizer = this.i18n.getLocalizerFactory().findInstance( localizerId );
		if ( localizer != null ) {
			InjectionHandle handle = localizer.resolveInjectionHandle( injectionId );
			if ( handle != null ) {
				return localizer.translateDirect( locale, handle.getKey(), handle.getArgs() );
			} else {
				return TranslationStorage.ENOINJHD;
			}
		} else {
			return TranslationStorage.ENOLCLZR;
		}
	}
	
}
