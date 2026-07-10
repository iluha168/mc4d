package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BaseFireBlock.class)
abstract
class BaseFireBlockMixin extends BlockMixin {
	@Shadow
	protected abstract boolean canBurn(BlockState state);

	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void animateTick_playLocalSound(Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		((Level4) instance).playLocalSound(x, y, z, Vec4i.getW(pos) + 0.5, sound, source, volume, pitch, distanceDelay);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick_addParticle_3(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		final double ww = Vec4i.getW(pos) + random.nextDouble();
		((LevelAccessor4) instance).addParticle(particle, x, y, z, ww, xd, yd, zd, zd);
	}
	@Inject(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;above()Lnet/minecraft/core/BlockPos;"
	))
	void animateTick_addParticle_4(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		final BlockPos4 pos4 = (BlockPos4) pos;
		final double posW = Vec4i.getW(pos);
		final LevelAccessor4 level4 = (LevelAccessor4) level;

		if (this.canBurn(level.getBlockState(pos4.kata()))) {
			for (int i = 0; i < 2; i++) {
				final double xx = pos.getX() + random.nextDouble();
				final double yy = pos.getY() + random.nextDouble();
				final double zz = pos.getZ() + random.nextDouble();
				final double ww = posW       + random.nextDouble() * 0.1F;
				level4.addParticle(ParticleTypes.LARGE_SMOKE, xx, yy, zz, ww, 0.0, 0.0, 0.0, 0.0);
			}
		}

		if (this.canBurn(level.getBlockState(pos4.ana()))) {
			for (int i = 0; i < 2; i++) {
				final double xx = pos.getX() + random.nextDouble();
				final double yy = pos.getY() + random.nextDouble();
				final double zz = pos.getZ() + random.nextDouble();
				final double ww = posW   + 1 - random.nextDouble() * 0.1F;
				level4.addParticle(ParticleTypes.LARGE_SMOKE, xx, yy, zz, ww, 0.0, 0.0, 0.0, 0.0);
			}
		}
	}
}
