package com.iluha168.mc4d.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;

/**
 * Implemented by {@link net.minecraft.client.renderer.entity.EntityRenderDispatcher}.
 */
public interface EntityRenderDispatcher4 {
	<E extends Entity> boolean shouldRender(E entity, Frustum culler, double camX, double camY, double camZ, double camW);

	<S extends EntityRenderState> void submit(
		S renderState, CameraRenderState camera, double x, double y, double z, double w, PoseStack poseStack, SubmitNodeCollector submitNodeCollector
	);
}
