package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubbleParticle;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BubbleParticle.class)
abstract class BubbleParticleMixin extends SingleQuadParticleMixin {
	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w);
		this.wd = wa * 0.2F + (this.random.nextFloat() * 2.0F - 1.0F) * 0.02F;
	}

	@Definition(id = "xo", field = "Lnet/minecraft/client/particle/BubbleParticle;xo:D")
	@Definition(id = "x", field = "Lnet/minecraft/client/particle/BubbleParticle;x:D")
	@Expression("this.xo = this.x")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/BubbleParticle;move(DDD)V"
	))
	void tick_move(BubbleParticle instance, double x, double y, double z) {
		((Particle4) instance).move(x, y, z, this.wd);
		this.wd *= 0.85F;
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tick_containing(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.w);
	}

	@Mixin(BubbleParticle.Provider.class)
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
			BubbleParticle particle = new BubbleParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random));
			((Particle4) particle).init_finish(w, wAux);
			return particle;
		}
	}
}
