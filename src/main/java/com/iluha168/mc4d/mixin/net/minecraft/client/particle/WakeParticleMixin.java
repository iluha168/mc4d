package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WakeParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WakeParticle.class)
abstract class WakeParticleMixin extends SingleQuadParticleMixin {
	// Proxy [xyz]d as this.[xyz], and stash for later.
	// Those fields are vacant during construction because of ParticleMixin.
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WakeParticle;xd:D", opcode = Opcodes.GETFIELD))
	double init_postpone_xd_read(WakeParticle instance) {
		return this.x;
	}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WakeParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd_write(WakeParticle instance, double value) {
		this.x = value;
	}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WakeParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd_write(WakeParticle instance, double value) {
		this.y = value;
	}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WakeParticle;zd:D", opcode = Opcodes.GETFIELD))
	double init_postpone_zd_read(WakeParticle instance) {
		return this.z;
	}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/WakeParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd_write(WakeParticle instance, double value) {
		this.z = value;
	}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		final double xd = this.x;
		final double yd = this.y;
		final double zd = this.z; // Destash and overwrite.
		super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
		// Vanilla does some calculations here that do not even matter, why...
		this.xd = xd;
		this.yd = yd;
		this.zd = zd;
		this.wd = wa;
	}

	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/WakeParticle;xo:D",
		opcode = Opcodes.PUTFIELD
	))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/WakeParticle;move(DDD)V"
	))
	void tick_move(WakeParticle instance, double xa, double ya, double za) {
		((Particle4) instance).move(xa, ya, za, this.wd);
		this.wd *= 0.98F;
	}

	@Mixin(WakeParticle.Provider.class)
	static abstract class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		@Final
		private SpriteSet sprites;

		@Overwrite
		@Deprecated
		public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = new WakeParticle(level, x, y, z, xAux, yAux, zAux, this.sprites);
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
