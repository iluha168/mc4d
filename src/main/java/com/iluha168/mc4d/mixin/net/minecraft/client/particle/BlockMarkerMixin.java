package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BlockMarker;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockMarker.class)
abstract class BlockMarkerMixin extends SingleQuadParticleMixin {
	@Mixin(BlockMarker.Provider.class)
	static class ProviderMixin implements ParticleProvider4<BlockParticleOption> {
		@Overwrite
		@Deprecated
		public Particle createParticle(
			BlockParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
		) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(
			BlockParticleOption options, ClientLevel level,
			double x, double y, double z, double w,
			double xAux, double yAux, double zAux, double wAux,
			RandomSource random
		) {
			final BlockMarker particle = new BlockMarker(level, x, y, z, options.getState());
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}
}
