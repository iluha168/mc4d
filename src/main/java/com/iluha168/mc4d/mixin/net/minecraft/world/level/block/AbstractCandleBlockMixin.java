package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractCandleBlock.class)
class AbstractCandleBlockMixin {
	@Redirect(method = "lambda$animateTick$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
	))
	private static Vec3 animateTick(
		Vec3 particlePos, double x, double y, double z,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		return ((Vec4) particlePos).add(x, y, z, Vec4i.getW(pos));
	}

	@Redirect(method = "addParticlesAndSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void addParticlesAndSound_addParticle(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") Vec3 pos
	) {
		((LevelAccessor4) level).addParticle(particle, x, y, z, ((Vec4) pos).w, xd, yd, zd, zd);
	}
	@Redirect(method = "addParticlesAndSound", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	private static void addParticlesAndSound_playLocalSound(
		Level level, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Local(argsOnly = true, name = "pos") Vec3 pos
	) {
		((Level4) level).playLocalSound(x, y, z, ((Vec4) pos).w + 0.5, sound, source, volume, pitch, distanceDelay);
	}

	@Redirect(method = "lambda$extinguish$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void extinguish(
		LevelAccessor level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "particlePos") Vec3 particlePos
	) {
		((LevelAccessor4) level).addParticle(particle, x, y, z, Vec4i.getW(pos) + ((Vec4) particlePos).w, xd, yd, zd, zd);
	}
}
