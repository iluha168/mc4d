package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
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
	// Normal perpendicular vectors U and V that define the random bubbles spin plane.
	@Unique private float spinUx, spinUz, spinUw, spinVz, spinVw; // Vx == 0

	@Override
	public void init_finish(double w) {
		super.init_finish(w);
		this.wd = 0.0;
		// Choosing a random 2D plane in the horizontal 3D plane.
		final float circleCos = this.random.nextFloat() * 2.0F - 1.0F;
		final float circleSin = Mth.sqrt(1.0F - circleCos * circleCos);
		final float roll = this.random.nextFloat() * Mth.TWO_PI;
		final float rollCos = Mth.cos(roll);
		final float rollSin = Mth.sin(roll);
		this.spinUx = circleSin;
		this.spinUz = -circleCos * rollCos;
		this.spinUw = -circleCos * rollSin;
		this.spinVz = rollSin;
		this.spinVw = -rollCos;
	}

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;zo:D")
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;z:D")
	@Expression("this.zo = this.z")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@ModifyExpressionValue(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;cos(D)F"
	))
	float tick_xd(float cosAngle, @Share("cosAngle") LocalFloatRef cosAngleRef) {
		cosAngleRef.set(cosAngle);
		return cosAngle * this.spinUx;
	}
	@ModifyExpressionValue(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;sin(D)F"
	))
	float tick_zd(float sinAngle, @Share("cosAngle") LocalFloatRef cosAngleRef) {
		final float cosAngle = cosAngleRef.get();
		this.wd = this.wd + 0.6F * (cosAngle * this.spinUw + sinAngle * this.spinVw);
		return cosAngle * this.spinUz + sinAngle * this.spinVz;
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/WaterCurrentDownParticle;move(DDD)V"
	))
	void tick_move(WaterCurrentDownParticle instance, double xa, double ya, double za) {
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
