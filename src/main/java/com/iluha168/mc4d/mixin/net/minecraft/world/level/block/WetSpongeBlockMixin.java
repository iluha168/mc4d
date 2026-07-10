package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WetSpongeBlock.class)
class WetSpongeBlockMixin extends BlockMixin {
	@Inject(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;getZ()I"
	))
	void animateTick_ww(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci, @Share("ww") LocalDoubleRef ww) {
		ww.set(Vec4i.getW(pos));
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick_addParticle(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Share("ww") LocalDoubleRef wwRef,
		@Local(name = "direction") Direction direction,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		double ww = wwRef.get();
		if (direction == Direction4.ANA || direction == Direction4.KATA) {
			// z is needed because vanilla's else branch modifies it for ANA and KATA
			z = pos.getZ() + random.nextDouble();
			ww += direction == Direction4.ANA ? 1.0 : 0.05;
		} else {
			ww += random.nextDouble();
		}
		((LevelAccessor4) instance).addParticle(particle, x, y, z, ww, xd, yd, zd, zd);
	}
}
