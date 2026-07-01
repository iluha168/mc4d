package com.iluha168.mc4d.world.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Relative;

import java.util.Set;

/**
 * All {@link net.minecraft.world.entity.Entity} instances implement this interface.
 */
public interface Entity4 {
	void setWO(double wo);
	double getWO();

	void setWOld(double wOld);
	double wOld();

	void syncPacketPositionCodec(double x, double y, double z, double w);

	boolean isFree(double xa, double ya, double za, double wa);

	void doCheckFallDamage(double xa, double ya, double za, double wa, boolean onGround);

	void absSnapTo(double x, double y, double z, double w);
	void absSnapTo(double x, double y, double z, double w, float yRot, float xRot);

	void snapTo(double x, double y, double z, double w);
	void snapTo(double x, double y, double z, double w, float yRot, float xRot);

	double distanceToSqr(double x2, double y2, double z2, double w2);

	void push(double xa, double ya, double za, double wa);

	boolean shouldRender(double camX, double camY, double camZ, double camW);

	void moveTowardsClosestSpace(double x, double y, double z, double w);

	boolean teleportTo(ServerLevel level, double x, double y, double z, double w, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera);

	int getBlockW();
	double getW();

	void setPosRaw(double x, double y, double z, double w);

}
