package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.block;

import com.iluha168.mc4d.world.phys.shapes.Shapes4;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.Iterator;

@Mixin(FluidRenderer.class)
class FluidRendererMixin {
	@Redirect(method = "isFaceOccludedByState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/Shapes;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape isFaceOccludedByState(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return Shapes4.box(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
	}

	@Redirect(method = "tesselate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction$Plane;iterator()Ljava/util/Iterator;"
	))
	Iterator<Direction> tesselate_HORIZONTAL(Direction.Plane instance) {
		// TODO remove when 4D renderer
		return Arrays.stream(new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}).iterator();
	}
}
