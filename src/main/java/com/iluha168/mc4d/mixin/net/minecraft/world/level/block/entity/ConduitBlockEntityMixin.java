package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ConduitBlockEntity.class)
class ConduitBlockEntityMixin extends BlockEntityMixin {
	@Unique
	private static final int MIN_KILL_SIZE = 80;

	@ModifyConstant(method = "serverTick", constant = @Constant(intValue = 42))
	private static int serverTick(int minKillSize) {
		return MIN_KILL_SIZE;
	}

	@ModifyConstant(method = "updateHunting", constant = @Constant(intValue = 42))
	private static int updateHunting(int minKillSize) {
		return MIN_KILL_SIZE;
	}

	@Definition(id = "offset", method = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "oz", local = @Local(type = int.class, name = "oz"))
	@Expression("?.offset(?, ?, oz)")
	@Redirect(method = "updateShape", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static BlockPos updateShape_testPos0(BlockPos worldPosition, int x, int y, int z) {
		return ((BlockPos4) worldPosition).offset(x, y, z, 0);
	}
	@WrapOperation(method = "updateShape", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;isWaterAt(Lnet/minecraft/core/BlockPos;)Z"
	))
	private static boolean updateShape_isWaterAt(Level instance, BlockPos blockPos, Operation<Boolean> original) {
		final BlockPos4 blockPos4 = (BlockPos4) blockPos;
		for (int ow = -1; ow <= 1; ow++) {
			if (!original.call(instance, blockPos4.offset(0, 0, 0, ow))) {
				return false;
			}
		}
		return true;
	}
	@Definition(id = "ozx", local = @Local(type = int.class, name = "ozx"))
	@Expression("ozx = @(-2)")
	@Inject(method = "updateShape", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void updateShape_ow(
		Level level, BlockPos worldPosition, List<BlockPos> effectBlocks, CallbackInfoReturnable<Boolean> cir,
		@Share("ow") LocalIntRef ow
	) {
		ow.set(-2);
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "ozx", local = @Local(type = int.class, name = "ozx"))
	@Expression("ozx = ozx + @(1)")
	@ModifyExpressionValue(method = "updateShape", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int updateShape_incrementOW(int one, @Share("ow") LocalIntRef ow) {
		ow.set(ow.get() + 1);
		if (ow.get() <= 2) return 0;
		ow.set(-2);
		return 1;
	}
	@Definition(id = "az", local = @Local(type = int.class, name = "az"))
	@Expression("az > 1")
	@ModifyExpressionValue(method = "updateShape", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean updateShape_awGt1(boolean original, @Share("ow") LocalIntRef ow, @Share("aw") LocalIntRef aw) {
		aw.set(Math.abs(ow.get()));
		return original || aw.get() > 1;
	}
	@Definition(id = "az", local = @Local(type = int.class, name = "az"))
	@Expression("az == 2")
	@ModifyExpressionValue(method = "updateShape", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean updateShape_ring0(boolean original, @Share("aw") LocalIntRef aw) {
		return original || aw.get() == 2;
	}
	@Definition(id = "ozx", local = @Local(type = int.class, name = "ozx"))
	@Expression("ozx == 0")
	@ModifyExpressionValue(method = "updateShape", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean updateShape_ring2(boolean original, @Share("ow") LocalIntRef ow) {
		return original || ow.get() == 0;
	}
	@Definition(id = "ax", local = @Local(type = int.class, name = "ax"))
	@Expression("ax == 2")
	@ModifyExpressionValue(method = "updateShape", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private static boolean updateShape_ring2az(boolean original, @Local(name = "az") int az, @Share("ow") LocalIntRef ow) {
		return original || (ow.get() == 0 && az == 2);
	}
	@Definition(id = "ay", local = @Local(type = int.class, name = "ay"))
	@Expression("ay == 2")
	@ModifyExpressionValue(method = "updateShape", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private static boolean updateShape_ring1(boolean original, @Share("aw") LocalIntRef aw) {
		return original || aw.get() == 2;
	}
	@Definition(id = "offset", method = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;")
	@Definition(id = "ozx", local = @Local(type = int.class, name = "ozx"))
	@Expression("?.offset(?, ?, ozx)")
	@Redirect(method = "updateShape", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static BlockPos updateShape_testPos1(BlockPos instance, int x, int y, int z, @Share("ow") LocalIntRef ow) {
		return ((BlockPos4) instance).offset(x, y, z, ow.get());
	}

	@Redirect(method = "applyEffects", at = @At(
		value = "NEW",
		target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB applyEffects_AABB(
		double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
		@Local(argsOnly = true, name = "worldPosition") BlockPos worldPosition
	) {
		final int w = Vec4i.getW(worldPosition);
		return new AABB4(minX, minY, minZ, w, maxX, maxY, maxZ, w + 1);
	}
	@Redirect(method = "applyEffects", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB applyEffects_expandTowards(AABB instance, double xa, double ya, double za) {
		return ((AABB4) instance).expandTowards(xa, ya, za, za);
	}

	@Redirect(method = "updateAndAttackTarget", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	private static void updateAndAttackTarget(
		ServerLevel level, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch,
		@Local(name = "targetEntity") LivingEntity targetEntity
	) {
		((Level4) level).playSound(except, x, y, z, ((Entity4) targetEntity).getW(), sound, source, volume, pitch);
	}

	@Redirect(method = "getDestroyRangeAABB", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	private static AABB getDestroyRangeAABB(BlockPos pos) {
		return new AABB4(pos);
	}

	@Redirect(method = "animationTick", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 0
	))
	private static Vec3 animationTick_particleEnd(
		double x, double y, double z,
		@Local(argsOnly = true, name = "worldPosition") BlockPos worldPosition
	) {
		return new Vec4(x, y, z, Vec4i.getW(worldPosition) + 0.5);
	}
	@Redirect(method = "animationTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 0
	))
	private static void animationTick_effectBlocks(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(name = "random") RandomSource random,
		@Local(name = "particleEnd") Vec3 particleEnd,
		@Local(name = "delta") BlockPos delta
	) {
		final float dw = -0.5F + random.nextFloat() +  Vec4i.getW(delta);
		((LevelAccessor4) level).addParticle(particle, x, y, z, ((Vec4) particleEnd).w, xd, yd, zd, dw);
	}
	@Redirect(method = "animationTick", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 1
	))
	private static Vec3 animationTick_targetPosition(double x, double y, double z, @Local(argsOnly = true, name = "destroyTarget") Entity destroyTarget) {
		return new Vec4(x, y, z, ((Entity4) destroyTarget).getW());
	}
	@Redirect(method = "animationTick", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;",
		ordinal = 2
	))
	private static Vec3 animationTick_velocity(
		double x, double y, double z,
	    @Local(argsOnly = true, name = "destroyTarget") Entity destroyTarget,
		@Local(name = "random") RandomSource random
	) {
		final float randw = (-0.5F + random.nextFloat()) * (3.0F + destroyTarget.getBbWidth());
		return new Vec4(x, y, z, randw);
	}
	@Redirect(method = "animationTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 1
	))
	private static void animationTick_destroyTarget(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(name = "targetPosition") Vec3 targetPosition,
		@Local(name = "velocity") Vec3 velocity
	) {
		((LevelAccessor4) level).addParticle(particle, x, y, z, ((Vec4) targetPosition).w, xd, yd, zd, ((Vec4) velocity).w);
	}
}
