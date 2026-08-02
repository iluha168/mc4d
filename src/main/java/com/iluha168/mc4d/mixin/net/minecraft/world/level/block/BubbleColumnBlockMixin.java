package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BubbleColumnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BubbleColumnBlock.class)
class BubbleColumnBlockMixin extends BlockMixin {
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 0
	))
	void animateTick_currentDown(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).addAlwaysVisibleParticle(particle, x, y, z, Vec4i.getW(pos), xd, yd, zd, zd);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void animateTick_playLocalSound(
		Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).playLocalSound(x, y, z, Vec4i.getW(pos), sound, source, volume, pitch, distanceDelay);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 1
	))
	void animateTick_bubbleColumnUp(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).addAlwaysVisibleParticle(particle, x, y, z, Vec4i.getW(pos) + 0.5, xd, yd, zd, zd);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addAlwaysVisibleParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V",
		ordinal = 2
	))
	void animateTick_bubbleColumnUpRandom(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		((Level4) instance).addAlwaysVisibleParticle(particle, x, y, z, Vec4i.getW(pos) + random.nextFloat(), xd, yd, zd, zd);
	}
}
