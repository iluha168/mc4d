package com.iluha168.mc4d.mixin.net.neoforged.neoforge.client;

import com.iluha168.mc4d.client.renderer.ShapeRenderer4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.BlockEntityRenderBoundsDebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEntityRenderBoundsDebugRenderer.class)
class BlockEntityRenderBoundsDebugRendererMixin {
	@Redirect(method = "onRenderLevelStage", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/ShapeRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDIF)V"
	))
	private static void onRenderLevelStage(PoseStack poseStack, VertexConsumer builder, VoxelShape shape, double x, double y, double z, int color, float width, @Local(name = "camera") Vec3 camera) {
		ShapeRenderer4.renderShape(poseStack, builder, shape, x, y, z, -((Vec4) camera).w, color, width);
	}
}
