package com.iluha168.mc4d.mixin.net.minecraft.util;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.util.Mth4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mth.class)
class MthMixin implements Mth4 {
	// TODO everything

	@Redirect(method = "getSeed(Lnet/minecraft/core/Vec3i;)J", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/Mth;getSeed(III)J"
	))
	private static long getSeed(int x, int y, int z, @Local(argsOnly = true, name = "vec") Vec3i vec) {
		return Mth4.getSeed(x, y, z, Vec4i.getW(vec));
	}

	@Overwrite
	@Deprecated
	public static long getSeed(int x, int y, int z) {
		throw Err4.arguments3("Mth4#getSeed");
	}

	// TODO everything
}
