package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.BaseAshSmokeParticle4;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseAshSmokeParticle.class)
abstract class BaseAshSmokeParticleMixin extends SingleQuadParticleMixin implements BaseAshSmokeParticle4 {
	@Inject(method = "<init>", at = @At("TAIL"))
	void init(ClientLevel level, double x, double y, double z, float dirX, float dirY, float dirZ, double xa, double ya, double za, float scale, SpriteSet sprites, float colorRandom, int maxLifetime, float gravity, boolean hasPhysics, CallbackInfo ci) {
		this.initIncomplete = 3;
	}

	@Override
	public void init_finish(double w, float dirW, double wa) {
		if (this.initIncomplete != 3) {
			throw new IllegalStateException("Programmer error: use BaseAshSmokeParticle4#init_finish.");
		}
		try {
			this.initIncomplete = 2;
			this.init_finish(w, 0.0);
			this.wd *= dirW;
			this.wd += wa;
		} catch (Throwable e) {
			this.initIncomplete = 3;
			throw e;
		}
	}
}
