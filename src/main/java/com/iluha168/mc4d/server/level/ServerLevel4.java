package com.iluha168.mc4d.server.level;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;

/**
 * Implemented by {@link net.minecraft.server.level.ServerLevel}.
 */
public interface ServerLevel4 {
	<T extends ParticleOptions> int sendParticles(
		T particle,
		double x, double y, double z, double w,
		int count,
		double xDist, double yDist, double zDist, double wDist,
		double speed
	);

	<T extends ParticleOptions> int sendParticles(
		T particle,
		boolean overrideLimiter,
		boolean alwaysShow,
		double x, double y, double z, double w,
		int count,
		double xDist, double yDist, double zDist, double wDist,
		double speed
	);

	<T extends ParticleOptions> boolean sendParticles(
		ServerPlayer player,
		T particle,
		boolean overrideLimiter,
		boolean alwaysShow,
		double x, double y, double z, double w,
		int count,
		double xDist, double yDist, double zDist, double wDist,
		double speed
	);

	boolean setChunkForced(int chunkX, int chunkZ, int chunkW, boolean forced);
}
