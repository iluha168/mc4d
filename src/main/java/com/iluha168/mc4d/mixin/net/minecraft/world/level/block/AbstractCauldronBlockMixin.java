package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.level.block.Block4;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractCauldronBlock.class)
public class AbstractCauldronBlockMixin {
	@Redirect(method = "lambda$static$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPE(double sizeX, double sizeZ, double minY, double maxY) {
		return Block4.column(sizeX, sizeZ, Math.min(sizeX, sizeZ), minY, maxY);
	}

	@Redirect(method = "lambda$static$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/Shapes;or(Lnet/minecraft/world/phys/shapes/VoxelShape;[Lnet/minecraft/world/phys/shapes/VoxelShape;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPE(VoxelShape first, VoxelShape[] tail) {
		return Shapes.or(Shapes.or(first, Block4.column(8.0, 8.0, 16.0, 0.0, 3.0)), tail);
	}
}
