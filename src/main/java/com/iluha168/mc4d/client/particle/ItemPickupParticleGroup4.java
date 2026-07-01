package com.iluha168.mc4d.client.particle;

import net.minecraft.client.particle.ItemPickupParticleGroup;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ItemPickupParticleGroup}.
 */
public interface ItemPickupParticleGroup4 {
	/**
	 * Implemented by {@link ItemPickupParticleGroup.ParticleInstance}.
	 */
	interface ParticleInstance {
		static ItemPickupParticleGroup4.ParticleInstance as(ItemPickupParticleGroup.ParticleInstance instance) {
			return (ItemPickupParticleGroup4.ParticleInstance) (Object) instance;
		}

		double wOffset();
		@ApiStatus.Internal
		void setWOffset(double wOffset);
	}
}
