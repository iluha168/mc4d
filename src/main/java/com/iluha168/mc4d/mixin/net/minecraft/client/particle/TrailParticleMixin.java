package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TrailParticle;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrailParticle.class)
abstract class TrailParticleMixin extends SingleQuadParticleMixin {
	@Shadow
	@Final
	private Vec3 target;

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/TrailParticle;zo:D")
	@Expression("this.zo = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/TrailParticle;z:D")
	@Expression("this.z = @(?)")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_w(CallbackInfo ci, @Local(name = "alpha") double alpha) {
		this.w = Mth.lerp(alpha, this.w, ((Vec4) this.target).w);
	}

	@Mixin(TrailParticle.Provider.class)
	abstract static class ProviderMixin implements ParticleProvider4<TrailParticleOption> {
		@Shadow
		public abstract Particle createParticle(TrailParticleOption options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random);

		@Override
		public @Nullable Particle createParticle(
			TrailParticleOption options, ClientLevel level,
			double x, double y, double z, double w,
			double xAux, double yAux, double zAux, double wAux,
			RandomSource random
		) {
			final Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			((Particle4) particle).init_finish(w, wAux);
			return particle;
		}
	}
}
