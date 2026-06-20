package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ClientboundLevelParticlesPacket}.
 */
public interface ClientboundLevelParticlesPacket4 {
	static <T extends ParticleOptions> ClientboundLevelParticlesPacket from(
		T particle,
		boolean overrideLimiter,
		boolean alwaysShow,
		double x, double y, double z, double w,
		float xDist, float yDist, float zDist, float wDist,
		float maxSpeed, int count
	) {
		ClientboundLevelParticlesPacket packet = new ClientboundLevelParticlesPacket(particle, overrideLimiter, alwaysShow, x, y, z, xDist, yDist, zDist, maxSpeed, count);
		ClientboundLevelParticlesPacket4 packet4 = (ClientboundLevelParticlesPacket4) packet;
		packet4.setW(w);
		packet4.setWDist(wDist);
		return packet;
	}

	double getW();
	@ApiStatus.Internal
	void setW(double w);

	float getWDist();
	@ApiStatus.Internal
	void setWDist(float wDist);
}
