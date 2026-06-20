package com.iluha168.mc4d.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by {@link ParticleEngine}.
 */
public interface ParticleEngine4 {
	@Nullable Particle createParticle(
		ParticleOptions options,
		double x, double y, double z, double w,
		double xa, double ya, double za, double wa
	);
}
