package com.iluha168.mc4d.world.level.levelgen.blending;

import com.iluha168.mc4d.core.Direction18;
import com.iluha168.mc4d.world.level.LevelReader4;
import net.minecraft.core.Direction8;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Implemented by {@link BlendingData}.
 */
public interface BlendingData4 {
	static @Nullable BlendingData getOrUpdateBlendingData(WorldGenRegion region, int chunkX, int chunkZ, int chunkW) {
		ChunkAccess chunk = ((LevelReader4) region).getChunk(chunkX, chunkZ, chunkW);
		BlendingData blendingData = chunk.getBlendingData();
		if (blendingData != null && !chunk.getHighestGeneratedStatus().isBefore(ChunkStatus.BIOMES)) {
			blendingData.calculateData(chunk, sideByGenerationAge(region, chunkX, chunkZ, chunkW, false));
			return blendingData;
		} else {
			return null;
		}
	}

	static Set<Direction8> sideByGenerationAge(WorldGenLevel region, int chunkX, int chunkZ, int chunkW, boolean wantedOldGen) {
		Set<Direction8> sides = EnumSet.noneOf(Direction8.class);

		for (Direction8 direction8 : Direction8.values()) {
			int testChunkX = chunkX + direction8.getStepX();
			int testChunkZ = chunkZ + direction8.getStepZ();
			int testChunkW = chunkW + Direction18.as(direction8).getStepW();
			if (((LevelReader4) region).getChunk(testChunkX, testChunkZ, testChunkW).isOldNoiseGeneration() == wantedOldGen) {
				sides.add(direction8);
			}
		}

		return sides;
	}
}
