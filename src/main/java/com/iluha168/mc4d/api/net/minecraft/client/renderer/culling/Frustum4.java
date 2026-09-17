package com.iluha168.mc4d.api.net.minecraft.client.renderer.culling;

import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link net.minecraft.client.renderer.culling.Frustum}.
 */
public interface Frustum4 {
	@ApiStatus.Internal
	double getCamW();

	void prepare(double camX, double camY, double camZ, double camW);

	int cubeInFrustum(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW
	);

	boolean pointInFrustum(double x, double y, double z, double minW, double maxW);
}
