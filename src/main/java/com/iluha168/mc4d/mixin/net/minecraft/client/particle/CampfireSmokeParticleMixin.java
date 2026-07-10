package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.Particle4;
import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireSmokeParticle.class)
abstract class CampfireSmokeParticleMixin extends SingleQuadParticleMixin {
	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w);
		this.wd = wa;
	}

	@Definition(id = "zo", field = "Lnet/minecraft/client/particle/CampfireSmokeParticle;zo:D")
	@Definition(id = "z", field = "Lnet/minecraft/client/particle/CampfireSmokeParticle;z:D")
	@Expression("this.zo = this.z")
	@Inject(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	void tick_wo(CallbackInfo ci) {
		this.wo = this.w();
	}
	@Redirect(method = "tick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/particle/CampfireSmokeParticle;move(DDD)V"
	))
	void tick_move(CampfireSmokeParticle instance, double x, double y, double z) {
		this.wd += this.random.nextFloat() / 5000.0F * (this.random.nextBoolean() ? 1 : -1);
		((Particle4) instance).move(x, y, z, this.wd);
	}

	@Mixin(targets = {
		"net.minecraft.client.particle.CampfireSmokeParticle$CosyProvider",
		"net.minecraft.client.particle.CampfireSmokeParticle$SignalProvider",
	})
	static abstract class ProviderMixin implements ParticleProvider<SimpleParticleType>, ParticleProvider4<SimpleParticleType> {
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			Particle particle = this.createParticle(options, level, x, y, z, xAux, yAux, zAux, random);
			//noinspection DataFlowIssue
			((Particle4) particle).init_finish(w, wAux);
			return particle;
		}
	}
}
