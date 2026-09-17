package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SculkChargeParticle;
import net.minecraft.core.particles.SculkChargeParticleOptions;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkChargeParticle.class)
abstract class SculkChargeParticleMixin extends SingleQuadParticleMixin {
	@Mixin(SculkChargeParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider<SculkChargeParticleOptions>, ParticleProvider4<SculkChargeParticleOptions> {
		@Redirect(method = "createParticle(Lnet/minecraft/core/particles/SculkChargeParticleOptions;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/SculkChargeParticle;setParticleSpeed(DDD)V"
		))
		void createParticle_postpone_setParticleSpeed(SculkChargeParticle instance, double xd, double yd, double zd) {}
		@Override
		public @Nullable Particle createParticle(SculkChargeParticleOptions options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			final Particle4 particle4 = (Particle4) particle;
			//noinspection DataFlowIssue
			particle4.init_finish(w, xAux, yAux, zAux, wAux);
			particle4.setParticleSpeed(xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
