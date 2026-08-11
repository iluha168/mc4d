package com.iluha168.mc4d.world.entity;

/**
 * Implemented by {@link net.minecraft.world.entity.LivingEntity}.
 */
public interface LivingEntity4 {
	float getWBodyRot();

	void knockback(double power, double xd, double zd, double wd);

	void lerpHeadRotationStep(int lerpHeadSteps, double targetYHeadRot, double targetWHeadRot);
}
