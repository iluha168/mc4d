package com.iluha168.mc4d.server.level;

import net.minecraft.server.level.ServerPlayer;

/**
 * Implemented by {@link net.minecraft.server.level.ChunkMap}.
 */
public interface ChunkMap4 {
	boolean isChunkTracked(ServerPlayer player, int chunkX, int chunkZ, int chunkW);
}
