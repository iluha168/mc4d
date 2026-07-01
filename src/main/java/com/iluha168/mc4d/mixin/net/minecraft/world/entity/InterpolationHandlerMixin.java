package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InterpolationHandler;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InterpolationHandler.class)
class InterpolationHandlerMixin {
	@Shadow
	@Final
	private Entity entity;

	@Shadow
	@Final
	private InterpolationHandler.InterpolationData interpolationData;

	@Redirect(method = "<init>(Lnet/minecraft/world/entity/Entity;ILjava/util/function/Consumer;)V", at = @At(
		value = "FIELD",
		target = "Lnet/minecraft/world/phys/Vec3;ZERO:Lnet/minecraft/world/phys/Vec3;",
		opcode = Opcodes.GETSTATIC
	))
	Vec3 init() {
		return Vec4.ZERO;
	}

	@Redirect(method = "interpolate", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 interpolate_newPosition(double x, double y, double z, @Local(name = "alpha") double alpha) {
		final double w = Mth.lerp(alpha, ((Entity4) this.entity).getW(), ((Vec4) this.interpolationData.position).w);
		return new Vec4(x, y, z, w);
	}
}
