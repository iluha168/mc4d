package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.treedecorators;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.treedecorators.AttachedToLeavesDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AttachedToLeavesDecorator.class)
class AttachedToLeavesDecoratorMixin {
	@Redirect(method = "place", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos place(BlockPos instance, int x, int y, int z) {
		return ((BlockPos4) instance).offset(x, y, z, z);
	}
}
