package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.world.level.block.Block4;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CeilingHangingSignBlock.class)
public class CeilingHangingSignBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPES(double sizeX, double sizeZ, double minY, double maxY) {
		return Block4.column(sizeX, sizeZ, sizeX, minY, maxY);
	}
}
