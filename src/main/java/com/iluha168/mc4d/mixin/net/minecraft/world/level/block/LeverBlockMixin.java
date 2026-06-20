package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeverBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LeverBlock.class)
class LeverBlockMixin {
	@Redirect(method = "makeParticle", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelAccessor;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void makeParticle(
		LevelAccessor level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(name = "opposite") Direction opposite,
		@Local(name = "oppositeConnect") Direction oppositeConnect
	) {
		final double w = Vec4i.getW(pos) + 0.5 + 0.1 * Direction4.as(opposite).getStepW() + 0.2 * Direction4.as(oppositeConnect).getStepW();
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, zd);
	}
}
