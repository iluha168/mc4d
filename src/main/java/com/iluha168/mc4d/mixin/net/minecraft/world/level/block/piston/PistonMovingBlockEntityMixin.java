package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.piston;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity.BlockEntityMixin;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.block.piston.PistonMovingBlockEntity4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.iluha168.mc4d.world.phys.shapes.VoxelShape4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonMovingBlockEntity.class)
abstract
class PistonMovingBlockEntityMixin extends BlockEntityMixin implements PistonMovingBlockEntity4 {
	@Shadow
	private Direction direction;

	@Shadow
	protected abstract float getExtendedProgress(float progress);

	@Shadow
	public abstract float getProgress(float a);

	@Override
	public float getWOff(float a) {
		return Direction4.as(this.direction).getStepW() * this.getExtendedProgress(this.getProgress(a));
	}

	@Redirect(method = "moveCollidedEntities", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"
	))
	private static void moveCollidedEntities(
		Entity entity, double xd, double yd, double zd,
		@Local(name = "movement") Direction movement,
		@Local(name = "deltaMovement") Vec3 deltaMovement
	) {
		final double dw = movement.getAxis() == Direction4.Axis.W
			? Direction4.as(movement).getStepW()
			: ((Vec4) deltaMovement).w;
		entity.setDeltaMovement(new Vec4(xd, yd, zd, dw));
	}

	@Redirect(method = "moveEntityByPiston", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 moveEntityByPiston(
		double x, double y, double z,
		@Local(argsOnly = true, name = "delta") double delta,
		@Local(argsOnly = true, name = "movement") Direction movement
	) {
		return new Vec4(x, y, z, delta * Direction4.as(movement).getStepW());
	}

	@Redirect(method = "moveStuckEntities", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB moveStuckEntities(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return new AABB4(
			minX, minY, minZ, minZ,
			maxX, maxY, maxZ, maxZ
		);
	}

	@Definition(id = "getZ", method = "Lnet/minecraft/world/entity/Entity;getZ()D")
	@Definition(id = "maxZ", field = "Lnet/minecraft/world/phys/AABB;maxZ:D")
	@Expression("?.getZ() <= ?.maxZ")
	@ModifyExpressionValue(method = "matchesStickyCritera", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean matchesStickyCritera(
		boolean original,
		@Local(argsOnly = true, name = "aabb") AABB aabb,
		@Local(argsOnly = true, name = "entity") Entity entity
	) {
		final double entityW = ((Entity4) entity).getW();
		AABB4 aabb4 = (AABB4) aabb;
		return original && entityW >= aabb4.minW && entityW <= aabb4.maxW;
	}

	@WrapMethod(method = "getMovement")
	private static double getMovement(AABB aabbToBeOutsideOf, Direction movement, AABB aabb, Operation<Double> original) {
		if (movement == Direction4.ANA)
			return ((AABB4) aabbToBeOutsideOf).maxW - ((AABB4) aabb).minW;
		if (movement == Direction4.KATA)
			return ((AABB4) aabb).maxW - ((AABB4) aabbToBeOutsideOf).minW;
		return original.call(aabbToBeOutsideOf, movement, aabb);
	}

	@Redirect(method = "moveByPositionAndProgress", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB moveByPositionAndProgress(
		AABB instance, double xa, double ya, double za,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "entity") PistonMovingBlockEntity entity,
		@Local(name = "currentPosition") double currentPosition
	) {
		return ((AABB4) instance).move(xa, ya, za, Vec4i.getW(pos) + currentPosition * Direction4.as(entity.getDirection()).getStepW());
	}

	@Redirect(method = "getCollisionShape", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/shapes/VoxelShape;move(DDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	VoxelShape getCollisionShape(VoxelShape collisionShape, double dx, double dy, double dz, @Local(name = "extendedProgress") float extendedProgress) {
		final double dw = Direction4.as(this.direction).getStepW() * extendedProgress;
		return ((VoxelShape4) collisionShape).move(dx, dy, dz, dw);
	}
}
