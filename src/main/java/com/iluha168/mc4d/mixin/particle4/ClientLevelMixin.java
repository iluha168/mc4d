package com.iluha168.mc4d.mixin.particle4;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClientLevel.class)
class ClientLevelMixin {
	/**
	 * @author iluha168
	 * @reason TODO remove this in favor of a 4D particle engine
	 */
	@Overwrite
	private void doAddParticle(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShowParticles, double x, double y, double z, double xd, double yd, double zd) {

	}
}
