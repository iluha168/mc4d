package com.iluha168.mc4d.mixin.net.minecraft.client.particle;

import net.minecraft.client.particle.DustParticleBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DustParticleBase.class)
abstract class DustParticleBaseMixin extends SingleQuadParticleMixin {
	@Override
	public void init_finish(double w, double wa) {
		super.init_finish(w, wa);
		this.wd *= 0.1F;
	}
}
