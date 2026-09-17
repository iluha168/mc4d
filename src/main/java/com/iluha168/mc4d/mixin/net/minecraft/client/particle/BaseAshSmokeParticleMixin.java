package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.api.net.minecraft.client.particle.BaseAshSmokeParticle4;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseAshSmokeParticle.class)
abstract class BaseAshSmokeParticleMixin extends SingleQuadParticleMixin implements BaseAshSmokeParticle4 {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BaseAshSmokeParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(BaseAshSmokeParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BaseAshSmokeParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(BaseAshSmokeParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/BaseAshSmokeParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(BaseAshSmokeParticle instance, double value) {}
	@Inject(method = "<init>", at = @At("TAIL"))
	void init(CallbackInfo ci) {
		this.initIncomplete = 3;
	}

	@Override
	public void init_finish(
		double w,
		float dirX, float dirY, float dirZ, float dirW,
		double xa, double ya, double za, double wa
	) {
		if (this.initIncomplete != 3) {
			throw new IllegalStateException("Programmer error: BaseAshSmokeParticle4#init_finish called twice.");
		}
		try {
			this.initIncomplete = 2;
			super.init_finish(w, 0.0, 0.0, 0.0, 0.0);
			this.xd *= dirX;
			this.yd *= dirY;
			this.zd *= dirZ;
			this.wd *= dirW;
			this.xd += xa;
			this.yd += ya;
			this.zd += za;
			this.wd += wa;
		} catch (Throwable e) {
			this.initIncomplete = 3;
			throw e;
		}
	}
}
