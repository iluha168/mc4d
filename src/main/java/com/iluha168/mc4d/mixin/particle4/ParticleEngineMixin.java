package com.iluha168.mc4d.mixin.particle4;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ParticleEngine.class)
class ParticleEngineMixin {
	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of a 4D particle engine
	 */
	@Overwrite
	public void add(Particle p) {}
}
