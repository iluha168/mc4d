package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.Particle4;
import com.iluha168.mc4d.api.net.minecraft.client.particle.ParticleProvider4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.FlyStraightTowardsParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlyStraightTowardsParticle.class)
abstract class FlyStraightTowardsParticleMixin extends SingleQuadParticleMixin {
	@Unique private double wStart;

	@Override
	public void init_finish(double w, double xd, double yd, double zd, double wd) {
		this.wd = wd;
		this.wStart = w;
		super.init_finish(w + wd);
	}

	@SuppressWarnings({"RedundantMethodOverride", "deprecation"})
	@Overwrite
	@Deprecated
	public void move(double xa, double ya, double za) {
		throw Err4.arguments3("Particle4#move");
	}
	@Override
	public void move(double xa, double ya, double za, double wa) {}

	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/FlyStraightTowardsParticle;zo:D",
		opcode = Opcodes.PUTFIELD
	))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Inject(method = "tick", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/client/particle/FlyStraightTowardsParticle;z:D",
		opcode = Opcodes.PUTFIELD
	))
	void tick_w(CallbackInfo ci, @Local(name = "posAlpha") float posAlpha) {
		this.w = this.wStart + this.wd * posAlpha;
	}

	@Mixin(FlyStraightTowardsParticle.OminousSpawnProvider.class)
	abstract static class OminousSpawnProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		public abstract Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
