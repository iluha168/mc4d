package com.iluha168.mc4d.mixin.net.minecraft.world.entity;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
class LivingEntityMixin {
	@Definition(id = "dz", local = @Local(type = double.class, name = "dz"))
	@Definition(id = "movement", local = @Local(type = Vec3.class, name = "movement"))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Expression("dz = movement.z")
	@Inject(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
	void createDW(
		CallbackInfo ci,
		@Local(name = "movement") Vec3 movement,
		@Share("dw") LocalDoubleRef dw
	) {
		dw.set(((Vec4) movement).w);
	}

	@Definition(id = "dz", local = @Local(type = double.class, name = "dz"))
	@Expression("dz = 0.0")
	@Inject(method = "aiStep", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
	void horizontalEpsilon(CallbackInfo ci, @Share("dw") LocalDoubleRef dw) {
		dw.set(0);
	}

	@Definition(id = "abs", method = "Ljava/lang/Math;abs(D)D")
	@Definition(id = "movement", local = @Local(type = Vec3.class, name = "movement"))
	@Definition(id = "z", field = "Lnet/minecraft/world/phys/Vec3;z:D")
	@Expression("abs(movement.z)")
	@Inject(method = "aiStep", at = @At("MIXINEXTRAS:EXPRESSION"))
	void wEpsilon(
		CallbackInfo ci,
		@Local(name = "movement") Vec3 movement,
		@Share("dw") LocalDoubleRef dw
	) {
		if (Math.abs(((Vec4) movement).w) < 0.003) {
			dw.set(0);
		}
	}

	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"
	))
	void setDeltaMovement(LivingEntity This, double dx, double dy, double dz, @Share("dw") LocalDoubleRef dw) {
		This.setDeltaMovement(new Vec4(dx, dy, dz, dw.get()));
	}

	@Redirect(method = "travelInAir", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V",
		ordinal = 0
	))
	void travelInAir_noFriction(LivingEntity instance, double dx, double dy, double dz, @Local(name = "movement") Vec3 movement) {
		double dw = ((Vec4) movement).w;
		instance.setDeltaMovement(new Vec4(dx, dy, dz, dw));
	}

	@Redirect(method = "travelInAir", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V",
		ordinal = 1
	))
	void travelInAir_friction(
		LivingEntity instance, double dx, double dy, double dz,
		@Local(name = "movement") Vec3 movement,
		@Local(name = "friction") float friction
	) {
		double dw = ((Vec4) movement).w * friction;
		instance.setDeltaMovement(new Vec4(dx, dy, dz, dw));
	}

	@Redirect(method = "jumpFromGround", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"
	))
	void jumpFromGround(LivingEntity This, double x, double y, double z, @Local(name = "movement") Vec3 movement) {
		This.setDeltaMovement(new Vec4(x, y, z, ((Vec4) movement).w));
	}
}
