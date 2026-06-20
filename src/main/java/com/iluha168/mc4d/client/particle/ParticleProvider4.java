package com.iluha168.mc4d.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by the same class that implement {@link ParticleProvider},
 */
// TODO actually implement by all classes
public interface ParticleProvider4<T extends ParticleOptions> {
	@Nullable Particle createParticle(
		T options, ClientLevel level,
		double x, double y, double z, double w,
		double xAux, double yAux, double zAux, double wAux,
		RandomSource random
	);

	// Sprite has no uses?..
}
