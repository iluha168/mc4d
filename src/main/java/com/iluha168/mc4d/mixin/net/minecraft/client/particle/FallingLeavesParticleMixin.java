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
	@Unique private double swirlPeriod2;

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, float fallAcceleration, float sideAcceleration, boolean swirl, boolean flowAway, float scale, float startVelocity, CallbackInfo ci) {
		final float particleRandom2 = this.random.nextFloat();
		this.waFlowScale = Math.cos(Math.toRadians(particleRandom2 * 60.0F)) * this.windBig;
		this.swirlPeriod2 = Math.toRadians(1000.0F + particleRandom2 * 3000.0F);
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
	@Inject(method = "tick", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;pow(DD)D",
		ordinal = 0
	))
	void tick_flowAway(CallbackInfo ci, @Share("wa") LocalDoubleRef wa, @Local(name = "relativeAge") float relativeAge) {
		wa.set(wa.get() + this.waFlowScale * Math.pow(relativeAge, 1.25));
	}
	@Inject(method = "tick", at = @At(
		value = "INVOKE",
		target = "Ljava/lang/Math;cos(D)D",
		ordinal = 0
	))
	void tick_swirl(CallbackInfo ci, @Share("wa") LocalDoubleRef wa, @Local(name = "relativeAge") float relativeAge) {
		wa.set(wa.get() + relativeAge * Math.cos(relativeAge * this.swirlPeriod2) * this.windBig);
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
