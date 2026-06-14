package com.iluha168.mc4d.server.level;

import net.minecraft.server.level.ChunkResult;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.concurrent.CompletableFuture;

/**
 * Implemented by {@link net.minecraft.server.level.ServerChunkCache}.
 */
public interface ServerChunkCache4 {
	CompletableFuture<ChunkResult<ChunkAccess>> getChunkFuture(int x, int z, int w, ChunkStatus targetStatus, boolean loadOrGenerate);
}
