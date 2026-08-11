package com.iluha168.mc4d.world.level.portal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;

/**
 * Implemented by {@link TeleportTransition}.
 */
public interface TeleportTransition4 {
	float wRot();
	@ApiStatus.Internal
	void setWRot(float wRot);

	float vRot();
	@ApiStatus.Internal
	void setVRot(float vRot);

	TeleportTransition withRotation(float yRot, float xRot, float wRot, float vRot);

	static TeleportTransition4 as(TeleportTransition transition) {
		return (TeleportTransition4) (Object) transition;
	}

	static TeleportTransition from(
		ServerLevel newLevel,
		Vec3 position,
		Vec3 deltaMovement,
		float yRot,
		float xRot,
		float wRot,
		float vRot,
		boolean missingRespawnBlock,
		boolean asPassenger,
		Set<Relative> relatives,
		TeleportTransition.PostTeleportTransition postTeleportTransition
	) {
		final TeleportTransition transition = new TeleportTransition(newLevel, position, deltaMovement, yRot, xRot, missingRespawnBlock, asPassenger, relatives, postTeleportTransition);
		final TeleportTransition4 transition4 = TeleportTransition4.as(transition);
		transition4.setWRot(wRot);
		transition4.setVRot(vRot);
		return transition;
	}
	static TeleportTransition from(
		ServerLevel newLevel,
		Vec3 pos,
		Vec3 speed,
		float yRot,
		float xRot,
		float wRot,
		float vRot,
		TeleportTransition.PostTeleportTransition postTeleportTransition
	) {
		return TeleportTransition4.from(newLevel, pos, speed, yRot, xRot, wRot, vRot, Set.of(), postTeleportTransition);
	}
	static TeleportTransition from(
		ServerLevel newLevel,
		Vec3 pos,
		Vec3 speed,
		float yRot,
		float xRot,
		float wRot,
		float vRot,
		Set<Relative> relatives,
		TeleportTransition.PostTeleportTransition postTeleportTransition
	) {
		return TeleportTransition4.from(newLevel, pos, speed, yRot, xRot, wRot, vRot, false, false, relatives, postTeleportTransition);
	}
}
