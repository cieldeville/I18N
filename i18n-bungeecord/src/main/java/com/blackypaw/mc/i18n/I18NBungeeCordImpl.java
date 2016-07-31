/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import com.blackypaw.mc.i18n.config.PluginConfig;
import com.blackypaw.mc.i18n.event.PlayerSetLanguageEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author BlackyPaw
 * @version 1.0
 */
class I18NBungeeCordImpl implements I18N<UUID> {
	
	private final Logger logger;
	
	private Locale               fallbackLocale;
	private LocaleResolver<UUID> localeResolver;
	private Map<UUID, Locale>    localeStorage;
	private boolean              shouldUseFallbackLocale;
	
	private Set<PendingConnection> watchedConnections;
	
	
	I18NBungeeCordImpl( Logger logger ) {
		this.logger = logger;
		this.watchedConnections = new HashSet<>();
	}
	
	@Override
	public boolean isInjectionSupported() {
		// Injection is not supported on BungeeCord:
		return false;
	}
	
	@Override
	public Localizer createLocalizer( TranslationStorage storage ) {
		return new LocalizerBungeeCordImpl( storage );
	}
	
	@Override
	public Locale getLocale( UUID key ) {
		return this.localeStorage.get( key );
	}
	
	@Override
	public boolean trySetLocale( UUID key, Locale locale ) {
		if ( this.localeResolver.trySetPlayerLocale( key, locale ) ) {
			this.storeLocale( key, locale );
			
			// Call a player set language event so that other plugins can resend
			// scoreboards, signs, etc.:
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer( key );
			if ( player != null ) {
				PlayerSetLanguageEvent event = new PlayerSetLanguageEvent( player, locale );
				ProxyServer.getInstance().getPluginManager().callEvent( event );
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void setLocaleResolver( LocaleResolver<UUID> resolver ) {
		this.destroyLocaleResolver();
		this.localeResolver = resolver;
	}
	
	@Override
	public boolean shouldUseFallbackLocale() {
		return this.shouldUseFallbackLocale;
	}
	
	@Override
	public Locale getFallbackLocale() {
		return this.fallbackLocale;
	}
	
	public boolean initializeFromConfig( PluginConfig config ) {
		switch ( config.getDefaultLocaleResolver().toUpperCase() ) {
			case "CONSTANT":
				this.localeResolver = new ConstantLocaleResolver<>( this.fallbackLocale );
				break;
			case "DATABASE":
				try {
					this.localeResolver = new DatabaseLocaleResolver( this, config.getDbHost(), config.getDbPort(), config
							.getDbUser(), config.getDbPassword(), config.getDbName(), config.getDbPrefix() );
				} catch ( SQLException e ) {
					this.logger.log( Level.SEVERE, "Failed to connect to MySQL database", e );
					return false;
				}
				break;
			default:
				this.logger.log( Level.SEVERE, "Unknown locale resolver '" + config.getDefaultLocaleResolver() + "'; please check the documentation for your version for supported values" );
				return false;
		}
		
		this.fallbackLocale = new Locale( config.getFallbackLocale() );
		this.localeStorage = new ConcurrentHashMap<>();
		this.shouldUseFallbackLocale = config.isUseFallbackLocale();
		
		this.logger.info( "Set fallback locale to " + ISO639.getName( this.fallbackLocale.getLanguage() ) );
		return true;
	}
	
	public void close() {
		this.destroyLocaleResolver();
	}
	
	LocaleResolver<UUID> getLocaleResolver() {
		return this.localeResolver;
	}
	
	void storeLocale( UUID key, Locale locale ) {
		this.localeStorage.put( key, locale );
	}
	
	void watchConnection( PendingConnection connection ) {
		synchronized ( this.watchedConnections ) {
			this.watchedConnections.add( connection );
		}
	}
	
	void unwatchConnection( PendingConnection connection ) {
		synchronized ( this.watchedConnections ) {
			this.watchedConnections.remove( connection );
		}
	}
	
	void checkWatchedConnections() {
		synchronized ( this.watchedConnections ) {
			Set<PendingConnection> removable = new HashSet<>();
			
			for ( PendingConnection connection : this.watchedConnections ) {
				if ( !connection.isConnected() ) {
					removable.add( connection );
					this.unstoreLocale( connection.getUniqueId() );
				}
			}
			
			this.watchedConnections.removeAll( removable );
		}
	}
	
	void unstoreLocale( UUID key ) {
		this.localeStorage.remove( key );
	}
	
	private void destroyLocaleResolver() {
		if ( this.localeResolver != null ) {
			if ( this.localeResolver instanceof AutoCloseable ) {
				try {
					( (AutoCloseable) this.localeResolver ).close();
				} catch ( Exception e ) {
					this.logger.log( Level.WARNING, "Failed to close AutoCloseable locale resolver", e );
				}
			}
			this.localeResolver = null;
		}
	}
	
}
