package com.iluha168.mc4d.world.entity;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Relative;

import java.util.Set;

/**
 * All {@link net.minecraft.world.entity.Entity} instances implement this interface.
 */
public interface Entity4 {
	void setPosRaw(Vec4 newPos);

	void absSnapTo(Vec4 newPos);
	void absSnapTo(Vec4 newPos, float yRot, float xRot);

	void doCheckFallDamage(Vec4 movement, boolean onGround);

	boolean teleportTo(ServerLevel level, Vec4 newPos, Set<Relative> relatives, float newYRot, float newXRot, boolean resetCamera);

	double getW();

	void setWO(double wo);
	double getWO();
	void setWOld(double wOld);
}
