package com.iluha168.mc4d.client.renderer.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/**
 * Implemented by {@link net.minecraft.client.renderer.entity.EntityRenderer}.
 */
public interface EntityRenderer4<T extends Entity, S extends EntityRenderState> {
	boolean shouldRender(T entity, Frustum culler, double camX, double camY, double camZ, double camW);

	AABB getBoundingBoxForCulling4(T entity);
}
