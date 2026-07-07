package com.iluha168.mc4d.mixin.net.minecraft.world.item.context;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPlaceContext.class)
abstract class BlockPlaceContextMixin extends UseOnContextMixin {
	@Redirect(method = "at", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 at(
		double x, double y, double z,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "direction") Direction direction
	) {
		return new Vec4(x, y, z, Vec4i.getW(pos) + 0.5 + Direction4.as(direction).getStepW() * 0.5);
	}

	// TODO remove hacks below when 4D renderer
	@WrapOperation(method = "getNearestLookingDirection", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;orderedByNearest(Lnet/minecraft/world/entity/Entity;)[Lnet/minecraft/core/Direction;"
	))
	Direction[] getNearestLookingDirection(Entity entity, Operation<Direction[]> original) {
		final int playerBlockW = ((Entity4) entity).getBlockW();
		final int hitBlockW = Vec4i.getW(this.getClickedPos());
		if (hitBlockW > playerBlockW) return new Direction[] { Direction4.ANA };
		if (hitBlockW < playerBlockW) return new Direction[] { Direction4.KATA };
		return original.call(entity);
	}

	@WrapOperation(method = "getNearestLookingDirections", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;orderedByNearest(Lnet/minecraft/world/entity/Entity;)[Lnet/minecraft/core/Direction;"
	))
	Direction[] getNearestLookingDirections(Entity entity, Operation<Direction[]> original) {
		final int playerBlockW = ((Entity4) entity).getBlockW();
		final int hitBlockW = Vec4i.getW(this.getClickedPos());
		final Direction[] dir3 = original.call(entity);
		if (hitBlockW > playerBlockW) return new Direction[] { Direction4.ANA , dir3[0], dir3[1], dir3[2], dir3[3], dir3[4], dir3[5], Direction4.KATA };
		if (hitBlockW < playerBlockW) return new Direction[] { Direction4.KATA, dir3[0], dir3[1], dir3[2], dir3[3], dir3[4], dir3[5], Direction4.ANA  };
		return new Direction[] { dir3[0], dir3[1], dir3[2], Direction4.ANA, Direction4.KATA, dir3[3], dir3[4], dir3[5] };
	}
}
