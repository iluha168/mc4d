package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Avatar.class)
class AvatarMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 DEFAULT_VEHICLE_ATTACHMENT(double x, double y, double z) {
		return new Vec4(x, y, z, z);
	}
}
