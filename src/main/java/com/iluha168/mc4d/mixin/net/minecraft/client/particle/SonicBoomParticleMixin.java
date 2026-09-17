package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SonicBoomParticle.class)
abstract class SonicBoomParticleMixin extends HugeExplosionParticleMixin {
	@Mixin(SonicBoomParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, 0.0, 0.0, 0.0, 0.0);
			return particle;
		}
	}
}
