package com.iluha168.mc4d.world.entity;

import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.phys.Vec3;

/**
 * Implemented by {@link InterpolationHandler}.
 */
public interface InterpolationHandler4 {
	float wRot();
	float vRot();

	void interpolateTo(Vec3 position, float yRot, float xRot, float wRot, float vRot);

	/**
	 * Implemented by {@link InterpolationHandler.InterpolationData}.
	 */
	interface InterpolationData {
		float wRot();
		void setWRot(float wRot);
		float vRot();
		void setVRot(float vRot);

		void addRotation(float yRot, float xRot, float wRot, float vRot);
	}
}
