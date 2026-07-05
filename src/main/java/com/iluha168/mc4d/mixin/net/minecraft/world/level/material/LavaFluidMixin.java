package com.iluha168.mc4d.mixin.net.minecraft.world.level.material;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LavaFluid.class)
class LavaFluidMixin extends FlowingFluidMixin {
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick_addParticle(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Share("ww") LocalDoubleRef ww
	) {
		ww.set(Vec4i.getW(pos) + random.nextDouble());
		((LevelAccessor4) instance).addParticle(particle, x, y, z, ww.get(), xd, yd, zd, zd);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
		ordinal = 0
	))
	void animateTick_playLocalSound(
		Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Share("ww") LocalDoubleRef ww
	) {
		((Level4) instance).playLocalSound(x, y, z, ww.get(), sound, source, volume, pitch, distanceDelay);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
		ordinal = 1
	))
	void animateTick_playLocalSound(
		Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).playLocalSound(x, y, z, Vec4i.getW(pos), sound, source, volume, pitch, distanceDelay);
	}

	@Redirect(method = "randomTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos randomTick(BlockPos instance, int x, int y, int z, @Local(argsOnly = true, name = "random") RandomSource random) {
		return ((BlockPos4) instance).offset(x, y, z, random.nextInt(3) - 1);
	}
}
