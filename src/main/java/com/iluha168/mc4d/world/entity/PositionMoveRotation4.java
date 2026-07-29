package com.iluha168.mc4d.world.entity;

import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link PositionMoveRotation}.
 */
public interface PositionMoveRotation4 {
	float wRot();
	@ApiStatus.Internal
	void setWRot(float wRot);

	float vRot();
	@ApiStatus.Internal
	void setVRot(float vRot);

	PositionMoveRotation withRotation(float yRot, float xRot, float wRot, float vRot);

	static PositionMoveRotation4 as(PositionMoveRotation positionMoveRotation) {
		return (PositionMoveRotation4) (Object) positionMoveRotation;
	}

	static float wRot(PositionMoveRotation positionMoveRotation) {
		return PositionMoveRotation4.as(positionMoveRotation).wRot();
	}
	static float vRot(PositionMoveRotation positionMoveRotation) {
		return PositionMoveRotation4.as(positionMoveRotation).vRot();
	}

	static PositionMoveRotation from(Vec3 position, Vec3 deltaMovement, float yRot, float xRot, float wRot, float vRot) {
		final PositionMoveRotation destination = new PositionMoveRotation(position, deltaMovement, yRot, xRot);
		final PositionMoveRotation4 destination4 = PositionMoveRotation4.as(destination);
		destination4.setWRot(wRot);
		destination4.setVRot(vRot);
		return destination;
	}
}
