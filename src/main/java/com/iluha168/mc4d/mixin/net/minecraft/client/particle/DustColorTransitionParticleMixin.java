package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustColorTransitionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DustColorTransitionParticle.class)
abstract class DustColorTransitionParticleMixin extends DustParticleBaseMixin {
	@Mixin(DustColorTransitionParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider<DustColorTransitionOptions>, ParticleProvider4<DustColorTransitionOptions> {
		@Override
		public @Nullable Particle createParticle(DustColorTransitionOptions options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
