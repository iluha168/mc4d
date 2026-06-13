package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.level.block.Block4;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GrindstoneBlock.class)
public class GrindstoneBlockMixin {
	@Redirect(method = "makeShapes", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPE(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return Block4.box(
			minX, minY, minZ, 8 - 1,
			maxX, maxY, maxZ, 8 + 1
		);
	}
}
