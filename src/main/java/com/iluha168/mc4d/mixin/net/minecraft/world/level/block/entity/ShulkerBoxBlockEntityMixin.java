package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerBoxBlockEntity.class)
class ShulkerBoxBlockEntityMixin {
	@Redirect(method = "getBoundingBox", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 getBoundingBox(double x, double y, double z) {
		return new Vec4(x, y, z, z);
	}

	@Redirect(method = "moveCollidedEntities", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 moveCollidedEntities(
		double x, double y, double z,
		@Local(name = "aabb") AABB aabb,
		@Local(name = "direction") Direction direction
	) {
		return new Vec4(x, y, z, (((AABB4) aabb).getWsize() + 0.01) * Direction4.as(direction).getStepW());
	}
}
