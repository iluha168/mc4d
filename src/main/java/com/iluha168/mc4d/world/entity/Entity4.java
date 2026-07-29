package com.iluha168.mc4d.world.entity;

import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Set;

/**
 * All {@link net.minecraft.world.entity.Entity} instances implement this interface.
 */
public interface Entity4 {
	void setWO(double wo);
	double getWO();

	void setWOld(double wOld);
	double wOld();

	float getWRot();
	void setWRot(float wRot);
	float getWRotO();
	void setWRotO(float wRotO);
	float getWRot(float partialTick);
	float getViewWRot(float partialTick);

	float getVRot();
	void setVRot(float vRot);
	float getVRotO();
	void setVRotO(float vRotO);
	float getVRot(float partialTick);
	float getViewVRot(float partialTick);

	void syncPacketPositionCodec(double x, double y, double z, double w);

	void setRot(float yRot, float xRot, float wRot, float vRot);

	void turn(double xo, double yo, double wo, double vo);

	boolean isFree(double xa, double ya, double za, double wa);

	void doCheckFallDamage(double xa, double ya, double za, double wa, boolean onGround);

	static Vec4 getInputVector(Vec4 input, float speed, float yRot, float wRot, float vRot) {
		final double length = input.lengthSqr();
		if (length < AABB4.EPSILON) {
			return Vec4.ZERO;
		}
		final Vec4 movement = (length > 1.0 ? input.normalize() : input).scale(speed);
		final float vSin = Mth.sin(vRot * Mth.DEG_TO_RAD);
		final float vCos = Mth.cos(vRot * Mth.DEG_TO_RAD);
		final double adjustedMovementX = movement.x * vCos + movement.w * vSin;
		final double adjustedMovementW = movement.w * vCos - movement.x * vSin;
		final float wSin = Mth.sin(wRot * Mth.DEG_TO_RAD);
		final float wCos = Mth.cos(wRot * Mth.DEG_TO_RAD);
		final double adjustedMovementZ = movement.z * wCos - adjustedMovementW * wSin;
		final float ySin = Mth.sin(yRot * Mth.DEG_TO_RAD);
		final float yCos = Mth.cos(yRot * Mth.DEG_TO_RAD);
		return new Vec4(
			adjustedMovementX * yCos - adjustedMovementZ * ySin,
			movement.y,
			adjustedMovementZ * yCos + adjustedMovementX * ySin,
			movement.z * wSin + adjustedMovementW * wCos
		);
	}

	void absSnapTo(double x, double y, double z, double w);
	void absSnapTo(double x, double y, double z, double w, float yRot, float xRot, float wRot, float vRot);
	void absSnapRotationTo(float yRot, float xRot, float wRot, float vRot);

	void snapTo(double x, double y, double z, double w);
	void snapTo(BlockPos spawnPos, float yRot, float xRot, float wRot, float vRot);
	void snapTo(Vec3 spawnPos, float yRot, float xRot, float wRot, float vRot);
	void snapTo(double x, double y, double z, double w, float yRot, float xRot, float wRot, float vRot);

	void setOldPosAndRot(Vec3 position, float yRot, float xRot, float wRot, float vRot);

	double distanceToSqr(double x2, double y2, double z2, double w2);

	void push(double xa, double ya, double za, double wa);

	Vec4 calculateViewVector(float xRot, float yRot, float wRot);

	boolean shouldRender(double camX, double camY, double camZ, double camW);

	void moveOrInterpolateTo(Vec3 position, float yRot, float xRot, float wRot, float vRot);
	void moveOrInterpolateTo(float yRot, float xRot, float wRot, float vRot);
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	void moveOrInterpolateTo(Optional<Vec3> position, Optional<Float> yRot, Optional<Float> xRot, Optional<Float> wRot, Optional<Float> vRot);

	void lerpHeadTo(float yHeadRot, float wHeadRot, int steps);

	void moveTowardsClosestSpace(double x, double y, double z, double w);

	float getWHeadRot();
	void setYHeadRot(float yHeadRot, float wHeadRot);
	void setYBodyRot(float yBodyRot, float wBodyRot);

	void forceSetRotation(float yRot, boolean relativeY, float xRot, boolean relativeX, float wRot, boolean relativeW, float vRot, boolean relativeV);

	boolean teleportTo(ServerLevel level, double x, double y, double z, double w, Set<Relative> relatives, float newYRot, float newXRot, float newWRot, float newVRot, boolean resetCamera);
	void dismountTo(double x, double y, double z, double w);
	void teleportTo(double x, double y, double z, double w);
	void teleportRelative(double dx, double dy, double dz, double dw);

	static Vec4 getCollisionHorizontalEscapeVector(double colliderWidth, double collidingWidth, float yRot, float wRot) {
		final double distance = (colliderWidth + collidingWidth + Mth.EPSILON) / 2.0;
		final float wCos = Mth.cos(wRot * Mth.DEG_TO_RAD);
		final float directionX = -Mth.sin(yRot * Mth.DEG_TO_RAD) * wCos;
		final float directionZ = Mth.cos(yRot * Mth.DEG_TO_RAD) * wCos;
		final float directionW = Mth.sin(wRot * Mth.DEG_TO_RAD);
		final float scale = Math.max(Math.max(Math.abs(directionX), Math.abs(directionZ)), Math.abs(directionW));
		return new Vec4(directionX * distance / scale, 0.0, directionZ * distance / scale, directionW * distance / scale);
	}

	int getBlockW();
	double getW();
	double getW(double progress);
	double getRandomW(double spread);

	void setPosRaw(double x, double y, double z, double w);

	void lerpPositionAndRotationStep(int stepsToTarget, double targetX, double targetY, double targetZ, double targetW, double targetYRot, double targetXRot, double targetWRot, double targetVRot);
}
