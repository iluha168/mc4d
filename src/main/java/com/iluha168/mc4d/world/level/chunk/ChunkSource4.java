package com.iluha168.mc4d.world.level.chunk;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

/**
 * All subclasses of {@link net.minecraft.world.level.chunk.ChunkSource} must implement {@link ChunkSource4}.
 */
public interface ChunkSource4 {
	default @Nullable LightChunk getChunkForLighting(int x, int z, int w) {
		return this.getChunk(x, z, w, ChunkStatus.EMPTY, false);
	}

	@Nullable ChunkAccess getChunk(int x, int z, int w, ChunkStatus targetStatus, boolean loadOrGenerate);
}
