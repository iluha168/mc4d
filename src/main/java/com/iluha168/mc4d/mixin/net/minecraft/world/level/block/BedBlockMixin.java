package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Mth4;
import com.iluha168.mc4d.world.level.block.Block4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BedBlock.class)
class BedBlockMixin {
	@Redirect(method = "lambda$static$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;box(DDDDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPES(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return Block4.box(minX, minY, minZ, minZ, maxX, maxY, maxZ, maxZ);
	}

	// TODO kickVillagerOutOfBed
	// TODO bounceUp
	// TODO findStandUpPositionAtOffset

	@Redirect(method = "getSeed", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;getSeed(III)J"
	))
	private long getSeed(int x, int y, int z, @Local(name = "sourcePos") BlockPos sourcePos) {
		return Mth4.getSeed(x, y, z, Vec4i.getW(sourcePos));
	}

	// TODO bedSurroundStandUpOffsets
	// TODO bedAboveStandUpOffsets
}
