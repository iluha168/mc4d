package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.client.particle.TerrainParticle4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TerrainParticle.class)
abstract class TerrainParticleMixin extends SingleQuadParticleMixin implements TerrainParticle4 {
	@Overwrite
	@Deprecated
	private static @Nullable TerrainParticle createTerrainParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux) {
		throw Err4.arguments3("TerrainParticle4#createTerrainParticle");
	}

	@Mixin(TerrainParticle.CrumblingProvider.class)
	static class CrumblingProviderMixin implements ParticleProvider4<BlockParticleOption> {
		@Overwrite
		@Deprecated
		public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = TerrainParticle4.createTerrainParticle(options, level, x, y, z, w, xAux, yAux, zAux, wAux);
			if (particle != null) {
				((Particle4) particle).setParticleSpeed(0.0, 0.0, 0.0, 0.0);
				particle.setLifetime(random.nextInt(10) + 1);
			}

			return particle;
		}
	}

	@Mixin(TerrainParticle.DustPillarProvider.class)
	static class DustPillarProviderMixin implements ParticleProvider4<BlockParticleOption> {
		@Overwrite
		@Deprecated
		public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = TerrainParticle4.createTerrainParticle(options, level, x, y, z, w, xAux, yAux, zAux, wAux);
			if (particle != null) {
				((Particle4) particle).setParticleSpeed(random.nextGaussian() / 30.0, yAux + random.nextGaussian() / 2.0, random.nextGaussian() / 30.0, random.nextGaussian() / 30.0);
				particle.setLifetime(random.nextInt(20) + 20);
			}

			return particle;
		}
	}

	@Mixin(TerrainParticle.Provider.class)
	static class ProviderMixin implements ParticleProvider4<BlockParticleOption> {
		@Overwrite
		@Deprecated
		public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(BlockParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			return TerrainParticle4.createTerrainParticle(options, level, x, y, z, w, xAux, yAux, zAux, wAux);
		}
	}
}
