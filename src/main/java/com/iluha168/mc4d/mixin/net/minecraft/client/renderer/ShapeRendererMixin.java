package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.util.Err4;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ShapeRenderer.class)
class ShapeRendererMixin {
	@Overwrite
	@Deprecated
	public static void renderShape(PoseStack poseStack, VertexConsumer builder, VoxelShape shape, double x, double y, double z, int color, float width) {
		throw Err4.arguments3("ShapeRenderer4#renderShape");
	}
}