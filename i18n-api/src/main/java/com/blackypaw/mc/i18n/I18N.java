/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.Locale;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public interface I18N<KeyType> {
	
	boolean isInjectionSupported();
	Localizer createLocalizer( TranslationStorage storage );
	
	Locale getPlayerLocale( KeyType key );
	boolean trySetPlayerLocale( KeyType key, Locale locale );
	
	void setLocaleResolver( LocaleResolver<KeyType> resolver );
	boolean shouldUseFallbackLocale();
	Locale getFallbackLocale();
	
}
