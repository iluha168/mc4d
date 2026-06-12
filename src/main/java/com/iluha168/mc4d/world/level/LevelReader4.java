package com.iluha168.mc4d.world.level;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

/**
 * All implementations of {@link net.minecraft.world.level.LevelReader} also implement {@link LevelReader4}.
 */
public interface LevelReader4 extends CollisionGetter4 {
	@Nullable ChunkAccess getChunk(
		final int chunkX, final int chunkZ, final int chunkW,
		final ChunkStatus targetStatus, final boolean loadOrGenerate
	);

	default ChunkAccess getChunk(int chunkX, int chunkZ, int chunkW) {
		return this.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.FULL, true);
	}

	default ChunkAccess getChunk(int chunkX, int chunkZ, int chunkW, ChunkStatus status) {
		return this.getChunk(chunkX, chunkZ, chunkW, status, true);
	}

	@Override
	default @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ, int chunkW) {
		return this.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.EMPTY, false);
	}

	boolean hasChunk(int chunkX, int chunkZ, int chunkW);

	int getHeight(Heightmap.Types type, int x, int z, int w);

}
