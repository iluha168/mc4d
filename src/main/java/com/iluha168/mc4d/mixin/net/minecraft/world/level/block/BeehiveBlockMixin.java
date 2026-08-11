package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.iluha168.mc4d.world.entity.item.ItemEntity4;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.phys.AABB4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BeehiveBlock.class)
class BeehiveBlockMixin {
	@Redirect(method = "angerNearbyBees", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/AABB;"
	))
	AABB angerNearbyBees_init(BlockPos pos) {
		return new AABB4(pos);
	}
	@Redirect(method = "angerNearbyBees", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/phys/AABB;inflate(DDD)Lnet/minecraft/world/phys/AABB;"
	))
	AABB angerNearbyBees_inflate(AABB instance, double xAdd, double yAdd, double zAdd) {
		return ((AABB4) instance).inflate(xAdd, yAdd, zAdd, zAdd);
	}

	@Redirect(method = "useItemOn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void useItemOn(
		Level instance, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch,
		@Local(argsOnly = true, name = "player") Player player
	) {
		((Level4) instance).playSound(except, x, y, z, ((Entity4) player).getW(), sound, source, volume, pitch);
	}

	@Redirect(method = "spawnParticle", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/BeehiveBlock;spawnFluidParticle(Lnet/minecraft/world/level/Level;DDDDD)V"
	))
	void spawnParticle(
		BeehiveBlock instance, Level level, double x1, double x2, double z1, double z2, double y,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "dripShape") VoxelShape dripShape
	) {
		final int w = Vec4i.getW(pos);
		this.spawnFluidParticle(
			level,
			x1, x2,
			z1, z2,
			w + dripShape.min(Direction4.Axis.W), w + dripShape.max(Direction4.Axis.W),
			y
		);
	}

	@Overwrite
	@Deprecated
	private void spawnFluidParticle(Level level, double x1, double x2, double z1, double z2, double y) {
		throw Err4.arguments3(null);
	}
	@Unique
	private void spawnFluidParticle(Level level, double x1, double x2, double z1, double z2, double w1, double w2, double y) {
		((LevelAccessor4) level).addParticle(
			ParticleTypes.DRIPPING_HONEY,
			Mth.lerp(level.getRandom().nextDouble(), x1, x2),
			y,
			Mth.lerp(level.getRandom().nextDouble(), z1, z2),
			Mth.lerp(level.getRandom().nextDouble(), w1, w2),
			0.0, 0.0, 0.0, 0.0
		);
	}

	@Redirect(method = "playerWillDestroy", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"
	))
	ItemEntity playerWillDestroy(
		Level level, double x, double y, double z, ItemStack itemStack,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		return ItemEntity4.from(level, x, y, z, Vec4i.getW(pos), itemStack);
	}
}
