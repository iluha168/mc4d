package com.iluha168.mc4d.world.entity.vehicle.minecart;

import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link NewMinecartBehavior}.
 */
public interface NewMinecartBehavior4 {
	// TODO everything else

	/**
	 * Implemented by {@link NewMinecartBehavior.MinecartStep}.
	 */
	interface MinecartStep {
		static NewMinecartBehavior.MinecartStep from(Vec3 position, Vec3 movement, float yRot, float xRot, float wRot, float vRot, float weight) {
			final NewMinecartBehavior.MinecartStep step = new NewMinecartBehavior.MinecartStep(position, movement, yRot, xRot, weight);
			NewMinecartBehavior4.MinecartStep step4 = NewMinecartBehavior4.MinecartStep.as(step);
			step4.setWRot(wRot);
			step4.setVRot(vRot);
			return step;
		}

		static NewMinecartBehavior4.MinecartStep as(NewMinecartBehavior.MinecartStep step) {
			return (NewMinecartBehavior4.MinecartStep) (Object) step;
		}

		static float wRot(NewMinecartBehavior.MinecartStep step) {
			return NewMinecartBehavior4.MinecartStep.as(step).wRot();
		}
		static float vRot(NewMinecartBehavior.MinecartStep step) {
			return NewMinecartBehavior4.MinecartStep.as(step).vRot();
		}

		float wRot();
		float vRot();

		@ApiStatus.Internal
		void setWRot(float wRot);
		@ApiStatus.Internal
		void setVRot(float vRot);
	}

	// TODO everything else
}
