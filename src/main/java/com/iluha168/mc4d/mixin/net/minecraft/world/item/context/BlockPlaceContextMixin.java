package com.iluha168.mc4d.mixin.net.minecraft.world.item.context;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPlaceContext.class)
abstract class BlockPlaceContextMixin {
	@Redirect(method = "at", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 at(
		double x, double y, double z,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "direction") Direction direction
	) {
		return new Vec4(x, y, z, Vec4i.getW(pos) + 0.5 + Direction4.as(direction).getStepW() * 0.5);
	}
}
