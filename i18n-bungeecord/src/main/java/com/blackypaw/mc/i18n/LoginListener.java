/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Locale;

/**
 * Implementation related class. Do not use under any circumstances - the only reason
 * this is even public is that BungeeCord does not attempt to invoke .setAccessible( true )
 * when reflecting event handlers, presumably because it does not want to get into trouble
 * with security managers, and thus requires both the class as well as the method handling
 * the actual event to be public.
 * <p>
 * TL;DR : Stay away!
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class LoginListener implements Listener {
	
	private final I18NBungeeCordImpl i18n;
	
	public LoginListener( I18NBungeeCordImpl i18n ) {
		this.i18n = i18n;
	}
	
	// Preamble:
	// This is just so utterly ugly - because of the fact that there is no reliable way to
	// tell whether or not another plugin might have cancelled the login event we must check
	// whether or not all the pending connections we store a reference to internally
	// are actually still connected and we must then remove them from our internal list if
	// they are not.
	
	@EventHandler( priority = EventPriority.LOWEST )
	public void onLogin( LoginEvent event ) {
		PendingConnection player = event.getConnection();
		Locale            locale = this.i18n.getLocaleResolver().resolveLocale( player.getUniqueId() );
		this.i18n.watchConnection( player );
		this.i18n.storeLocale( player.getUniqueId(), locale );
	}
	
}
