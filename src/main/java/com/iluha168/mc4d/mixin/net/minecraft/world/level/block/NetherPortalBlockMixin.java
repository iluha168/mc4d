package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.api.net.minecraft.world.level.Level4;
import com.iluha168.mc4d.api.net.minecraft.world.level.LevelAccessor4;
import com.iluha168.mc4d.api.net.minecraft.world.level.block.Block4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/Block;column(DDDD)Lnet/minecraft/world/phys/shapes/VoxelShape;"
	))
	private static VoxelShape SHAPES(double sizeX, double sizeZ, double minY, double maxY) {
		return Block4.column(sizeX, sizeZ, sizeX, minY, maxY);
	}

	// TODO getPortalDestination
	// TODO getExitPortal
	// TODO getDimensionTransitionFromExit
	// TODO createDimensionTransition

	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
	))
	void animateTick_playLocalSound(
		Level instance, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, boolean distanceDelay,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).playLocalSound(x, y, z, Vec4i.getW(pos) + 0.5, sound, source, volume, pitch, distanceDelay);
	}
	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick_addParticle(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Local(name = "flip") int flip
	) {
		final BlockPos4 pos4 = (BlockPos4) pos;
		final double posW = Vec4i.getW(pos);
		double w = posW + random.nextDouble();
		double wa = (random.nextFloat() - 0.5) * 0.5;
		Block This = (Block) (Object) this;
		if (!level.getBlockState(pos4.kata()).is(This) && !level.getBlockState(pos4.ana()).is(This)) {
			// z changed by vanilla's else branch; rollback
			z = pos.getZ() + random.nextDouble();
			zd = wa;

			w = posW + 0.5 + 0.25 * flip;
			wa = random.nextFloat() * 2.0F * flip;
		}
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, wa);
	}
}
