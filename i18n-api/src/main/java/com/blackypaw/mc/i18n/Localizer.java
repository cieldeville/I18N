/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.Locale;

/**
 * A localizer may be used to translate messages stored in a TranslationStorage into actual
 * translated strings. It serves as the common denominator for systems which do not support
 * localization but only manual translation of messages. For platforms which do support
 * localization see {@link InjectionAwareLocalizer}.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public abstract class Localizer implements AutoCloseable {
	
	/**
	 * Translates a message directly using the translation storage the localizer was given
	 * during its creation. See {@link TranslationStorage#translate(Locale, String, Object...)}
	 * for a more thorough explanation.
	 *
	 * @param locale The language to translate into
	 * @param key The translation key of the message to be translated
	 * @param args Optional arguments to be inserted into the translation
	 *
	 * @return The translated string
	 */
	public abstract String translateDirect( Locale locale, String key, Object... args );
	
	/**
	 * See {@link #translateDirect(Locale, String, Object...)}
	 *
	 * @param locale The language to translate into
	 * @param keyHash The hash of the translation key of the message to be translated
	 * @param args Optional arguments to be inserted into the translation
	 *
	 * @return The translated string
	 */
	public abstract String translateDirect( Locale locale, int keyHash, Object... args );
	
}
