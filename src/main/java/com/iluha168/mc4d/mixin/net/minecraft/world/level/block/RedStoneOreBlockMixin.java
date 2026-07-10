package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneOreBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RedStoneOreBlock.class)
class RedStoneOreBlockMixin extends BlockMixin {
	@Redirect(method = "spawnParticles", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void spawnParticles(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(name = "pos", argsOnly = true) BlockPos pos,
		@Local(name = "offset") double offset,
		@Local(name = "random") RandomSource random,
		@Local(name = "direction") Direction direction,
		@Local(name = "axis") Direction.Axis axis
	) {
		final double dw = axis == Direction4.Axis.W ? 0.5 + offset * Direction4.as(direction).getStepW() : random.nextFloat();
		((LevelAccessor4) instance).addParticle(particle, x, y, z, Vec4i.getW(pos) + dw, xd, yd, zd, zd);
	}
}
