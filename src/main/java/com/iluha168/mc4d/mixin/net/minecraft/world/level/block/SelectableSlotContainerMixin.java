package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SelectableSlotContainer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SelectableSlotContainer.class)
interface SelectableSlotContainerMixin {
	@Redirect(method = "getRelativeHitCoordinatesForBlockFace", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;subtract(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 getRelativeHitCoordinatesForBlockFace(Vec3 instance, double x, double y, double z, @Local(name = "hitBlockPos") BlockPos hitBlockPos) {
		return ((Vec4) instance).subtract(x, y, z, Vec4i.getW(hitBlockPos));
	}
	// TODO hit direction when 4D renderer
}
