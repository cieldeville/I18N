package com.blackypaw.mc.i18n;

import java.nio.charset.StandardCharsets;

/**
 * Several hashing utilities related to the FNV hash function.
 *
 * @author BlackyPaw
 * @version 1.0
 */
class FNVHash {

	private static final long FNV_32_PRIME  = 16777619L;
	private static final long FNV_32_OFFSET = 2166136261L;

	/**
	 * Calculates the FVN-1a 32-bit hash value of the given string.
	 *
	 * @param str The string to hash
	 * @return The hash value of the string
	 */
	public static int hash1a32( String str ) {
		return hash1a32( str.getBytes( StandardCharsets.UTF_8 ) );
	}

	/**
	 * Calculates the FVN-1a 32-bit hash value of the given bytes.
	 *
	 * @param buffer The bytes to hash
	 * @return The hash value of the bytes
	 */
	public static int hash1a32( byte[] buffer ) {
		int hash = (int) FNV_32_OFFSET;
		for ( int i = 0; i < buffer.length; ++i ) {
			hash ^= buffer[i];
			hash *= FNV_32_PRIME;
		}
		return hash;
	}

}
