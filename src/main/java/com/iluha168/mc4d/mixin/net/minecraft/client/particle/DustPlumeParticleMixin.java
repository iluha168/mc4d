package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.BaseAshSmokeParticle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustPlumeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DustPlumeParticle.class)
abstract class DustPlumeParticleMixin extends BaseAshSmokeParticleMixin {
	@Mixin(DustPlumeParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((BaseAshSmokeParticle4) particle).init_finish(w, 0.7F, 0.6F, 0.7F, 0.7F, xAux, yAux + 0.15F, zAux, wAux);
			return particle;
		}
	}
}
