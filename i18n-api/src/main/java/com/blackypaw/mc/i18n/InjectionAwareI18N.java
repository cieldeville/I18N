/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

/**
 * Extension of the I18N interface which indicates support for localizers supporting injection
 * functionality.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public interface InjectionAwareI18N<KeyType> extends I18N<KeyType> {
	
	/**
	 * See {@link I18N} for a more thorough discussion of this method. It is only
	 * repeated inside this interface in order to leverage return type specialization.
	 *
	 * @param storage The storage the created localizer should refer back to
	 *
	 * @return A localiezr instance which supports injection functionality
	 *
	 * @see I18N
	 */
	InjectionAwareLocalizer createLocalizer( TranslationStorage storage );
	
}
