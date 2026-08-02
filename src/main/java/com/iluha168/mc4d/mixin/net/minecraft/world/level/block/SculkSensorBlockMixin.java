package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SculkSensorBlock.class)
class SculkSensorBlockMixin {
	@Redirect(method = "activate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void activate_playSound(
		Level instance, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).playSound(except, x, y, z, Vec4i.getW(pos) + 0.5, sound, source, volume, pitch);
	}

	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick_addParticle(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(name = "dir") Direction dir,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		final int stepW = Direction4.as(dir).getStepW();
		final double w = Vec4i.getW(pos) + 0.5 + (stepW == 0 ? 0.5 - random.nextDouble() : stepW * 0.6);
		((LevelAccessor4) instance).addParticle(particle, x, y, z, w, xd, yd, zd, zd);
	}
}
