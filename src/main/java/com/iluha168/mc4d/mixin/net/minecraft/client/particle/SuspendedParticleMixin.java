package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SuspendedParticle.class)
abstract class SuspendedParticleMixin extends SingleQuadParticleMixin {
	@Mixin(SuspendedParticle.CrimsonSporeProvider.class)
	static abstract class CrimsonSporeProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			final double xa = random.nextGaussian() * 1.0E-6F;
			final double ya = random.nextGaussian() * 1.0E-4F;
			final double za = random.nextGaussian() * 1.0E-6F;
			final double wa = random.nextGaussian() * 1.0E-6F;
			((Particle4) particle).init_finish(w, xa, ya, za, wa);
			return particle;
		}
	}

	@Mixin(SuspendedParticle.SporeBlossomAirProvider.class)
	static abstract class SporeBlossomAirProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w, 0.0, -0.8F, 0.0, 0.0);
			return particle;
		}
	}

	@Mixin(SuspendedParticle.UnderwaterProvider.class)
	static abstract class UnderwaterProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}

	@Mixin(SuspendedParticle.WarpedSporeProvider.class)
	static abstract class WarpedSporeProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w, 0.0, random.nextFloat() * -1.9 * random.nextFloat() * 0.1, 0.0, 0.0);
			return particle;
		}
	}
}
