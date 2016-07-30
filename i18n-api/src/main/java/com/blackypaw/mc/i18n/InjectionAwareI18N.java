/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

/**
 * @author BlackyPaw
 * @version 1.0
 */
public interface InjectionAwareI18N<KeyType> extends I18N<KeyType> {
	
	InjectionAwareLocalizer createLocalizer( TranslationStorage storage );
	
}
