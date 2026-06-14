package com.iluha168.mc4d.client.color.block;

/**
 * Implemented by {@link net.minecraft.client.color.block.BlockTintCache}.
 */
public interface BlockTintCache4 {
	void invalidateForChunk(int chunkX, int chunkZ, int chunkW);

	/**
	 * Implemented by {@link net.minecraft.client.color.block.BlockTintCache.LatestCacheInfo}.
	 */
	interface LatestCacheInfo {
		int w();
		void setW(int w);
	}
}
