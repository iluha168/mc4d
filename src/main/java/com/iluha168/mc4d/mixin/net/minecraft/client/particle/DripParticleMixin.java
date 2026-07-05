package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DripParticle.class)
abstract class DripParticleMixin extends SingleQuadParticleMixin {
	@Definition(id = "xo", field = "Lnet/minecraft/client/particle/DripParticle;xo:D")
	@Definition(id = "x", field = "Lnet/minecraft/client/particle/DripParticle;x:D")
	@Expression("this.xo = this.x")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/DripParticle;move(DDD)V"
	))
	void tick_move(DripParticle instance, double x, double y, double z) {
		((Particle4) instance).move(x, y, z, this.wd);
	}
	@Inject(method = "tick", at = @At(
		value = "CONSTANT",
		args = "doubleValue=0.9800000190734863",
		ordinal = 0
	))
	void tick_drag(CallbackInfo ci) {
		this.wd *= 0.98F;
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tick_containing(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.w);
	}

	@Mixin(targets = "net.minecraft.client.particle.DripParticle$DripHangParticle")
	abstract static class DripHangParticleMixin extends DripParticleMixin {
		@Redirect(method = "preMoveUpdate", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		))
		void preMoveUpdate(ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
			((LevelAccessor4) instance).addParticle(particle, x, y, z, this.w(), xd, yd, zd, this.wd);
		}
		@Inject(method = "postMoveUpdate", at = @At("HEAD"))
		void postMoveUpdate(CallbackInfo ci) {
			this.wd = this.wd() * 0.02;
		}
	}

	@Mixin(targets = "net.minecraft.client.particle.DripParticle$DripstoneFallAndLandParticle")
	abstract static class DripstoneFallAndLandParticleMixin extends FallAndLandParticleMixin {
		@Redirect(method = "postMoveUpdate", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		))
		void postMoveUpdate_addParticle(ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
			((LevelAccessor4) instance).addParticle(particle, x, y, z, this.w(), xd, yd, zd, zd);
		}
		@Redirect(method = "postMoveUpdate", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
		))
		void postMoveUpdate_playLocalSound(ClientLevel instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
			((Level4) instance).playLocalSound(x, y, z, this.w, sound, source, volume, pitch, distanceDelay);
		}
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.DripParticle$DripstoneLavaFallProvider",
		"net.minecraft.client.particle.DripParticle$DripstoneLavaHangProvider",
		"net.minecraft.client.particle.DripParticle$DripstoneWaterFallProvider",
		"net.minecraft.client.particle.DripParticle$DripstoneWaterHangProvider",

		"net.minecraft.client.particle.DripParticle$HoneyFallProvider",
		"net.minecraft.client.particle.DripParticle$HoneyHangProvider",
		"net.minecraft.client.particle.DripParticle$HoneyLandProvider",

		"net.minecraft.client.particle.DripParticle$LavaFallProvider",
		"net.minecraft.client.particle.DripParticle$LavaHangProvider",
		"net.minecraft.client.particle.DripParticle$LavaLandProvider",

		"net.minecraft.client.particle.DripParticle$NectarFallProvider",

		"net.minecraft.client.particle.DripParticle$ObsidianTearFallProvider",
		"net.minecraft.client.particle.DripParticle$ObsidianTearHangProvider",
		"net.minecraft.client.particle.DripParticle$ObsidianTearLandProvider",

		"net.minecraft.client.particle.DripParticle$SporeBlossomFallProvider",

		"net.minecraft.client.particle.DripParticle$WaterFallProvider",
		"net.minecraft.client.particle.DripParticle$WaterHangProvider",
	})
	static abstract class ProviderNoInitialVelocityMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w);
			return particle;
		}
	}

	@Mixin(targets = "net.minecraft.client.particle.DripParticle$FallAndLandParticle")
	abstract static class FallAndLandParticleMixin extends DripParticleMixin {
		@Redirect(method = "postMoveUpdate", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		))
		void postMoveUpdate(ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
			((LevelAccessor4) instance).addParticle(particle, x, y, z, this.w(), xd, yd, zd, zd);
		}
	}

	@Mixin(targets = "net.minecraft.client.particle.DripParticle$HoneyFallAndLandParticle")
	abstract static class HoneyFallAndLandParticleMixin extends FallAndLandParticleMixin {
		@Redirect(method = "postMoveUpdate", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		))
		void postMoveUpdate_addParticle(ClientLevel instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd) {
			((LevelAccessor4) instance).addParticle(particle, x, y, z, this.w(), xd, yd, zd, zd);
		}
		@Redirect(method = "postMoveUpdate", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
		))
		void postMoveUpdate_playLocalSound(ClientLevel instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay) {
			((Level4) instance).playLocalSound(x, y, z, this.w, sound, source, volume, pitch, distanceDelay);
		}
	}
}
