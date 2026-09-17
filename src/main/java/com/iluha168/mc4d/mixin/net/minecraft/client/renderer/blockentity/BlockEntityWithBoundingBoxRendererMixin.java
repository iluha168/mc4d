package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.api.net.minecraft.world.phys.AABB4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.renderer.blockentity.BlockEntityWithBoundingBoxRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithBoundingBoxRenderer.class)
class BlockEntityWithBoundingBoxRendererMixin {
	@Definition(id = "BOX_AND_INVISIBLE_BLOCKS", field = "Lnet/minecraft/world/level/block/entity/BoundingBoxRenderable$Mode;BOX_AND_INVISIBLE_BLOCKS:Lnet/minecraft/world/level/block/entity/BoundingBoxRenderable$Mode;")
	@Expression("? == BOX_AND_INVISIBLE_BLOCKS")
	@ModifyExpressionValue(method = "extract", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean extract_wOffset(
		boolean original,
		@Local(name = "size") Vec3i size,
		@Local(name = "startingPos") BlockPos startingPos,
		@Share("wOffset") LocalIntRef wOffset
	) {
		final int offset = Mth.floor(MC4DClient.cameraW()) - Vec4i.getW(startingPos);
		wOffset.set(offset);
		// Do not render invisible blocks outside camera slice.
		return original && 0 <= offset && offset < Vec4i.getW(size);
	}
	@Redirect(method = "extract", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos extract_offset(BlockPos instance, int x, int y, int z, @Share("wOffset") LocalIntRef wOffset) {
		return ((BlockPos4) instance).offset(x, y, z, wOffset.get());
	}

	@Definition(id = "getZ", method = "Lnet/minecraft/core/Vec3i;getZ()I")
	@Expression("?.getZ() >= 1")
	@ModifyExpressionValue(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BlockEntityWithBoundingBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean submit_size(
		boolean original,
		@Local(argsOnly = true, name = "state") BlockEntityWithBoundingBoxRenderState state,
		@Local(name = "size") Vec3i size
	) {
		if (original && Vec4i.getW(size) >= 1) {
			final int minW = Vec4i.getW(state.blockPos) + Vec4i.getW(state.box.localPos());
			final int maxW = minW + Vec4i.getW(state.box.size());
			final int cameraW = Mth.floor(MC4DClient.cameraW());
			return minW <= cameraW && cameraW < maxW;
		}
		return false;
	}
	@Redirect(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/BlockEntityWithBoundingBoxRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB submit_box(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(name = "pos") BlockPos pos,
		@Local(name = "far") BlockPos far
	) {
		return new AABB4(
			minX, minY, minZ, Vec4i.getW(pos),
			maxX, maxY, maxZ, Vec4i.getW(far)
		);
	}

	@Inject(method = "renderInvisibleBlocks", at = @At("HEAD"))
	void renderInvisibleBlocks_cameraSlice(BlockEntityWithBoundingBoxRenderState state, BlockPos localPos, Vec3i size, CallbackInfo ci, @Share("w") LocalIntRef w) {
		w.set(Mth.floor(MC4DClient.cameraW()));
	}
	@Redirect(method = "renderInvisibleBlocks", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB renderInvisibleBlocks_marker(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ,
		@Local(name = "scale") float scale,
		@Share("w") LocalIntRef w
	) {
		final int cameraW = w.get();
		return new AABB4(
			minX, minY, minZ, cameraW + 0.45F - scale,
			maxX, maxY, maxZ, cameraW + 0.55F + scale
		);
	}

	// TODO renderStructureVoids - never used??

	@Redirect(method = "getRenderBoundingBox(Lnet/minecraft/world/level/block/entity/BlockEntity;)Lnet/minecraft/world/phys/AABB;", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/AABB;INFINITE:Lnet/minecraft/world/phys/AABB;",
		opcode = Opcodes.GETSTATIC
	))
	AABB getRenderBoundingBox() {
		return AABB4.INFINITE;
	}
}
