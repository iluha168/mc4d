package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.piston;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.mixin.net.minecraft.world.level.block.BlockMixin;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBaseBlock.class)
class PistonBaseBlockMixin extends BlockMixin {
	@Redirect(method = "triggerEvent", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos triggerEvent(BlockPos instance, int x, int y, int z, @Local(name = "direction") Direction direction) {
		return ((BlockPos4) instance).offset(x, y, z, Direction4.as(direction).getStepW() * 2);
	}
}
