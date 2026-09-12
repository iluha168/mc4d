package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.BaseAshSmokeParticle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WhiteAshParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WhiteAshParticle.class)
abstract class WhiteAshParticleMixin extends BaseAshSmokeParticleMixin {
	@Mixin(WhiteAshParticle.Provider.class)
	static class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		@Final
		private SpriteSet sprites;

		@Overwrite
		@Deprecated
		public Particle createParticle(
			SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
		) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final double xa = random.nextFloat() * -1.9 * random.nextFloat() * 0.1;
			final double ya = random.nextFloat() * -0.5 * random.nextFloat() * 0.1 * 5.0;
			final double za = random.nextFloat() * -1.9 * random.nextFloat() * 0.1;
			final double wa = random.nextFloat() * -1.9 * random.nextFloat() * 0.1;
			WhiteAshParticle particle = new WhiteAshParticle(level, x, y, z, xa, ya, za, 1.0F, this.sprites);
			((BaseAshSmokeParticle4) particle).init_finish(w, 0.1F, -0.1F, 0.1F, 0.1F, xa, ya, za, wa);
			return particle;
		}
	}
}
