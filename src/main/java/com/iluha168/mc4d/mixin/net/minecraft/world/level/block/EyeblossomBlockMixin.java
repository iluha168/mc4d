package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.server.level.ServerLevel4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EyeblossomBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EyeblossomBlock.class)
class EyeblossomBlockMixin {
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void animateTick(
		Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).playLocalSound(x, y, z, Vec4i.getW(pos), sound, source, volume, pitch, distanceDelay);
	}

	@Redirect(method = "tryChangingState", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos tryChangingState(BlockPos pos, int x, int y, int z) {
		return ((BlockPos4) pos).offset(x, y, z, z);
	}

	@Mixin(EyeblossomBlock.Type.class)
	static class TypeMixin {
		@Redirect(method = "spawnTransformParticle", at = @At(
			value = "NEW",
			target = "(DDD)Lnet/minecraft/world/phys/Vec3;"
		))
		Vec3 spawnTransformParticle_velocity(double x, double y, double z, @Local(argsOnly = true, name = "random") RandomSource random) {
			return new Vec4(x, y, z, random.nextDouble() - 0.5);
		}
		@Redirect(method = "spawnTransformParticle", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
		))
		int spawnTransformParticle_sendParticles(
			ServerLevel instance, ParticleOptions particle,
			double x, double y, double z,
			int count,
			double xDist, double yDist, double zDist,
			double speed,
			@Local(name = "start") Vec3 start
		) {
			return ((ServerLevel4) instance).sendParticles(particle, x, y, z, ((Vec4) start).w, count, xDist, yDist, zDist, zDist, speed);
		}
	}
}
