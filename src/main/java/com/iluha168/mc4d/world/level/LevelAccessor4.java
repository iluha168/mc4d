package com.iluha168.mc4d.world.level;

import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.LevelAccessor;

/**
 * Vanilla: {@link LevelAccessor}.
 */
public interface LevelAccessor4 extends LevelReader4 {
	@Override
	default boolean hasChunk(int chunkX, int chunkZ, int chunkW) {
		return ((ChunkSource4) ((LevelAccessor) this).getChunkSource()).hasChunk(chunkX, chunkZ, chunkW);
	}

	void addParticle(
		final ParticleOptions particle,
		final double x, final double y, final double z, final double w,
		final double xd, final double yd, final double zd, final double wd
	);
}
