package com.iluha168.mc4d.world.level.lighting;

import net.minecraft.world.level.BlockGetter;

/**
 * Implemented by {@link net.minecraft.world.level.lighting.ChunkSkyLightSources}.
 */
public interface ChunkSkyLightSources4 {
	boolean update(BlockGetter level, int x, int y, int z, int w);
	int getLowestSourceY(int x, int z, int w);
}
