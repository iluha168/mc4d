package com.iluha168.mc4d.server.level;

import java.util.concurrent.CompletableFuture;

/**
 * Implemented by {@link net.minecraft.server.level.ThreadedLevelLightEngine}.
 */
public interface ThreadedLevelLightEngine4 {
	CompletableFuture<?> waitForPendingTasks(int chunkX, int chunkZ, int chunkW);
}
