package com.iluha168.mc4d.mixin.net.minecraft.world.phys;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Vec3.class)
class Vec3Mixin {
	@Redirect(method = "atLowerCornerOf", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 atLowerCornerOf(double x, double y, double z, @Local(argsOnly = true, name = "pos") Vec3i pos) {
		return new Vec4(x, y, z, Vec4i.getW(pos));
	}

	@Overwrite
	@Deprecated
	public static Vec3 atLowerCornerWithOffset(Vec3i pos, double x, double y, double z) {
		throw Err4.arguments3("Vec4#atLowerCornerWithOffset");
	}

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

	@Redirect(method = "upFromBottomCenterOf", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;atLowerCornerWithOffset(Lnet/minecraft/core/Vec3i;DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 upFromBottomCenterOf(Vec3i pos, double x, double y, double z) {
		return Vec4.atLowerCornerWithOffset(pos, x, y, z, z);
	}

	@Inject(method = "<init>(Lnet/minecraft/core/Vec3i;)V", at = @At("HEAD"))
	private static void init_Vec3i(Vec3i vec, CallbackInfo ci) {
		throw Err4.return3("Vec4#<init>");
	}

	@Redirect(method = "directionFromRotation(Lnet/minecraft/world/phys/Vec2;)Lnet/minecraft/world/phys/Vec3;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;directionFromRotation(FF)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 directionFromRotation_fromVec(float rotX, float rotY, @Local(argsOnly = true, name = "rotation") Vec2 rotation) {
		return Vec4.directionFromRotation(rotX, rotY, ((RotationVec) rotation).w);
	}
	@Overwrite
	@Deprecated
	public static Vec3 directionFromRotation(float rotX, float rotY) {
		throw Err4.rotation("Vec4#directionFromRotation");
	}

	@Overwrite // Other mods that target this SHOULD fail mixin applications, why modify math?
	@Deprecated
	public static Vec3 applyLocalCoordinatesToRotation(Vec2 rotation, Vec3 direction) {
		return Vec4.applyLocalCoordinatesToRotation((RotationVec) rotation, (Vec4) direction);
	}
}
