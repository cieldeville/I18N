/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import com.blackypaw.mc.i18n.config.PluginConfig;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public class I18NBungeeCordAdapter extends Plugin {
	
	private static I18NBungeeCordAdapter adapter;
	
	public static I18N<UUID> getI18N() {
		return adapter.i18n;
	}
	
	
	// Fields:
	private I18NBungeeCordImpl i18n;
	private PluginConfig       config;
	
	// ============================================= Lifecycle
	
	@Override
	public void onEnable() {
		adapter = this;
		
		this.createPluginDirectory();
		this.createTranslationsDirectory();
		this.loadPlatformConfig();
		this.constructI18NImplementation();
		this.registerListeners();
		this.setupConnectionWipeoutTask();
	}
	
	@Override
	public void onDisable() {
		if ( this.i18n != null ) {
			this.i18n.close();
		}
		
		this.i18n = null;
		adapter = null;
	}
	
	// ============================================= Initialization
	
	private void createPluginDirectory() {
		final File pluginDirectory = this.getDataFolder();
		if ( !pluginDirectory.exists() ) {
			if ( !pluginDirectory.mkdirs() ) {
				this.getLogger()
				    .warning( "Failed to create plugin data folder; please double-check your file-system permissions!" );
			}
		} else if ( !pluginDirectory.isDirectory() ) {
			this.getLogger()
			    .warning( "Plugin data folder is not a directory; please double-check your folder structure" );
		}
	}
	
	private void createTranslationsDirectory() {
		final File translationsDirectory = new File( this.getDataFolder(), "translations" );
		if ( !translationsDirectory.exists() ) {
			if ( !translationsDirectory.mkdirs() ) {
				this.getLogger()
				    .warning( "Failed to create translations folder; please double-check your file-system permissions!" );
			}
		} else if ( !translationsDirectory.isDirectory() ) {
			this.getLogger()
			    .warning( "Translations folder is not a directory; please double-check your folder structure" );
		}
	}
	
	private void loadPlatformConfig() {
		this.config = new PluginConfig();
		try {
			this.config.initialize( new File( this.getDataFolder(), "config.cfg" ) );
		} catch ( IOException e ) {
			this.getLogger()
			    .log( Level.SEVERE, "Failed to load / create platform-dependant configuration files from file-system; please double-check your file-system permissions!", e );
		}
	}
	
	private void constructI18NImplementation() {
		this.i18n = new I18NBungeeCordImpl( this.getLogger() );
		if ( !this.i18n.initializeFromConfig( this.config ) ) {
			this.getLogger().log( Level.SEVERE, "Failed to create I18N implementation: could not initialize from configuration" );
		}
	}
	
	private void registerListeners() {
		final PluginManager pluginManager = this.getProxy().getPluginManager();
		pluginManager.registerListener( this, new LoginListener( this.i18n ) );
		pluginManager.registerListener( this, new PlayerDisconnectListener( this.i18n ) );
	}
	
	private void setupConnectionWipeoutTask() {
		if ( this.config.getConnectionWipeoutInterval() < 0 ) {
			return;
		}
		
		this.getProxy().getScheduler().schedule( this, new Runnable() {
			@Override
			public void run() {
				I18NBungeeCordAdapter.this.i18n.checkWatchedConnections();
			}
		}, this.config.getConnectionWipeoutInterval(), this.config.getConnectionWipeoutInterval(), TimeUnit.MILLISECONDS );
	}
	
}
