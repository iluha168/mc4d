package com.iluha168.mc4d.world.level.chunk;

/**
 * Implemented by {@link net.minecraft.world.level.chunk.Strategy}.
 */
public interface Strategy4<T> {
	int getIndex(int x, int y, int z, int w);
}
