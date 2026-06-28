package com.iluha168.mc4d.server.level;

import net.minecraft.server.level.ColumnPos;

/**
 * Implemented by {@link ColumnPos}.
 */
public interface ColumnPos4 {
	int COORD_BITS = Long.SIZE / 3;
	long COORD_MASK = (1L << COORD_BITS) - 1L;

	int w();
	void setW(int w);

	static long asLong(int x, int z, int w) {
		return x & COORD_MASK | (z & COORD_MASK) << COORD_BITS | (w & COORD_MASK) << (COORD_BITS*2);
	}

	static int getW(long pos) {
		return (int)(pos >>> (COORD_BITS*2) & COORD_MASK);
	}
	// TODO everything
}
