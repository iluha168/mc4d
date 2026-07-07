package com.iluha168.mc4d.client;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;

/**
 * Implemented by {@link Camera}.
 */
public interface Camera4 {
	/** Distance in blocks the camera detaches from its entity's eyes while the camera offset key combo is held. */
	double OFFSET_DISTANCE = 1.0;
	/** Fraction of distance the camera travels towards the target per tick. */
	double OFFSET_DISTANCE_REACH_SPEED = 0.3;
	/** Extra distance kept between the camera and geometry during peeking, to avoid xray. */
	double OFFSET_COLLISION_MARGIN = 0.1;

	void setOffsetDirection(@NonNull Vec4 target);
	void unsetOffsetDirection();

	/** @return the collision-clamped offset that was applied to the camera position in the latest rendered frame. */
	@NonNull Vec4 getOffsetApplied();
	/** @return whether the offset is non-zero. */
	boolean hasOffset();

	/** {@link #getOffsetApplied()} of the main camera. */
	static @NonNull Vec4 getMainCameraOffset() {
		return ((Camera4) Minecraft.getInstance().gameRenderer.getMainCamera()).getOffsetApplied();
	}
	/** {@link #hasOffset()} of the main camera. */
	static boolean mainCameraHasOffset() {
		return ((Camera4) Minecraft.getInstance().gameRenderer.getMainCamera()).hasOffset();
	}
}
