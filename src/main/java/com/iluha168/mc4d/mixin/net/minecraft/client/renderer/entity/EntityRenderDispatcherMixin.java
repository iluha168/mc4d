package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.entity;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.renderer.SubmitNodeStorage4;
import com.iluha168.mc4d.client.renderer.entity.EntityRenderDispatcher4;
import com.iluha168.mc4d.client.renderer.entity.EntityRenderer4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.util.Err4;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderDispatcher.class)
abstract
class EntityRenderDispatcherMixin implements EntityRenderDispatcher4 {
	@Shadow
	public abstract <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S entityRenderState);

	@Shadow
	public abstract <S extends EntityRenderState> void submit(S renderState, CameraRenderState camera, double x, double y, double z, PoseStack poseStack, SubmitNodeCollector submitNodeCollector);

	@Shadow
	public abstract <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity);

	@Overwrite
	@Deprecated
	public <E extends Entity> boolean shouldRender(E entity, Frustum culler, double camX, double camY, double camZ) {
		throw Err4.arguments3("EntityRenderDispatcher4#shouldRender");
	}
	@Override
	public <E extends Entity> boolean shouldRender(E entity, Frustum culler, double camX, double camY, double camZ, double camW) {
		EntityRenderer<? super E, ?> renderer = this.getRenderer(entity);
		//noinspection unchecked
		return ((EntityRenderer4<? super E, ?>) renderer).shouldRender(entity, culler, camX, camY, camZ, camW);
	}

	@Override
	public <S extends EntityRenderState> void submit(S renderState, CameraRenderState camera, double x, double y, double z, double w, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
		EntityRenderer<?, ? super S> renderer = this.getRenderer(renderState);
		// TODO replace when 4D renderer

		if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MC4DClient.NEIGHBOURING_SLICE_ENTITY_RENDERER)) {
			final SubmitNodeStorage4 storage = (SubmitNodeStorage4) submitNodeCollector;
			Position4 pos = (Position4) renderer.getRenderOffset(renderState);
			final double relativeW = w + pos.w();
			final double sizeW = renderState.boundingBoxWidth * 0.5;
			final int previousTintColor = storage.entityTintColor();
			try {
				storage.setEntityTintColor(MC4DClient.getTintColor(relativeW / sizeW));
				this.submit(renderState, camera, x, y, z, poseStack, submitNodeCollector);
			} finally {
				storage.setEntityTintColor(previousTintColor);
			}
			return;
		}

		this.submit(renderState, camera, x, y, z, poseStack, submitNodeCollector);
	}
}
