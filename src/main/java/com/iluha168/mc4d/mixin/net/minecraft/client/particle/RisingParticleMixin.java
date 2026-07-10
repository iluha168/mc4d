package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import com.iluha168.mc4d.client.particle.RisingParticle4;
import net.minecraft.client.particle.RisingParticle;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RisingParticle.class)
abstract class RisingParticleMixin extends SingleQuadParticleMixin implements RisingParticle4 {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/RisingParticle;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(RisingParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/RisingParticle;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(RisingParticle instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/RisingParticle;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(RisingParticle instance, double value) {
		this.initIncomplete = 3;
	}

	@Override
	public void init_finish(double w, double xa, double ya, double za, double wa) {
		if (this.initIncomplete != 3) {
			throw new IllegalStateException("Programmer error: use RisingParticle4#init_finish.");
		}
		try {
			this.initIncomplete = 2;
			super.init_finish(w, wa);
			this.xd = this.xd * 0.01F + xa;
			this.yd = this.yd * 0.01F + ya;
			this.zd = this.zd * 0.01F + za;
			this.wd = this.wd * 0.01F + wa;
			this.w = this.w + (this.random.nextFloat() - this.random.nextFloat()) * 0.05F;
		} catch (Throwable e) {
			this.initIncomplete = 3;
			throw e;
		}
	}
}
