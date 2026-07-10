package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import net.minecraft.client.particle.DustParticleBase;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DustParticleBase.class)
abstract class DustParticleBaseMixin extends SingleQuadParticleMixin {
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/DustParticleBase;xd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_xd(DustParticleBase<?> instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/DustParticleBase;yd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_yd(DustParticleBase<?> instance, double value) {}
	@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/DustParticleBase;zd:D", opcode = Opcodes.PUTFIELD))
	void init_postpone_zd(DustParticleBase<?> instance, double value) {}

	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w, wa);
		this.xd *= 0.1F;
		this.yd *= 0.1F;
		this.zd *= 0.1F;
		this.wd *= 0.1F;
	}
}
