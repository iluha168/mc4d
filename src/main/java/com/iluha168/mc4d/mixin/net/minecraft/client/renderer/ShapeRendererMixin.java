package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.world.phys.shapes.VoxelShape4;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShapeRenderer.class)
class ShapeRendererMixin {
	@Redirect(method = "renderShape", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;forAllEdges(Lnet/minecraft/world/phys/shapes/Shapes$DoubleLineConsumer;)V"
	))
	private static void renderShape(VoxelShape shape, Shapes.DoubleLineConsumer consumer) {
		((VoxelShape4) shape).forAllEdges((x1, y1, z1, w1, x2, y2, z2, w2) -> {
			// TODO 4D renderer should use W
			consumer.consume(x1, y1, z1, x2, y2, z2);
		});
	}
}
