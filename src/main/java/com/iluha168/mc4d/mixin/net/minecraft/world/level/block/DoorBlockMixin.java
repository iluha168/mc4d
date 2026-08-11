package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Mth4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.DoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DoorBlock.class)
class DoorBlockMixin {
	// TODO getHinge

	@Redirect(method = "getSeed", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;getSeed(III)J"
	))
	private long getSeed(int x, int y, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return Mth4.getSeed(x, y, z, Vec4i.getW(pos));
	}
}
