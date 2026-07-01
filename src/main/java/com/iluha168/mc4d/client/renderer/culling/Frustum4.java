package com.iluha168.mc4d.client.renderer.culling;

/**
 * Implemented by {@link net.minecraft.client.renderer.culling.Frustum}.
 */
public interface Frustum4 {
	void prepare(double camX, double camY, double camZ, double camW);

	double getCamW();
}
