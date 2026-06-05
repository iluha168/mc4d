package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;

@Mixin(Player.class)
abstract class PlayerMixin {
	@Redirect(method = "aiStep", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB pickupArea(AABB instance, double xAdd, double yAdd, double zAdd) {
		assert xAdd == zAdd;
		return ((AABB4) instance).inflate(xAdd, yAdd, zAdd, xAdd);
	}

	@Redirect(method = "maybeBackOffFromEdge", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;canFallAtLeast(DDD)Z"
	))
	boolean maybeBackOffFromEdge_canFallAtLeast4(Player instance, double deltaX, double deltaZ, double minHeight) throws IOException {
		assert (Object) this == instance;
		return this.canFallAtLeast(deltaX, deltaZ, 0, minHeight);
	}

	@Redirect(method = "maybeBackOffFromEdge", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	Vec3 maybeBackOffFromEdge(
		double deltaX, double deltaY, double deltaZ,
		@Local(name = "delta", argsOnly = true) Vec3 delta,
		@Local(name = "step") double step,
		@Local(name = "maxDownStep") float maxDownStep,
		@Local(name = "stepX") double stepX,
		@Local(name = "stepZ") double stepZ
	) throws IOException {
		double deltaW = ((Vec4) delta).w;
		double stepW = Math.signum(deltaW) * step;

		while (deltaW != 0 && this.canFallAtLeast(0, deltaZ, deltaW, maxDownStep)) {
			if (Math.abs(deltaW) <= step) {
				deltaW = 0;
				break;
			}
			deltaW -= stepW;
		}

		while (deltaX != 0 && deltaZ != 0 && deltaW != 0 && this.canFallAtLeast(deltaX, deltaZ, deltaW, maxDownStep)) {
			if (Math.abs(deltaX) <= step) {
				deltaX = 0;
			} else {
				deltaX -= stepX;
			}

			if (Math.abs(deltaZ) <= step) {
				deltaZ = 0;
			} else {
				deltaZ -= stepZ;
			}

			if (Math.abs(deltaW) <= step) {
				deltaW = 0;
			} else {
				deltaW -= stepW;
			}
		}

		return new Vec4(deltaX, deltaY, deltaZ, deltaW);
	}

	@Redirect(method = "isAboveGround", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/player/Player;canFallAtLeast(DDD)Z"
	))
	boolean isAboveGround(Player instance, double deltaX, double deltaZ, double minHeight) throws IOException {
		assert (Object) this == instance;
		return this.canFallAtLeast(deltaX, deltaZ, 0, minHeight);
	}

	/**
	 * @author iluha168
	 * @reason Uses 3 arguments for space. Removing the method, replacing with a method with 4 args.
	 */
	@Overwrite
	@Deprecated
	private boolean canFallAtLeast(double deltaX, double deltaZ, double minHeight) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space. Do not use a private method instead."));
	}
	@Unique
	private boolean canFallAtLeast(double deltaX, double deltaZ, double deltaW, double minHeight) throws IOException {
		Entity player = (Entity) (Object) this;
		AABB4 boundingBox = (AABB4) player.getBoundingBox();
		try (Level level = player.level()) {
			return level.noCollision(
				player,
				new AABB4(
					boundingBox.minX + AABB4.EPSILON + deltaX,
					boundingBox.minY - minHeight - AABB4.EPSILON,
					boundingBox.minZ + AABB4.EPSILON + deltaZ,
					boundingBox.minW + AABB4.EPSILON + deltaW,
					boundingBox.maxX - AABB4.EPSILON + deltaX,
					boundingBox.minY,
					boundingBox.maxZ - AABB4.EPSILON + deltaZ,
					boundingBox.maxW - AABB4.EPSILON + deltaW
				)
			);
		}
	}
}
