package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.MushroomBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(MushroomBlock.class)
class MushroomBlockMixin {
	@ModifyConstant(method = "randomTick", constant = @Constant(intValue = 5))
	private int randomTick_max(int max) {
		return max * 9; // Preserve vanilla's density (9x3x9 -> 9x3x9x9).
	}
	@Redirect(method = "randomTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	), slice = @Slice(to = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"
	)))
	private BlockPos randomTick_offsetBounds(BlockPos pos, int x, int y, int z) {
		return ((BlockPos4) pos).offset(x, y, z, z);
	}
	@Redirect(method = "randomTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	), slice = @Slice(from = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;betweenClosed(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Ljava/lang/Iterable;"
	)))
	private BlockPos randomTick_offsetSpread(BlockPos pos, int x, int y, int z, @Local(argsOnly = true, name = "random") RandomSource random) {
		return ((BlockPos4) pos).offset(x, y, z, random.nextInt(3) - 1);
	}
}
