package com.iluha168.mc4d.mixin.net.minecraft.world.level.material;

import com.iluha168.mc4d.core.BlockPos4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidState.class)
class FluidStateMixin {
	@Redirect(method = "shouldRenderBackwardUpFace", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos shouldRenderBackwardUpFace(BlockPos instance, int x, int y, int z) {
		// TODO remove when 4D renderer
		return ((BlockPos4) instance).offset(x, y, z, 0);
	}
}
