package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.levelgen.PositionalRandomFactory4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PositionalRandomFactory.class)
interface PositionalRandomFactoryMixin extends PositionalRandomFactory4 {
	@Redirect(method = "at(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/util/RandomSource;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/PositionalRandomFactory;at(III)Lnet/minecraft/util/RandomSource;"
	))
	default RandomSource at(PositionalRandomFactory instance, int x, int y, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((PositionalRandomFactory4) instance).at(x, y, z, Vec4i.getW(pos));
	}
}
