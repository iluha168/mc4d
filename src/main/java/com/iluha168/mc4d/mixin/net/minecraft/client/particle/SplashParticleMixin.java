package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.ParticleProvider4;
import com.iluha168.mc4d.client.particle.SplashParticle4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SplashParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SplashParticle.class)
abstract class SplashParticleMixin extends WaterDropParticleMixin implements SplashParticle4 {
	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		if (this.initIncomplete != 3) {
			throw new IllegalStateException("Programmer error: use init_finish of this SplashParticle's subclass?");
		}
		try {
			this.initIncomplete = 2;
			super.init_finish(w, wa);
			if (ya == 0.0 && (xa != 0.0 || za != 0.0 || wa != 0.0)) {
				this.xd = xa;
				this.yd = 0.1;
				this.zd = za;
				this.wd = wa;
			}
		} catch (Throwable e) {
			this.initIncomplete = 3;
			throw e;
		}
	}

	@Definition(id = "ya", local = @Local(type = double.class, name = "ya", argsOnly = true))
	@Expression("ya == 0.0")
	@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean init(boolean original) {
		this.initIncomplete = 3;
		return false;
	}

	@Mixin(SplashParticle.Provider.class)
	static class ProviderMixin implements ParticleProvider4<SimpleParticleType> {
		@Shadow
		@Final
		private SpriteSet sprite;

		@Overwrite
		@Deprecated
		public Particle createParticle(
			SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random
		) {
			throw Err4.arguments3("ParticleProvider4#createParticle");
		}
		@Override
		public @Nullable Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double w, double xAux, double yAux, double zAux, double wAux, RandomSource random) {
			SplashParticle particle = new SplashParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random));
			((SplashParticle4) particle).init_finish(w, xAux, yAux, zAux, wAux);
			return particle;
		}
	}
}
