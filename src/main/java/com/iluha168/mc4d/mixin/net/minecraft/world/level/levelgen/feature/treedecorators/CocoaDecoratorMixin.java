package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.treedecorators;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Direction4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CocoaDecorator.class)
class CocoaDecoratorMixin {
	@Redirect(method = "lambda$place$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos place(BlockPos instance, int x, int y, int z, @Local(name = "opposite") Direction opposite) {
		return ((BlockPos4) instance).offset(x, y, z, Direction4.as(opposite).getStepW());
	}
}
