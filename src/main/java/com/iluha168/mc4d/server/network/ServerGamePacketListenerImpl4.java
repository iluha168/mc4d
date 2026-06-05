package com.iluha168.mc4d.server.network;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;

/**
 * {@link net.minecraft.server.network.ServerGamePacketListenerImpl} implements this interface.
 */
public interface ServerGamePacketListenerImpl4 {
	void teleport(Vec4 pos, float yRot, float xRot);

	boolean isEntityCollidingWithAnythingNew(LevelReader level, Entity entity, AABB oldAABB, Vec4 targetPos);
}
