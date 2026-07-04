package com.iluha168.mc4d.mixin.net.minecraft.util;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleUtils.class)
class ParticleUtilsMixin {
	@Redirect(method = "getRandomSpeedRanges", at = @At(
		value = "NEW",
		target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 getRandomSpeedRanges(double x, double y, double z, @Local(argsOnly = true, name = "random") RandomSource random) {
		return new Vec4(x, y, z, Mth.nextDouble(random, -0.5, 0.5));
	}

	@Redirect(method = "spawnParticlesAlongAxis", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void spawnParticlesAlongAxis(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "attachedAxis") Direction.Axis attachedAxis,
		@Local(argsOnly = true, name = "radius") double radius,
		@Local(name = "centerOfBlock") Vec3 centerOfBlock,
		@Local(name = "random") RandomSource random
	) {
		final boolean stepW = attachedAxis == Direction4.Axis.W;
		final double w = ((Vec4) centerOfBlock).w + Mth.nextDouble(random, -1.0, 1.0) * (stepW ? 0.5 : radius);
		final double wBaseSpeed = stepW ? Mth.nextDouble(random, -1.0, 1.0) : 0.0;
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, wBaseSpeed);
	}

	@Redirect(method = "spawnParticleOnFace", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void spawnParticleOnFace(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "face") Direction face,
		@Local(argsOnly = true, name = "speed") Vec3 speed,
		@Local(argsOnly = true, name = "stepFactor") double stepFactor,
		@Local(name = "centerOfBlock") Vec3 centerOfBlock,
		@Local(name = "random") RandomSource random
	) {
		final int stepW = Direction4.as(face).getStepW();
		final double w = ((Vec4) centerOfBlock).w + (stepW == 0 ? Mth.nextDouble(random, -0.5, 0.5) : stepW * stepFactor);
		final double wBaseSpeed = stepW == 0 ? ((Vec4) speed).w : 0.0;
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, wBaseSpeed);
	}

	@Redirect(method = "spawnParticleBelow", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void spawnParticleBelow(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		final double w = Vec4i.getW(pos) + random.nextDouble();
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, zd);
	}

	@Definition(id = "z", local = @Local(type = double.class, name = "z"))
	@Expression("z = @(?)")
	@Inject(method = "spawnParticles", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void spawnParticles_w(
		LevelAccessor level, BlockPos pos, int count, double spreadWidth, double spreadHeight, boolean allowFloatingParticles, ParticleOptions particle, CallbackInfo ci,
		@Local(name = "random") RandomSource random,
		@Local(name = "spreadStartOffset") double spreadStartOffset,
		@Share("wVelocity") LocalDoubleRef wVelocity,
		@Share("w") LocalDoubleRef w
	) {
		wVelocity.set(random.nextGaussian() * 0.02);
		w.set(Vec4i.getW(pos) + spreadStartOffset + random.nextDouble() * spreadWidth * 2.0);
	}
	@Redirect(method = "spawnParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos spawnParticles_containing(double x, double y, double z, @Share("w") LocalDoubleRef w) {
		return BlockPos4.containing(x, y, z, w.get());
	}
	@Redirect(method = "spawnParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void spawnParticles_addParticle(
		LevelAccessor level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Share("w") LocalDoubleRef w,
		@Share("wVelocity") LocalDoubleRef wVelocity
	) {
		((LevelAccessor4) level).addParticle(particle, x, y, z, w.get(), xd, yd, zd, wVelocity.get());
	}

	@Redirect(method = "spawnSmashAttackParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 spawnSmashAttackParticles_add(Vec3 instance, double x, double y, double z) {
		return ((Vec4) instance).add(x, y, z, z);
	}
	@Redirect(method = "spawnSmashAttackParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 0
	))
	private static void spawnSmashAttackParticles_center(
		LevelAccessor level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(name = "center") Vec3 center
	) {
		final double w = ((Vec4) center).w + level.getRandom().nextGaussian() / 2.0;
		final double wd = level.getRandom().nextGaussian() * 0.2F;
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, wd);
	}
	@Redirect(method = "spawnSmashAttackParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 1
	))
	private static void spawnSmashAttackParticles_ring(
		LevelAccessor level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "count") int count,
		@Local(name = "center") Vec3 center,
		@Local(name = "i") int i
	) {
		final Vec3 dir = MathHelpers.fibonacciSphere(i, count / 1.5F);
		final double nx =         center .x + 3.5 * dir.x + level.getRandom().nextGaussian() / 2.0;
		final double nz =         center .z + 3.5 * dir.y + level.getRandom().nextGaussian() / 2.0;
		final double nw = ((Vec4) center).w + 3.5 * dir.z + level.getRandom().nextGaussian() / 2.0;
		final double wd = level.getRandom().nextGaussian() * 0.05F;
		((LevelAccessor4) level).addParticle(particle, nx, y, nz, nw, xd, yd, zd, wd);
	}
}
