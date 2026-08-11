package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.Containers4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.level.block.entity.CampfireBlockEntity4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CampfireBlockEntity.class)
class CampfireBlockEntityMixin implements CampfireBlockEntity4 {
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 4))
	private int init(final int numSlots) {
		return CampfireBlockEntity4.NUM_SLOTS;
	}

	@Redirect(method = "cookTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"
	))
	private static void cookTick_dropItemStack(
		Level level, double x, double y, double z, ItemStack itemStack,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		Containers4.dropItemStack(level, x, y, z, Vec4i.getW(pos), itemStack);
	}

	// TODO particleTick getClockWise
	@Redirect(method = "particleTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	private static void particleTick_addParticle(
		Level level, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(name = "distanceFromCenter") float distanceFromCenter,
		@Local(name = "slot") int slot
	) {
		// Vanilla 4-corner cycle run twice: W-, then W+.
		// Not mathematically accurate, but if you see this, might as well submit a pull request? :P
		final double w = Vec4i.getW(pos) + 0.5 + (slot < CampfireBlockEntity4.NUM_SLOTS/2 ? -distanceFromCenter : distanceFromCenter);
		((LevelAccessor4) level).addParticle(particle, x, y, z, w, xd, yd, zd, zd);
	}
}
