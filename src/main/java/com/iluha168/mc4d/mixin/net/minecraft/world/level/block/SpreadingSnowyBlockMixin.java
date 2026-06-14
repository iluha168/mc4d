package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SpreadingSnowyBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpreadingSnowyBlock.class)
class SpreadingSnowyBlockMixin {
	@Redirect(method = "randomTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos randomTick(BlockPos instance, int x, int y, int z, @Local(argsOnly = true, name = "random") RandomSource random) {
		return ((BlockPos4) instance).offset(x, y, z, random.nextInt(3) - 1);
	}
}
