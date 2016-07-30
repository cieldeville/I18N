/*
 * Copyright (c) 2016, BlackyPaw
 * All rights reserved.
 *
 * This code is licensed under a BSD 3-Clause license. For further license details view the LICENSE file in the root folder of this source tree.
 */

package com.blackypaw.mc.i18n;

import java.util.Locale;

/**
 * Resembles the Supplier&lt;T&gt; functional interface which is not yet available in Java 7.
 * Used to inject the current value of the fallback locale set for the respective implementation
 * into translation storages.
 *
 * @author BlackyPaw
 * @version 1.0
 */
public interface FallbackLocaleSupplier {
	
	Locale getFallbackLocale();
	
}
