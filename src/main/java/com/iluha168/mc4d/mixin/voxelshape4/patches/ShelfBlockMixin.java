package com.iluha168.mc4d.mixin.voxelshape4.patches;

import com.iluha168.mc4d.world.level.block.Block4;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShelfBlock.class)
public class ShelfBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPES(
		double minX, double minY, double minZ,
		double maxX, double maxY, double maxZ
	) {
		// TODO I lack the creativity to make an interesting shape, so just gonna extrude for now
		return Block4.box(
			minX, minY, minZ, 0.0,
			maxX, maxY, maxZ, 16.0
		);
	}
}
