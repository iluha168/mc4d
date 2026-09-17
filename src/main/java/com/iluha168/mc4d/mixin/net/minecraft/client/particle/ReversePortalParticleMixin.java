package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ReversePortalParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReversePortalParticle.class)
abstract class ReversePortalParticleMixin extends PortalParticleMixin {
	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/ReversePortalParticle;xo:D",
		opcode = Opcodes.PUTFIELD
	))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/ReversePortalParticle;setPos(DDD)V"
	))
	void tick_setPos(ReversePortalParticle instance, double x, double y, double z, @Local(name = "speedMultiplier") float speedMultiplier) {
		this.w = this.w + this.wd * speedMultiplier;
		((Particle4) instance).setPos(x, y, z, this.w); // 4D Neo lmao: update the particle's bounding box
	}

	@Mixin(ReversePortalParticle.ReversePortalProvider.class)
	abstract static class ReversePortalProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
