package com.iluha168.mc4d.util;

/**
 * Implemented by {@link net.minecraft.util.Mth}.
 */
public interface Mth4 {
	static long getSeed(int x, int y, int z, int w) {
		@SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
		long seed = x * 3129871 ^ z * 116129781L ^ w * 2654435761L ^ y;
		seed = seed * seed * 42317861L + seed * 11L;
		return seed >> 16;
	}
}
