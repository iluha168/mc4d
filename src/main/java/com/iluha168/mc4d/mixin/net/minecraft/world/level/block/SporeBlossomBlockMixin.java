package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SporeBlossomBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SporeBlossomBlock.class)
class SporeBlossomBlockMixin {
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 0
	))
	void animateTick_addParticleFalling(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		final double wFalling = Vec4i.getW(pos) + random.nextDouble();
		((LevelAccessor4) instance).addParticle(particle, x, y, z, wFalling, xd, yd, zd, zd);
	}
	@ModifyConstant(method = "animateTick", constant = @Constant(intValue = 14))
	int animateTick_attempts(int addParticleAttempts) {
		return addParticleAttempts * 21; // Preserve vanilla's density (21*10*21 -> 21*10*21*21).
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos animateTick_set(
		BlockPos.MutableBlockPos ambientPos, int x, int y, int z,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		return ((BlockPos4.MutableBlockPos) ambientPos).set(x, y, z, Vec4i.getW(pos) + Mth.nextInt(random, -10, 10));
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 1
	))
	void animateTick_addParticleAmbient(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(name = "ambientPos") BlockPos.MutableBlockPos ambientPos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		((LevelAccessor4) instance).addParticle(particle, x, y, z, Vec4i.getW(ambientPos) + random.nextDouble(), xd, yd, zd, zd);
	}
}
