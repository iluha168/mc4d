package com.iluha168.mc4d.server.level;

import net.minecraft.server.level.ChunkTrackingView;
import org.jetbrains.annotations.ApiStatus;

/**
 * Extended by {@link ChunkTrackingView}.
 */
public interface ChunkTrackingView4 {
	static ChunkTrackingView4 as(ChunkTrackingView view) {
		return (ChunkTrackingView4) view;
	}
	static ChunkTrackingView4 as(ChunkTrackingView.Positioned view) {
		return (ChunkTrackingView4) (Object) view;
	}

	default boolean contains(int x, int z, int w) {
		return this.contains(x, z, w, true);
	}

	boolean contains(int chunkX, int chunkZ, int chunkW, boolean includeNeighbors);

	default boolean isInViewDistance(int chunkX, int chunkZ, int chunkW) {
		return this.contains(chunkX, chunkZ, chunkW, false);
	}

	static boolean isInViewDistance(int centerX, int centerZ, int centerW, int viewDistance, int chunkX, int chunkZ, int chunkW) {
		return isWithinDistance(centerX, centerZ, centerW, viewDistance, chunkX, chunkZ, chunkW, false);
	}

	static boolean isWithinDistance(int centerX, int centerZ, int centerW, int viewDistance, int chunkX, int chunkZ, int chunkW, boolean includeNeighbors) {
		int bufferRange = includeNeighbors ? 2 : 1;
		long deltaX = Math.max(0, Math.abs(chunkX - centerX) - bufferRange);
		long deltaZ = Math.max(0, Math.abs(chunkZ - centerZ) - bufferRange);
		long deltaW = Math.max(0, Math.abs(chunkW - centerW) - bufferRange);
		long distanceSquared = deltaX * deltaX + deltaZ * deltaZ + deltaW * deltaW;
		int radiusSquared = viewDistance * viewDistance;
		return distanceSquared < radiusSquared;
	}

	/**
	 * Implemented by {@link ChunkTrackingView.Positioned}.
	 */
	interface Positioned {
		static ChunkTrackingView4.Positioned as(ChunkTrackingView.Positioned view) {
			return (ChunkTrackingView4.Positioned) (Object) view;
		}

		@ApiStatus.Internal
		int minW();
		@ApiStatus.Internal
		int maxW();
	}
}
