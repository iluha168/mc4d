package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FallingLeavesParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingLeavesParticle.class)
abstract class FallingLeavesParticleMixin extends SingleQuadParticleMixin {
	@Shadow
	@Final
	private float windBig;

	@Unique private double waFlowScale;
	// Normal perpendicular vectors U and V that define the random leaves spin plane.
	@Unique private double spinUx, spinUz, spinUw, spinVz, spinVw; // Vx == 0

	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;cos(D)D"
	))
	double init_xaFlowScale(double original, @Share("zaFlowDirection") LocalDoubleRef zaFlowDirection) {
		// In 4D, the 60deg arc (2D spherical cap) becomes a spherical cap of a 3D sphere.
		final double halfConeRad = Math.toRadians(60d / 2d);
		// Yeah, wind direction is biased like that, spawn a million particles in vanilla, and you will see this cone.
		final double circleCos = 1d - this.random.nextDouble() * (1d - Math.cos(halfConeRad));
		final double circleSin = Math.sqrt(1d - circleCos * circleCos);
		final double roll = this.random.nextDouble() * Mth.TWO_PI;
		final double inPlane = circleSin * Math.cos(roll);
		final double axisCos = Math.cos(halfConeRad);
		final double axisSin = Math.sin(halfConeRad);

		this.waFlowScale = circleSin * Math.sin(roll) * this.windBig;
		zaFlowDirection.set(circleCos * axisSin + inPlane * axisCos);
		return              circleCos * axisCos - inPlane * axisSin;
	}
	@ModifyExpressionValue(method = "<init>", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sin(D)D"
	))
	double init_zaFlowScale(double original, @Share("zaFlowDirection") LocalDoubleRef zaFlowDirection) {
		return zaFlowDirection.get();
	}
	@Inject(method = "<init>", at = @At("TAIL"))
	void init_swirlPlane(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, float fallAcceleration, float sideAcceleration, boolean swirl, boolean flowAway, float scale, float startVelocity, CallbackInfo ci) {
		// Choosing a random 2D plane in the horizontal 3D plane.
		final double circleCos = this.random.nextDouble() * 2.0 - 1.0;
		final double circleSin = Math.sqrt(1.0 - circleCos * circleCos);
		final double roll = this.random.nextDouble() * Mth.TWO_PI;
		final double rollCos = Math.cos(roll);
		final double rollSin = Math.sin(roll);
		this.spinUx = circleSin;
		this.spinUz = -circleCos * rollCos;
		this.spinUw = -circleCos * rollSin;
		this.spinVz = rollSin;
		this.spinVw = -rollCos;
	}

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/FallingLeavesParticle;zo:D")
	@Expression("this.zo = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Definition(id = "za", local = @Local(type = double.class, name = "za"))
	@Expression("za = @(0.0)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wa(CallbackInfo ci, @Share("wa") LocalDoubleRef wa) {
		wa.set(0.0);
	}
	@Definition(id = "zaFlowScale", field = "Lnet/minecraft/client/particle/FallingLeavesParticle;zaFlowScale:D")
	@Definition(id = "pow", method = "Ljava/lang/Math;pow(DD)D")
	@Expression("this.zaFlowScale * @(pow(?, ?))")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	double tick_flowAway(double speed, @Share("wa") LocalDoubleRef wa) {
		wa.set(wa.get() + this.waFlowScale * speed);
		return speed;
	}
	@ModifyExpressionValue(method = "tick", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;cos(D)D"
	))
	double tick_swirlX(double swirlCos, @Share("swirlCos") LocalDoubleRef swirlCosRef) {
		swirlCosRef.set(swirlCos);
		return swirlCos * this.spinUx;
	}
	@ModifyExpressionValue(method = "tick", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;sin(D)D"
	))
	double tick_swirlZ(
		double original,
		@Share("swirlCos") LocalDoubleRef swirlCosRef,
		@Share("wa") LocalDoubleRef wa,
		@Local(name = "relativeAge") float relativeAge
	) {
		final double swirlCos = swirlCosRef.get();
		wa.set(wa.get() + relativeAge * (swirlCos * this.spinUw + original * this.spinVw) * this.windBig);
		return swirlCos * this.spinUz + original * this.spinVz;
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/FallingLeavesParticle;move(DDD)V"
	))
	void tick_move(FallingLeavesParticle instance, double x, double y, double z, @Share("wa") LocalDoubleRef wa) {
		this.wd += wa.get() * 0.0025F;
		((Particle4) instance).move(x, y, z, this.wd);
	}
	@Definition(id = "zd", field = "Lnet/minecraft/client/particle/FallingLeavesParticle;zd:D")
	@Expression("this.zd == 0.0")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean tick_stopped(boolean original) {
		return original || this.wd == 0.0;
	}
	@Definition(id = "friction", field = "Lnet/minecraft/client/particle/FallingLeavesParticle;friction:F")
	@Expression("this.friction")
	@Inject(method = "tick", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	void tick_friction(CallbackInfo ci) {
		this.wd *= this.friction;
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.FallingLeavesParticle$CherryProvider",
		"net.minecraft.client.particle.FallingLeavesParticle$PaleOakProvider",
	})
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.FallingLeavesParticle$TintedLeavesProvider",
	})
	abstract static class TintedLeavesProviderMixin implements ParticleProvider<ColorParticleOption>, ParticleProvider4<ColorParticleOption> {
		@Override
		public @Nullable Particle createParticle(ColorParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}
}
