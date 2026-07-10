package com.iluha168.mc4d.mixin.net.minecraft.world.entity.monster;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Shulker.class)
class ShulkerMixin {
	// TODO everything

	@Redirect(method = "getProgressDeltaAabb", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getProgressDeltaAabb_boundsAtBottomCenter(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new AABB4(
			minX, minY, minZ, minZ,
			maxX, maxY, maxZ, maxZ
		);
	}
	@Redirect(method = "getProgressDeltaAabb", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getProgressDeltaAabb_expandTowards(
		AABB instance, double xa, double ya, double za,
		@Local(argsOnly = true, name = "size") float size,
		@Local(argsOnly = true, name = "direction") Direction direction,
		@Local(name = "maxMovement") double maxMovement
	) {
		return ((AABB4) instance).expandTowards(xa, ya, za, Direction4.as(direction).getStepW() * maxMovement * size);
	}
	@Redirect(method = "getProgressDeltaAabb", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;contract(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getProgressDeltaAabb_contract(
		AABB instance, double xa, double ya, double za,
		@Local(argsOnly = true, name = "size") float size,
		@Local(argsOnly = true, name = "direction") Direction direction,
		@Local(name = "minMovement") double minMovement
	) {
		return ((AABB4) instance).contract(xa, ya, za, -Direction4.as(direction).getStepW() * (1.0 + minMovement) * size);
	}
	@Redirect(method = "getProgressDeltaAabb", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getProgressDeltaAabb_move(AABB instance, double xa, double ya, double za, @Local(argsOnly = true, name = "position") Vec3 position) {
		return ((AABB4) instance).move(xa, ya, za, ((Vec4) position).w);
	}

	// TODO everything
}
