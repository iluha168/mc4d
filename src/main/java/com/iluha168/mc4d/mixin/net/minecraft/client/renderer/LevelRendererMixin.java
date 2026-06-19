package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.client.renderer.LevelRenderer4;
import com.iluha168.mc4d.client.renderer.ShapeRenderer4;
import com.iluha168.mc4d.client.renderer.ViewArea4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
abstract
class LevelRendererMixin implements LevelRenderer4 {
	@Shadow
	private @Nullable ViewArea viewArea;

	@Shadow
	@Final
	LevelRenderState levelRenderState;

	@Unique private int lastCameraSectionW;
	@Unique private double lastCameraW;

	@Inject(method = "setLevel", at = @At("HEAD"))
	void setLevel(@Nullable ClientLevel level, CallbackInfo ci) {
		this.lastCameraSectionW = Integer.MIN_VALUE;
	}

	@Definition(id = "lastCameraSectionX", field = "Lnet/minecraft/client/renderer/LevelRenderer;lastCameraSectionX:I")
	@Expression("this.lastCameraSectionX != ?")
	@ModifyExpressionValue(method = "cullTerrain", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean cullTerrain_repositionCamera(boolean original, @Local(name = "cameraPos") Vec3 cameraPos, @Share("cameraSectionW") LocalIntRef cameraSectionW) {
		cameraSectionW.set(SectionPos.posToSectionCoord(((Vec4) cameraPos).w));
		return original || this.lastCameraSectionW != cameraSectionW.get();
	}
	@Definition(id = "lastCameraSectionX", field = "Lnet/minecraft/client/renderer/LevelRenderer;lastCameraSectionX:I")
	@Expression("this.lastCameraSectionX = @(?)")
	@Inject(method = "cullTerrain", at = @At("MIXINEXTRAS:EXPRESSION"))
	void cullTerrain_lastCameraSectionW(Camera camera, Frustum frustum, boolean spectator, CallbackInfo ci, @Share("cameraSectionW") LocalIntRef cameraSectionW) {
		this.lastCameraSectionW = cameraSectionW.get();
	}
	@Inject(method = "cullTerrain", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher;setCameraPosition(Lnet/minecraft/world/phys/Vec3;)V"
	)) // TODO remove when rewriting rendering to 4D
	void cullTerrain_repositionCamera(Camera camera, Frustum frustum, boolean spectator, CallbackInfo ci, @Local(name = "cameraPos") Vec3 cameraPos) {
		// Here we make client recalculate meshes for EVERY chunk when switching slices. See SectionCompilerMixin.
		final double cameraPosW = ((Vec4) cameraPos).w;
		if (Math.floor(cameraPosW * 16.0) != Math.floor(this.lastCameraW * 16.0)) {
			// Update the camera's section more frequently cuz it is cheap.
			this.setSectionDirty(
				SectionPos.blockToSectionCoord(cameraPos.x),
				SectionPos.blockToSectionCoord(cameraPos.y),
				SectionPos.blockToSectionCoord(cameraPos.z),
				SectionPos.blockToSectionCoord(cameraPosW),
				false
			);
		}
		// Ideally we should update all sections when camera moves, but that is way too taxing on performance.
		if (Math.floor(cameraPosW * 4.0) != Math.floor(this.lastCameraW * 4.0)) {
			//noinspection DataFlowIssue
			((ViewArea4) this.viewArea).setAllSectionWDirty(SectionPos.blockToSectionCoord(cameraPosW), true);
		}
		this.lastCameraW = Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MC4DClient.NEIGHBOURING_3D_SLICE_RENDERER)
			? cameraPosW
			: Double.NaN; // Rendering not enabled, setting a value that will always be different from camera pos, but same every tick
				// This way it dirties chunks when toggled on and off.
	}

	@Definition(id = "zOld", field = "Lnet/minecraft/world/entity/Entity;zOld:D")
	@Definition(id = "entity", local = @Local(type = Entity.class, name = "entity"))
	@Definition(id = "getZ", method = "Lnet/minecraft/world/entity/Entity;getZ()D")
	@Expression("entity.zOld = entity.getZ()")
	@Inject(method = "extractVisibleEntities", at = @At("MIXINEXTRAS:EXPRESSION"))
	void setWOld(
		Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState output, CallbackInfo ci,
		@Local(name = "entity") Entity entity
	) {
		Entity4 entity4 = (Entity4) entity;
		entity4.setWOld(entity4.getW());
	}

	// TODO submitEntities when 4D renderer
	// TODO extractVisibleBlockEntities when 4D renderer
	// TODO submitBlockEntities when 4D renderer

	@Redirect(method = "extractBlockDestroyAnimation", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;distToCenterSqr(DDD)D"
	))
	double extractBlockDestroyAnimation(BlockPos instance, double x, double y, double z, @Local(name = "cameraPos") Vec3 cameraPos) {
		return instance.distToCenterSqr(cameraPos);
	}

	// TODO submitBlockDestroyAnimation when 4D renderer
	// TODO renderBlockOutline when 4D renderer

	@Redirect(method = "renderHitOutline", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/ShapeRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDIF)V"
	))
	void renderHitOutline(PoseStack poseStack, VertexConsumer builder, VoxelShape shape, double x, double y, double z, int color, float width, @Local(name = "pos") BlockPos pos) {
		final double camW = ((Vec4) this.levelRenderState.cameraRenderState.pos).w;
		ShapeRenderer4.renderShape(poseStack, builder, shape, x, y, z, Vec4i.getW(pos) - camW, color, width);
	}

	@Redirect(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Z)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/LevelRenderer;setSectionDirty(IIIZ)V"
	))
	void setBlockDirty(LevelRenderer self, int sectionX, int sectionY, int sectionZ, boolean playerChanged, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int blockW = Vec4i.getW(pos);
		for (int w = blockW - 1; w <= blockW + 1; w++)
			this.setSectionDirty(sectionX, sectionY, sectionZ, SectionPos.blockToSectionCoord(w), playerChanged);
	}

	@Overwrite
	@Deprecated
	public void setBlocksDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
		throw Err4.arguments3("LevelRenderer4#setBlocksDirty");
	}
	@Override
	public void setBlocksDirty(int x0, int y0, int z0, int w0, int x1, int y1, int z1, int w1) {
		for (int w = w0 - 1; w <= w1 + 1; w++)
			for (int z = z0 - 1; z <= z1 + 1; z++)
				for (int x = x0 - 1; x <= x1 + 1; x++)
					for (int y = y0 - 1; y <= y1 + 1; y++)
						this.setSectionDirty(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(y), SectionPos.blockToSectionCoord(z), SectionPos.blockToSectionCoord(w));
	}

	@Redirect(method = "setBlockDirty(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/LevelRenderer;setBlocksDirty(IIIIII)V"
	))
	private void setBlockDirty(LevelRenderer self, int x0, int y0, int z0, int x1, int y1, int z1, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int w = Vec4i.getW(pos);
		this.setBlocksDirty(x0, y0, z0, w, x1, y1, z1, w);
	}

	@Overwrite
	@Deprecated
	public void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3("LevelRenderer4#setSectionDirtyWithNeighbors");
	}
	@Override
	public void setSectionDirtyWithNeighbors(int sectionX, int sectionY, int sectionZ, int sectionW) {
		this.setSectionRangeDirty(sectionX - 1, sectionY - 1, sectionZ - 1, sectionW - 1, sectionX + 1, sectionY + 1, sectionZ + 1, sectionW + 1);
	}

	@Overwrite
	@Deprecated
	public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int maxSectionX, int maxSectionY, int maxSectionZ) {
		throw Err4.arguments3("LevelRenderer4#setSectionRangeDirty");
	}
	@Override
	public void setSectionRangeDirty(int minSectionX, int minSectionY, int minSectionZ, int minSectionW, int maxSectionX, int maxSectionY, int maxSectionZ, int maxSectionW) {
		for (int w = minSectionW; w <= maxSectionW; w++)
			for (int z = minSectionZ; z <= maxSectionZ; z++)
				for (int x = minSectionX; x <= maxSectionX; x++)
					for (int y = minSectionY; y <= maxSectionY; y++)
						this.setSectionDirty(x, y, z, w);
	}

	@Overwrite
	@Deprecated
	public void setSectionDirty(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3("LevelRenderer4#setSectionDirty");
	}
	@Override
	public void setSectionDirty(int sectionX, int sectionY, int sectionZ, int sectionW) {
		this.setSectionDirty(sectionX, sectionY, sectionZ, sectionW, false);
	}

	@Overwrite
	@Deprecated
	private void setSectionDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged) {
		throw Err4.arguments3(null);
	}
	@Unique
	private void setSectionDirty(int sectionX, int sectionY, int sectionZ, int sectionW, boolean playerChanged) {
		//noinspection DataFlowIssue
		((ViewArea4) this.viewArea).setDirty(sectionX, sectionY, sectionZ, sectionW, playerChanged);
	}

	@Definition(id = "getX", method = "Lnet/minecraft/core/BlockPos;getX()I")
	@Expression("?.getX() != ?.getX()")
	@ModifyExpressionValue(method = "destroyBlockProgress", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean destroyBlockProgress(boolean original, @Local(name = "entry") BlockDestructionProgress entry, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return original || Vec4i.getW(entry.getPos()) != Vec4i.getW(pos);
	}
}
