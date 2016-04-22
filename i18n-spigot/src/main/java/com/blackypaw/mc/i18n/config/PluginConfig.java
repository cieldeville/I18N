/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n.config;

import com.blackypaw.simpleconfig.SimpleConfig;
import com.blackypaw.simpleconfig.annotation.Comment;

/**
 * SimpleConfig based configuration class. Exposes all configurable options of the I18N utiltities.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public class PluginConfig extends SimpleConfig {

	@Comment( "The fallback locale to use for players whose locale could not be resolved (language code only)" )
	private String fallbackLocale = "en";

	@Comment( "Whether or not failed translation attempts should be re-attempted using the fallback locale configured above" )
	private boolean useFallbackLocale = true;

	@Comment( "If set to true the localizer used by this plugin itself will be allowed to lazy load user translations inside the translations folder" )
	private boolean allowUserTranslations = true;

	@Comment( "If set to true the native names of languages will be displayed. If set to false their english names will be displayed instead" )
	private boolean useNativeLanguageNames = false;

	@Comment( "The locale resolver to be used per default unless another plugin changes it programmatically.\n" +
	          "May be one of the following constants:\n" +
	          "- CONSTANT : The fallback locale will be returned for all players\n" +
	          "- DATABASE : Player locales will be resolved from a MySQL database (slow)" )
	private String defaultLocaleResolver = "CONSTANT";

	@Comment( "For use with DATABASE locale resolver: Hostname of MySQL instance" )
	private String dbHost = "127.0.0.1";

	@Comment( "For use with DATABASE locale resolver: Port of MySQL instance" )
	private int dbPort = 3306;

	@Comment( "For use with DATABASE locale resolver: Username for MySQL authentication" )
	private String dbUser = "root";

	@Comment( "For use with DATABASE locale resolver: Password for MySQL authentication" )
	private String dbPassword = "qwertzy123456";

	@Comment( "For use with DATABASE locale resolver: Name of Database for MySQL instance" )
	private String dbName = "i18n";

	@Comment( "For use with DATABASE locale resolver: Name prefix for MySQL tables" )
	private String dbPrefix = "i18n_";

	// ============================================= ACCESSORS ============================================= //

	public String getFallbackLocale() {
		return fallbackLocale;
	}

	public boolean isUseFallbackLocale() {
		return useFallbackLocale;
	}

	public boolean isAllowUserTranslations() {
		return allowUserTranslations;
	}

	public boolean isUseNativeLanguageNames() {
		return useNativeLanguageNames;
	}

	public String getDefaultLocaleResolver() {
		return defaultLocaleResolver;
	}

	public String getDbHost() {
		return dbHost;
	}

	public int getDbPort() {
		return dbPort;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbPrefix() {
		return dbPrefix;
	}

}
