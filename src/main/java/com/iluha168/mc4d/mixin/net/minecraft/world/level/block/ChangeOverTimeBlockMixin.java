package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChangeOverTimeBlock.class)
interface ChangeOverTimeBlockMixin {
	@Redirect(method = "getNextState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;withinManhattan(Lnet/minecraft/core/BlockPos;III)Ljava/lang/Iterable;"
	))
	private static Iterable<BlockPos> getNextState(BlockPos origin, int reachX, int reachY, int reachZ) {
		return BlockPos4.withinManhattan(origin, reachX, reachY, reachZ, reachZ);
	}
}
