package com.iluha168.mc4d.mixin.net.minecraft.world.level.material;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Fluid.class)
class FluidMixin {
	@Redirect(method = "getAABB", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB getAABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int minW = Vec4i.getW(pos);
		return new AABB4(minX, minY, minZ, minW, maxX, maxY, maxZ, minW + 1.0);
	}
}
