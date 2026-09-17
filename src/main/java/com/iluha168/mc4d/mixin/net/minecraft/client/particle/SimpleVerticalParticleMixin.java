package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleVerticalParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SimpleVerticalParticle.class)
abstract class SimpleVerticalParticleMixin extends SingleQuadParticleMixin {
	// Proxy yd as this.y, and stash for later. this.y is vacant during construction because of ParticleMixin.
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SimpleVerticalParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd_write(SimpleVerticalParticle instance, double value) {
		this.y = value;
	}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SimpleVerticalParticle;yd:D", opcode = Opcodes.GETFIELD))
	double init_postpone_yd_read(SimpleVerticalParticle instance) {
		return this.y;
	}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		final double yd = this.y; // Destash.
		super.init_finish(w, xa, ya, za, wa);
		this.xd = xa;
		this.yd = yd; // "upward" was already applied to yd.
		this.zd = za;
		this.wd = wa;
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.SimpleVerticalParticle$PauseMobGrowthProvider",
		"net.minecraft.client.particle.SimpleVerticalParticle$ResetMobGrowthProvider",
	})
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
