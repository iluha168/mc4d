package com.iluha168.mc4d.client.multiplayer;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by {@link net.minecraft.client.multiplayer.ClientLevel}.
 */
public interface ClientLevel4 {
	void animateTick(int xt, int yt, int zt, int wt);
	void doAnimateTick(int xt, int yt, int zt, int wt, int r, RandomSource animateRandom, @Nullable Block markerParticleTarget, BlockPos.MutableBlockPos pos);

	void setSectionDirtyWithNeighbors(int chunkX, int chunkY, int chunkZ, int chunkW);
	void setSectionRangeDirty(
		int minSectionX, int minSectionY, int minSectionZ, int minSectionW,
		int maxSectionX, int maxSectionY, int maxSectionZ, int maxSectionW
	);
}
