/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import com.blackypaw.mc.i18n.chat.ChatComponent;
import com.blackypaw.mc.i18n.chat.ChatComponentDeserializer;
import com.blackypaw.mc.i18n.command.CommandLanguage;
import com.blackypaw.mc.i18n.config.PluginConfig;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Adapter class providing an I18N implementation for the Spigot platform.
 * <p>
 * This class is the entry pointer for accessing the I18N library when using Spigot. Use the static method
 * {@link #getI18N()} to get an I18N implementation that is conform with Spigot and is automated in regards
 * to resolving player locales on login and unloading them as well as injection handling.
 * <p>
 * The I18N instance returned by this adapter is injection aware.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class I18NSpigotAdapter extends JavaPlugin {
	
	private static I18NSpigotAdapter adapter;
	
	private PluginConfig   config;
	private Localizer      commonLocalizer;
	private I18NSpigotImpl i18n;
	
	/**
	 * Returns the actual I18N interface implementation provided by this adapter.
	 *
	 * @return The actual I18N interface implementation provided by this adapter
	 */
	public static InjectionAwareI18N<UUID> getI18N() {
		return adapter.i18n;
	}
	
	@Override
	public void onEnable() {
		adapter = this;
		
		this.createPluginDirectory();
		this.createTranslationsDirectory();
		this.loadPlatformConfig();
		this.constructI18NImplementation();
		this.prepareLocalizer();
		this.registerCommands();
		this.registerListeners();
		this.installInterceptors();
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
			this.getLogger().log( Level.SEVERE, "Failed to load / create platform-dependant configuration files from file-system; please double-check your file-system permissions!", e );
		}
	}
	
	private void constructI18NImplementation() {
		this.i18n = new I18NSpigotImpl( this.getLogger() );
		if ( !this.i18n.initializeFromConfig( this.config ) ) {
			this.getLogger().log( Level.SEVERE, "Failed to create I18N implementation: could not initialize from configuration" );
		}
	}
	
	private void prepareLocalizer() {
		PropertyTranslationStorage storage = new PropertyTranslationStorage( this.i18n, new File( this.getDataFolder(), "translations" ), this.config.isAllowUserTranslations() );
		this.tryLoadCommonLanguage( storage, Locale.ENGLISH );
		this.tryLoadCommonLanguage( storage, Locale.GERMAN );
		this.tryLoadCommonLanguage( storage, new Locale( "es" ) );
		this.tryLoadCommonLanguage( storage, new Locale( "eu" ) );
		this.commonLocalizer = this.i18n.createLocalizer( storage );
	}
	
	private void tryLoadCommonLanguage( PropertyTranslationStorage storage, Locale locale ) {
		// Try to load the given common language from the plugin's classpath:
		try {
			try ( Reader in = new BufferedReader( new InputStreamReader( new BufferedInputStream( this.getClass().getResourceAsStream( "/translations/" + locale.getLanguage() + ".properties" ) ), StandardCharsets.UTF_8 ) ) ) {
				Properties properties = new Properties();
				properties.load( in );
				storage.loadLanguage( locale, properties );
			}
		} catch ( IOException ignored ) {
			this.getLogger().warning( "Failed to load common translation " + locale.getLanguage() );
		}
	}
	
	private void registerCommands() {
		this.getCommand( "language" ).setExecutor( new CommandLanguage( this.i18n, this.commonLocalizer, this.config.isUseNativeLanguageNames() ) );
	}
	
	private void registerListeners() {
		final PluginManager pluginManager = this.getServer().getPluginManager();
		pluginManager.registerEvents( new PlayerLoginListener( this.i18n ), this );
		pluginManager.registerEvents( new PlayerQuitListener( this.i18n ), this );
	}
	
	private void installInterceptors() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter( ChatComponent.class, new ChatComponentDeserializer() );
		Gson gson = gsonBuilder.create();
		
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener( new InterceptorChat( this, gson, this.i18n ) );
		protocolManager.addPacketListener( new InterceptorTitle( this, gson, this.i18n ) );
		protocolManager.addPacketListener( new InterceptorScoreboard( this, gson, this.i18n ) );
		protocolManager.addPacketListener( new InterceptorSign( this, gson, this.i18n ) );
		protocolManager.addPacketListener( new InterceptorSettings( this, gson, this.i18n ) );
		protocolManager.addPacketListener( new InterceptorSlot( this, gson, this.i18n ) );
	}
	
}
