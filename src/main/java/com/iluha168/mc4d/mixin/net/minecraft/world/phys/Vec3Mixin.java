package com.iluha168.mc4d.mixin.net.minecraft.world.phys;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO static methods
@Mixin(Vec3.class)
class Vec3Mixin {
	@Redirect(method = "atCenterOf", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;atLowerCornerWithOffset(Lnet/minecraft/core/Vec3i;DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 atCenterOf(Vec3i pos, double x, double y, double z) {
		return Vec4.atLowerCornerWithOffset(pos, x, y, z, z);
	}

	@Redirect(method = "atBottomCenterOf", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;atLowerCornerWithOffset(Lnet/minecraft/core/Vec3i;DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 atBottomCenterOf(Vec3i pos, double x, double y, double z) {
		return Vec4.atLowerCornerWithOffset(pos, x, y, z, z);
	}

	@WrapMethod(method = "applyLocalCoordinatesToRotation")
	private static Vec3 applyLocalCoordinatesToRotation(Vec2 rotation, Vec3 direction, Operation<Vec3> original) {
		return direction instanceof Vec4 direction4
			? Vec4.applyLocalCoordinatesToRotation(rotation, direction4)
			: original.call(rotation, direction);
	}
}
