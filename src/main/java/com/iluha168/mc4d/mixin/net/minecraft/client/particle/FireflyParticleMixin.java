package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FireflyParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FireflyParticle.class)
abstract class FireflyParticleMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/FireflyParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(FireflyParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/FireflyParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(FireflyParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/FireflyParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(FireflyParticle instance, double value) {}
	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w, wa);
		this.yd *= 0.8F;
		this.xd *= 0.8F;
		this.zd *= 0.8F;
		this.wd *= 0.8F;
	}

	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tick_containing(double x, double y, double z) {
		return BlockPos4.containing(x, y, z, this.w());
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/FireflyParticle;setParticleSpeed(DDD)V"
	))
	void tick_setParticleSpeed(FireflyParticle instance, double xd, double yd, double zd) {
		((Particle4) instance).setParticleSpeed(xd, yd, zd, -0.05F + 0.1F * this.random.nextFloat());
	}

	@Mixin(FireflyParticle.FireflyProvider.class)
	abstract static class FireflyProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, 0.5 - random.nextDouble());
			return particle;
		}
	}
}
