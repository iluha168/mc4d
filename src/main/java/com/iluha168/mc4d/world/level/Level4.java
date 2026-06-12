package com.iluha168.mc4d.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * All {@link net.minecraft.world.level.Level} instances implement {@link Level4}.
 */
public interface Level4 {
	int MAX_LEVEL_SIZE = 100000;

	LevelChunk getChunk(int chunkX, int chunkZ, int chunkW);

	BlockPos getBlockRandomPos(int xo, int yo, int zo, int wo, int yMask);
}
