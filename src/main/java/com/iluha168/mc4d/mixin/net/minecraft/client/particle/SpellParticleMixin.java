package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.client.particle.SpellParticle4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpellParticle.class)
abstract class SpellParticleMixin extends SingleQuadParticleMixin implements SpellParticle4 {
	@Shadow
	@Final
	private static RandomSource RANDOM;

	@Shadow
	protected abstract boolean isCloseToScopingPlayer();

	@Shadow
	protected abstract void setAlpha(float alpha);

	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SpellParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(SpellParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SpellParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(SpellParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/SpellParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(SpellParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/SpellParticle;isCloseToScopingPlayer()Z"))
	boolean init_postpone_isCloseToScopingPlayer(SpellParticle instance) {
		this.initIncomplete = 3;
		return false;
	}
	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		if (this.initIncomplete != 3) {
			throw new IllegalStateException("Programmer error: use SpellParticle4#init_finish.");
		}
		try {
			this.initIncomplete = 2;
			super.init_finish(w, 0.5 - RANDOM.nextDouble());
			this.yd *= 0.2F;
			if (xa == 0.0 && za == 0.0 && wa == 0.0) {
				this.xd *= 0.1F;
				this.zd *= 0.1F;
				this.wd *= 0.1F;
			}
			if (this.isCloseToScopingPlayer()) {
				this.setAlpha(0.0F);
			}
		} catch (Throwable e) {
			this.initIncomplete = 3;
			throw e;
		}
	}

	@Redirect(method = "isCloseToScopingPlayer", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(DDD)D"
	))
	double isCloseToScopingPlayer(Vec3 eyePosition, double x, double y, double z) {
		return ((Vec4) eyePosition).distanceToSqr(x, y, z, this.w());
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.SpellParticle$InstantProvider",
		"net.minecraft.client.particle.SpellParticle$MobEffectProvider",
		"net.minecraft.client.particle.SpellParticle$Provider",
		"net.minecraft.client.particle.SpellParticle$WitchProvider",
	})
	abstract static class ProviderMixin<T extends ParticleOptions> implements ParticleProvider<T>, ParticleProvider4<T> {
		@Override
		public @Nullable Particle createParticle(T options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((SpellParticle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
