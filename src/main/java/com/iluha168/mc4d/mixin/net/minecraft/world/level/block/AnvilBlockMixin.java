package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.level.block.Block4;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPES(double sizeX, double sizeZ, double minY, double maxY) {
		double halfZ = sizeZ / 2.0;
		double halfW = sizeX / 2.0;
		return Block4.boxZ(sizeX, minY, maxY, 8 - halfZ, 8 + halfZ, 8 - halfW, 8 + halfW);
	}
}
