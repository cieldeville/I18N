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
import com.blackypaw.mc.i18n.event.PlayerSetLanguageEvent;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Main class of the I18N utility plugin. Provides factory methods for localizers as well as a
 * small API for configuring the I18N plugin programmatically.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class I18NUtilities extends JavaPlugin {

	private static I18NUtilities instance;

	/**
	 * Constructs a new localizer instance which will be valid until the I18N plugins gets disabled.
	 * The translation storage which will be used for looking up translation keys / key hashes must
	 * be specified. In order to prevent heavy load during runtime you should consider disabling lazy
	 * loading for the storage and rather load all supported languages manually.
	 *
	 * @param storage The translation storage to be used for translation lookups
	 *
	 * @return The created localizer instance
	 */
	public static Localizer createLocalizer( TranslationStorage storage ) {
		return instance.factory.createInstance( storage );
	}

	/**
	 * Sets the locale resolver to be used for querying player locales. Should be set before the
	 * first player joins. Use with caution: multiple plugins might try to change the locale resolver
	 * so unless you really do need to use your own locale resolver you should first check if one
	 * of the default resolvers suits your needs.
	 *
	 * @param resolver The locale resolver to be used for detecting player locales
	 */
	public static void setLocaleResolver( LocaleResolver resolver ) {
		instance.destroyLocaleResolver();
		instance.resolver = resolver;
	}

	/**
	 * Gets the fallback locale configured by the user.
	 *
	 * @return The fallback locale configured by the user
	 */
	public static Locale getFallbackLocale() {
		return instance.fallbackLocale;
	}

	/**
	 * Gets whether or not the fallback locale should be used for translations in case a given
	 * translation key is not available in a certain player's language or the player's language
	 * could not be loaded for some reason.
	 *
	 * @return Whether or not the fallback locale should be used under the circumstances explained above
	 */
	public static boolean shouldUseFallbackLocale() {
		return instance.config.isUseFallbackLocale();
	}

	/**
	 * Gets the locale that is currently set for the player.
	 *
	 * @param player The player to get the locale of
	 *
	 * @return The locale of the given player
	 */
	public static Locale getPlayerLocale( Player player ) {
		return instance.getLocale( player );
	}

	/**
	 * Attempts to change the given player's locale. On success the method should return true, on failure
	 * it should return false. If the call succeeds the player will receive messages in the new language
	 * from this point on.
	 *
	 * @param player The player to set the locale of
	 * @param locale The locale to set
	 *
	 * @return Whether or not the locale could be changed successfully
	 */
	public static boolean trySetPlayerLocale( Player player, Locale locale ) {
		if ( instance.resolver.trySetPlayerLocale( player, locale ) ) {
			instance.storeLocale( player, locale );

			// Call a player set language event so that other plugins can resend
			// scoreboards, signs, etc.:
			PlayerSetLanguageEvent event = new PlayerSetLanguageEvent( player, locale );
			Bukkit.getServer().getPluginManager().callEvent( event );

			return true;
		}
		return false;
	}

	private PluginConfig config;

	private Locale            fallbackLocale;
	private LocalizerFactory  factory;
	private LocaleResolver    resolver;
	private Map<UUID, Locale> localeStorage;

	private Gson gson;

	// Internal:
	private Localizer commonLocalizer;

	@Override
	public void onEnable() {
		instance = this;
		PluginManager pluginManager = this.getServer().getPluginManager();

		// Ensure the plugin directory exists:
		final File pluginDirectory = this.getDataFolder();
		if ( !pluginDirectory.exists() ) {
			if ( !pluginDirectory.mkdirs() ) {
				this.getLogger().severe( "Failed to create Plugin Data Folder; please double-check your filesystem permissions!" );
				pluginManager.disablePlugin( this );
				return;
			}
		} else if ( !pluginDirectory.isDirectory() ) {
			this.getLogger().severe( "Plugin Data Folder is not a directory!" );
			pluginManager.disablePlugin( this );
			return;
		}

		// Ensure the translation directory exists:
		final File translationsDirectory = new File( pluginDirectory, "translations" );
		if ( !translationsDirectory.exists() ) {
			if ( !translationsDirectory.mkdirs() ) {
				this.getLogger().severe( "Failed to create Translations Folder; please double-check your filesystem permissions!" );
				pluginManager.disablePlugin( this );
				return;
			}
		} else if ( !translationsDirectory.isDirectory() ) {
			this.getLogger().severe( "Translations Folder is not a directory!" );
			pluginManager.disablePlugin( this );
			return;
		}

		// Load the configuration:
		this.config = new PluginConfig();
		try {
			this.config.initialize( new File( pluginDirectory, "config.cfg" ) );
		} catch ( IOException e ) {
			e.printStackTrace();
			this.getLogger().severe( "Failed to load / create configuration file; please double-check your filesystem permissions!" );
			pluginManager.disablePlugin( this );
			return;
		}

		this.fallbackLocale = new Locale( this.config.getFallbackLocale() );
		this.factory = new LocalizerFactory( this );
		switch ( this.config.getDefaultLocaleResolver() ) {
			case "CONSTANT":
				this.resolver = new ConstantLocaleResolver( this.fallbackLocale );
				break;
			case "DATABASE":
				try {
					this.resolver = new DatabaseLocaleResolver( this.config.getDbHost(), this.config.getDbPort(), this.config.getDbUser(), this.config.getDbPassword(), this.config.getDbName(), this.config.getDbPrefix() );
				} catch ( SQLException e ) {
					e.printStackTrace();
					this.getLogger().severe( "Failed to connect to MySQL instance" );
					pluginManager.disablePlugin( this );
					return;
				}
				break;
			default:
				this.getLogger().severe( "Unknown default locale resolver; please use one of the documented constants!" );
				pluginManager.disablePlugin( this );
				return;
		}
		this.localeStorage = new HashMap<>();

		// Print name of fallback locale; will trigger load of iso639 files:
		this.getLogger().info( "Set fallback locale to " + ISO639.getName( this.fallbackLocale.getLanguage() ) );

		// Prepare common localizer for plugin's own use:
		this.commonLocalizer = createLocalizer( this.prepareCommonStorage( translationsDirectory ) );

		// Create Gson instance used for interception of JSON formatted messages:
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter( ChatComponent.class, new ChatComponentDeserializer() );
		this.gson = gsonBuilder.create();

		// Register listeners:
		pluginManager.registerEvents( new PlayerLoginListener( this ), this );
		pluginManager.registerEvents( new PlayerQuitListener( this ), this );

		// Register commands:
		this.getCommand( "language" ).setExecutor( new CommandLanguage( this, this.commonLocalizer, this.config.isUseNativeLanguageNames() ) );

		this.installInterceptors();

		new PacketListener();
	}

	@Override
	public void onDisable() {
		this.destroyLocaleResolver();

		this.factory.dispose();
		this.factory = null;

		instance = null;
	}

	// ============================================= INTERNAL ACCESSORS ============================================= //

	/**
	 * Gets the locale resolver used internally for retrieving a player's locale.
	 *
	 * @return The locale resolver to be used for retrieving a player's locale
	 */
	LocaleResolver getLocaleResolver() {
		return this.resolver;
	}

	/**
	 * Stores a player's locale into the cache. May be used to overwrite the cached locale.
	 *
	 * @param player The player to cache a locale for
	 * @param locale The actual locale to be cached
	 */
	void storeLocale( Player player, Locale locale ) {
		this.localeStorage.put( player.getUniqueId(), locale );
	}

	/**
	 * Removes a player's locale from the cache.
	 *
	 * @param player The player whose locale should be removed from the cache
	 */
	void unstoreLocale( Player player ) {
		this.localeStorage.remove( player.getUniqueId() );
	}

	/**
	 * Gets the cached locale of the given player.
	 *
	 * @param player The player to get the locale of
	 *
	 * @return The player's locale if cached or the fallback locale if no cached locale exists
	 */
	Locale getLocale( Player player ) {
		Locale locale = this.localeStorage.get( player.getUniqueId() );
		if ( locale == null ) {
			return this.fallbackLocale;
		}
		return locale;
	}

	// ============================================= INTERNATIONALIZATION ============================================= //

	private TranslationStorage prepareCommonStorage( File translationDirectory ) {
		PropertyTranslationStorage storage = new PropertyTranslationStorage( translationDirectory, this.config.isAllowUserTranslations() );
		this.tryLoadCommonLanguage( storage, Locale.ENGLISH );
		this.tryLoadCommonLanguage( storage, Locale.GERMAN );
		return storage;
	}

	private void tryLoadCommonLanguage( PropertyTranslationStorage storage, Locale locale ) {
		// Try to load the given common language from the plugin's classpath:
		try {
			try ( BufferedInputStream in = new BufferedInputStream( this.getClass().getResourceAsStream( "/translations/" + locale.getLanguage() + ".properties" ) ) ) {
				Properties properties = new Properties();
				properties.load( in );
				storage.loadLanguage( locale, properties );
			}
		} catch ( IOException ignored ) {
			this.getLogger().warning( "Failed to load common translation " + locale.getLanguage() );
		}
	}

	// ============================================= INTERCEPTION ============================================= //

	/**
	 * Installs all necessary packet interceptors required for intercepting packets containing text to be
	 * translated.
	 */
	private void installInterceptors() {
		final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

		this.installChatInterceptor( protocolManager );
		this.installTitleInterceptor( protocolManager );
		this.installScoreboardInterceptors( protocolManager );
		this.installSignInterceptor( protocolManager );
	}

	/**
	 * Installs the packet interceptor required for intercepting chat packets.
	 *
	 * @param protocolManager The protocol manager as handed out by protocol library
	 */
	private void installChatInterceptor( ProtocolManager protocolManager ) {
		final I18NUtilities self = this;

		protocolManager.addPacketListener( new PacketAdapter( this, ListenerPriority.LOWEST, PacketType.Play.Server.CHAT ) {
			@Override
			public void onPacketSending( PacketEvent event ) {
				final Player          player = event.getPlayer();
				final PacketContainer packet = event.getPacket();

				String message     = self.restoreTextFromChatComponent( packet.getChatComponents().read( 0 ) );
				String translation = self.translateMessageIfAppropriate( self.getLocale( player ), message );

				if ( message != translation ) {
					// Yes, the test of instance equality is what I want to do here
					// as it saves me time determining whether the message was actually
					// translated or not:
					packet.getChatComponents().write( 0, WrappedChatComponent.fromText( translation ) );
				}
			}
		} );
	}

	private void installTitleInterceptor( ProtocolManager protocolManager ) {
		final I18NUtilities self = this;

		protocolManager.addPacketListener( new PacketAdapter( this, ListenerPriority.LOWEST, PacketType.Play.Server.TITLE ) {
			@Override
			public void onPacketSending( PacketEvent event ) {
				final Player          player = event.getPlayer();
				final PacketContainer packet = event.getPacket();

				EnumWrappers.TitleAction action = packet.getTitleActions().read( 0 );
				if ( action == EnumWrappers.TitleAction.TITLE || action == EnumWrappers.TitleAction.SUBTITLE ) {
					String message     = self.restoreTextFromChatComponent( packet.getChatComponents().read( 0 ) );
					String translation = self.translateMessageIfAppropriate( self.getLocale( player ), message );

					if ( message != translation ) {
						packet.getChatComponents().write( 0, WrappedChatComponent.fromText( translation ) );
					}
				}
			}
		} );
	}

	private void installScoreboardInterceptors( ProtocolManager protocolManager ) {
		final I18NUtilities self = this;

		protocolManager.addPacketListener( new PacketAdapter( this, ListenerPriority.LOWEST, PacketType.Play.Server.SCOREBOARD_OBJECTIVE ) {
			@Override
			public void onPacketSending( PacketEvent event ) {
				final Player          player = event.getPlayer();
				final PacketContainer packet = event.getPacket();

				int mode = packet.getIntegers().read( 0 );
				if ( mode == 0 || mode == 2 ) {
					String message     = packet.getStrings().read( 1 );
					String translation = self.translateMessageIfAppropriate( self.getLocale( player ), message );

					if ( message != translation ) {
						packet.getStrings().write( 1, translation );
					}
				}
			}
		} );

		protocolManager.addPacketListener( new PacketAdapter( this, ListenerPriority.LOWEST, PacketType.Play.Server.SCOREBOARD_SCORE ) {
			@Override
			public void onPacketSending( PacketEvent event ) {
				final Player          player = event.getPlayer();
				final PacketContainer packet = event.getPacket();

				String message     = packet.getStrings().read( 0 );
				String translation = self.translateMessageIfAppropriate( self.getLocale( player ), message );

				if ( message != translation ) {
					packet.getStrings().write( 0, translation );
				}
			}
		} );

		protocolManager.addPacketListener( new PacketAdapter( this, ListenerPriority.LOWEST, PacketType.Play.Server.SCOREBOARD_TEAM ) {
			@Override
			public void onPacketSending( PacketEvent event ) {
				final Player          player = event.getPlayer();
				final PacketContainer packet = event.getPacket();
				final Locale          locale = self.getLocale( player );

				int mode = packet.getIntegers().read( 1 );
				if ( mode == 0 || mode == 2 ) {
					String displayName = packet.getStrings().read( 1 );
					String prefix      = packet.getStrings().read( 2 );
					String suffix      = packet.getStrings().read( 3 );

					String translatedDisplayName = self.translateMessageIfAppropriate( locale, displayName );
					String translatedPrefix      = self.translateMessageIfAppropriate( locale, prefix );
					String translatedSuffix      = self.translateMessageIfAppropriate( locale, suffix );

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
							String translatedEntry = self.translateMessageIfAppropriate( locale, entry );

							if ( entry != translatedEntry ) {
								entries.set( i, translatedEntry );
							}
						}
					}
				}
			}
		} );
	}

	private void installSignInterceptor( ProtocolManager protocolManager ) {
		final I18NUtilities self = this;

		protocolManager.addPacketListener( new PacketAdapter( this, ListenerPriority.LOWEST, PacketType.Play.Server.UPDATE_SIGN ) {
			@Override
			public void onPacketSending( PacketEvent event ) {
				final Player          player = event.getPlayer();
				final PacketContainer packet = event.getPacket();
				final Locale          locale = self.getLocale( player );

				// Translate all four lines if necessary:
				boolean changed = false;
				WrappedChatComponent[] chatComponents = packet.getChatComponentArrays().read( 0 );
				for ( int i = 0; i < chatComponents.length; ++i ) {
					WrappedChatComponent chat = chatComponents[i];
					if ( chat != null ) {
						String message = self.restoreTextFromChatComponent( chat );
						String translated = self.translateMessageIfAppropriate( locale, message );

						if ( message != translated ) {
							chatComponents[i] = WrappedChatComponent.fromText( translated );
							changed = true;
						}
					}
				}

				if ( changed ) {
					// Only write back when really needed:
					packet.getChatComponentArrays().write( 0, chatComponents );
				}
			}
		} );
	}

	private String restoreTextFromChatComponent( WrappedChatComponent component ) {
		return this.gson.fromJson( component.getJson(), ChatComponent.class ).getUnformattedText();
	}

	private String translateMessageIfAppropriate( Locale locale, String message ) {
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

	private String translateMessageImmediate( Locale locale, String message ) {
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

		Localizer localizer = this.factory.findInstance( localizerId );
		if ( localizer != null ) {
			return localizer.translateDirect( locale, keyHash );
		} else {
			return TranslationStorage.ENOLCLZR;
		}
	}

	private String translateMessageInjected( Locale locale, String message ) {
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

		Localizer localizer = this.factory.findInstance( localizerId );
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

	// ============================================= CLEANUP ============================================= //

	private void destroyLocaleResolver() {
		if ( this.resolver != null ) {
			if ( this.resolver instanceof DatabaseLocaleResolver ) {
				( (DatabaseLocaleResolver) this.resolver ).close();
			}
			this.resolver = null;
		}
	}

}
