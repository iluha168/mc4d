package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.GlowParticle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GlowParticle.class)
abstract class GlowParticleMixin extends SingleQuadParticleMixin implements GlowParticle4 {
	@Mixin(GlowParticle.ElectricSparkProvider.class)
	static abstract class ElectricSparkProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);
		@Redirect(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/GlowParticle;setParticleSpeed(DDD)V"
		))
		void createParticle_postpone_setParticleSpeed(GlowParticle instance, double x, double y, double z) {}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			final Particle4 particle4 = (Particle4) particle;
			particle4.init_finish(w, 0.0, 0.0, 0.0, 0.0);
			particle4.setParticleSpeed(xAux * 0.25, yAux * 0.25, zAux * 0.25, wAux * 0.25);
			return particle;
		}
	}

	@Mixin(GlowParticle.GlowSquidProvider.class)
	static abstract class GlowSquidProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);
		@Definition(id = "xAux", local = @Local(type = double.class, name = "xAux", argsOnly = true))
		@Expression("xAux == 0.0")
		@ModifyExpressionValue(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;", at = @At("MIXINEXTRAS:EXPRESSION"))
		boolean createParticle_postpone_0(boolean original) {
			return false;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			final Particle4 particle4 = (Particle4) particle;
			particle4.init_finish(w, 0.5 - random.nextDouble(), yAux, 0.5 - random.nextDouble(), 0.5 - random.nextDouble());
			((GlowParticle4) particle).GlowSquidProvider_helper(xAux == 0.0 && zAux == 0.0 && wAux == 0.0);
			return particle;
		}
	}
	@Override
	public void GlowSquidProvider_helper(final boolean horizontalAux0) {
		this.yd *= 0.2F;
		if (horizontalAux0) {
			this.xd *= 0.1F;
			this.zd *= 0.1F;
			this.wd *= 0.1F;
		}
	}

	@Mixin(GlowParticle.ScrapeProvider.class)
	static abstract class ScrapeProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);
		@Redirect(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/GlowParticle;setParticleSpeed(DDD)V"
		))
		void createParticle_postpone_setParticleSpeed(GlowParticle instance, double x, double y, double z) {}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			final Particle4 particle4 = (Particle4) particle;
			particle4.init_finish(w, 0.0, 0.0, 0.0, 0.0);
			particle4.setParticleSpeed(xAux * 0.01, yAux * 0.01, zAux * 0.01, wAux * 0.01);
			return particle;
		}
	}

	@Mixin(GlowParticle.WaxOffProvider.class)
	static abstract class WaxOffProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);
		@Redirect(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/GlowParticle;setParticleSpeed(DDD)V"
		))
		void createParticle_postpone_setParticleSpeed(GlowParticle instance, double x, double y, double z) {}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			final Particle4 particle4 = (Particle4) particle;
			particle4.init_finish(w, 0.0, 0.0, 0.0, 0.0);
			particle4.setParticleSpeed(xAux * 0.01 / 2.0, yAux * 0.01, zAux * 0.01 / 2.0,  wAux * 0.01 / 2.0);
			return particle;
		}
	}

	@Mixin(GlowParticle.WaxOnProvider.class)
	static abstract class WaxOnProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);
		@Redirect(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/GlowParticle;setParticleSpeed(DDD)V"
		))
		void createParticle_postpone_setParticleSpeed(GlowParticle instance, double x, double y, double z) {}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			final Particle4 particle4 = (Particle4) particle;
			particle4.init_finish(w, 0.0, 0.0, 0.0, 0.0);
			particle4.setParticleSpeed(xAux * 0.01 / 2.0, yAux * 0.01, zAux * 0.01 / 2.0,  wAux * 0.01 / 2.0);
			return particle;
		}
	}
}
