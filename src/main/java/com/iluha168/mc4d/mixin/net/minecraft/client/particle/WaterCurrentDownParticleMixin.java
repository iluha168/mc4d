package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterCurrentDownParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterCurrentDownParticle.class)
abstract class WaterCurrentDownParticleMixin extends SingleQuadParticleMixin {
	@Unique private float angleW;

	@Override
	public void init_finish(double w) {
		super.init_finish(w);
		this.wd = 0.0;
		this.angleW = this.random.nextFloat() * Mth.TWO_PI;
	}

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;zo:D")
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;z:D")
	@Expression("this.zo = this.z")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;move(DDD)V"
	))
	void tick_move(WaterCurrentDownParticle instance, double xa, double ya, double za) {
		this.wd = this.wd() + 0.6F * Mth.cos(this.angleW);
		this.wd *= 0.07;
		((Particle4) instance).move(xa, ya, za, this.wd);
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tick_containing(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.w);
	}
	@Inject(method = "tick", at = @At(
		value = "CONSTANT",
		args = "floatValue=0.08"
	))
	void tick_wAngle(CallbackInfo ci) {
		this.angleW += 0.08F;
	}

	@Mixin(WaterCurrentDownParticle.Provider.class)
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
			final WaterCurrentDownParticle particle = new WaterCurrentDownParticle(level, x, y, z, this.sprite.get(random));
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}
}
