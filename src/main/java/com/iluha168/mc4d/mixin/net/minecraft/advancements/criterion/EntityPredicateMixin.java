package com.iluha168.mc4d.mixin.net.minecraft.advancements.criterion;

import com.iluha168.mc4d.advancements.criterion.LocationPredicate4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPredicate.class)
public class EntityPredicateMixin {
	// TODO matches distance
	// TODO matches movement
	@Definition(id = "matches", method = "Lnet/minecraft/advancements/criterion/LocationPredicate;matches(Lnet/minecraft/server/level/ServerLevel;DDD)Z")
	@Definition(id = "entity", local = @Local(type = Entity.class, name = "entity", argsOnly = true))
	@Definition(id = "getX", method = "Lnet/minecraft/world/entity/Entity;getX()D")
	@Expression("?.matches(?, entity.getX(), ?, ?)")
	@Redirect(method = "matches(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;)Z", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean matches_locationEntity(LocationPredicate instance, ServerLevel level, double x, double y, double z, @Local(argsOnly = true, name = "entity") Entity entity) {
		return LocationPredicate4.as(instance).matches(level, x, y, z, ((Entity4) entity).getW());
	}
	@Definition(id = "matches", method = "Lnet/minecraft/advancements/criterion/LocationPredicate;matches(Lnet/minecraft/server/level/ServerLevel;DDD)Z")
	@Definition(id = "onPos", local = @Local(type = Vec3.class, name = "onPos"))
	@Definition(id = "x", method = "Lnet/minecraft/world/phys/Vec3;x()D")
	@Expression("?.matches(?, onPos.x(), ?, ?)")
	@Redirect(method = "matches(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/Entity;)Z", at = @At("MIXINEXTRAS:EXPRESSION"))
	boolean matches_locationPos(LocationPredicate instance, ServerLevel level, double x, double y, double z, @Local(name = "onPos") Vec3 onPos) {
		return LocationPredicate4.as(instance).matches(level, x, y, z, ((Vec4) onPos).w);
	}
}
