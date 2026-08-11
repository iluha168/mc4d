package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BubbleColumnUpParticle.class)
abstract class BubbleColumnUpParticleMixin extends SingleQuadParticleMixin {
	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w);
		this.wd = wa * 0.2F + (this.random.nextFloat() * 2.0F - 1.0F) * 0.02F;
	}

	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tick(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.w());
	}

	@Mixin(BubbleColumnUpParticle.Provider.class)
	static class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		@Final
		private SpriteSet sprite;

		@Overwrite
		@Deprecated
		public Particle createParticle(
			SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
		) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final BubbleColumnUpParticle particle = new BubbleColumnUpParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random));
			((Particle4) particle).init_finish(w, wAux);
			return particle;
		}
	}
}
