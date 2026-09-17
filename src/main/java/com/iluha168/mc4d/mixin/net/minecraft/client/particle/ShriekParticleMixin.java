package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.core.particles.ShriekParticleOption;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShriekParticle.class)
abstract class ShriekParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ShriekParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(ShriekParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ShriekParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(ShriekParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ShriekParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(ShriekParticle instance, double value) {}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
		this.xd = 0.0;
		this.yd = 0.1;
		this.zd = 0.0;
		this.wd = 0.0;
	}

	@Mixin(ShriekParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider<ShriekParticleOption>, ParticleProvider4<ShriekParticleOption> {
		@Override
		public @Nullable Particle createParticle(ShriekParticleOption options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, 0.0, 0.0, 0.0, 0.0);
			return particle;
		}
	}
}
