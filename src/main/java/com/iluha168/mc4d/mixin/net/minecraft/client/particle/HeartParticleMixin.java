package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeartParticle.class)
abstract class HeartParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/HeartParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(HeartParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/HeartParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(HeartParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/HeartParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(HeartParticle instance, double value) {}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
		this.xd *= 0.01F;
		this.yd *= 0.01F;
		this.zd *= 0.01F;
		this.wd *= 0.01F;
		this.yd += 0.1;
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.HeartParticle$AngryVillagerProvider",
		"net.minecraft.client.particle.HeartParticle$Provider",
	})
	abstract static class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, 0.0, 0.0, 0.0, 0.0);
			return particle;
		}
	}
}
