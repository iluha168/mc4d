package com.iluha168.mc4d.world.level.chunk;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * Implemented by {@link net.minecraft.world.level.chunk.ChunkGenerator}.
 */
public interface ChunkGenerator4 {
	int getBaseHeight(int x, int z, int w, final Heightmap.Types type, final LevelHeightAccessor heightAccessor, final RandomState randomState);
	int getFirstFreeHeight(int x, int z, int w, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState);
	int getFirstOccupiedHeight(int x, int z, int w, Heightmap.Types type, LevelHeightAccessor heightAccessor, RandomState randomState);
}
