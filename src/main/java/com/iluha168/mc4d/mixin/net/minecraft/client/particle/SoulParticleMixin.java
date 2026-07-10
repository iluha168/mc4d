package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.client.particle.RisingParticle4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SoulParticle.class)
abstract class SoulParticleMixin extends RisingParticleMixin {
	@Mixin(targets = {
		"net.minecraft.client.particle.SoulParticle$EmissiveProvider",
		"net.minecraft.client.particle.SoulParticle$Provider",
	})
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((RisingParticle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
