package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.FarmlandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FarmlandBlock.class)
class FarmlandBlockMixin {
	@Redirect(method = "isNearWater", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos isNearWater_offsetW(BlockPos pos, int x, int y, int z) {
		return ((BlockPos4) pos).offset(x, y, z, z);
	}
}
