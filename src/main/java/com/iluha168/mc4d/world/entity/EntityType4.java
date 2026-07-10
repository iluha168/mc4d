package com.iluha168.mc4d.world.entity;

import com.iluha168.mc4d.world.phys.AABB4;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Implemented by {@link EntityType}.
 */
public interface EntityType4<T extends Entity> {
	AABB4 getSpawnAABB(double x, double y, double z, double w);
}
