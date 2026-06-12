package com.iluha168.mc4d.mixin.level4;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LevelReader.class)
interface LevelReaderMixin extends LevelReader4 {
	// TODO the rest

	@ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = 30000000))
	private static int getMaxLocalRawBrightness(int constant) {
		return Level4.MAX_LEVEL_SIZE;
	}
	@ModifyConstant(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", constant = @Constant(intValue = -30000000))
	private static int getMaxLocalRawBrightness_(int constant) {
		return -Level4.MAX_LEVEL_SIZE;
	}
	@Expression("? < ?")
	@ModifyExpressionValue(method = "getMaxLocalRawBrightness(Lnet/minecraft/core/BlockPos;I)I", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	private static boolean getMaxLocalRawBrightness(boolean original, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int w = Vec4i.getW(pos);
		return original && w >= -Level4.MAX_LEVEL_SIZE && w < Level4.MAX_LEVEL_SIZE;
	}

	// TODO the rest
}
