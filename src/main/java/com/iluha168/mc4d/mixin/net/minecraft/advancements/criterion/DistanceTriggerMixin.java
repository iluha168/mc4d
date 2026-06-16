package com.iluha168.mc4d.mixin.net.minecraft.advancements.criterion;

import com.iluha168.mc4d.advancements.criterion.DistancePredicate4;
import com.iluha168.mc4d.advancements.criterion.LocationPredicate4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.DistanceTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DistanceTrigger.class)
class DistanceTriggerMixin {
	@Mixin(DistanceTrigger.TriggerInstance.class)
	static class TriggerInstanceMixin {
		@Redirect(method = "matches(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Z", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/advancements/criterion/LocationPredicate;matches(Lnet/minecraft/server/level/ServerLevel;DDD)Z"
		))
		boolean matches_startPosition(LocationPredicate predicate, ServerLevel level, double x, double y, double z, @Local(argsOnly = true, name = "enteredPosition") Vec3 enteredPosition) {
			return LocationPredicate4.as(predicate).matches(level, x, y, z, ((Vec4) enteredPosition).w);
		}
		@Redirect(method = "matches(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)Z", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/advancements/criterion/DistancePredicate;matches(DDDDDD)Z"
		))
		boolean matches_distance(DistancePredicate predicate, double x0, double y0, double z0, double x1, double y1, double z1, @Local(argsOnly = true, name = "enteredPosition") Vec3 enteredPosition, @Local(argsOnly = true, name = "playerPosition") Vec3 playerPosition) {
			return DistancePredicate4.as(predicate).matches(x0, y0, z0, ((Vec4) enteredPosition).w, x1, y1, z1, ((Vec4) playerPosition).w);
		}
	}
}
