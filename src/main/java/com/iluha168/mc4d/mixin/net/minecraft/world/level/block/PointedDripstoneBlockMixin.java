package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PointedDripstoneBlock.class)
class PointedDripstoneBlockMixin extends BlockMixin {
	@Redirect(
		method = "spawnDripParticle(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;Lnet/minecraft/core/BlockPos;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
		)
	)
	private static void spawnDripParticle(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "stalactiteTipPos") BlockPos stalactiteTipPos,
		@Local(name = "offset") Vec3 offset
	) {
		final double w = Vec4i.getW(stalactiteTipPos) + 0.5 + ((Vec4) offset).w;
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, zd);
	}
}
