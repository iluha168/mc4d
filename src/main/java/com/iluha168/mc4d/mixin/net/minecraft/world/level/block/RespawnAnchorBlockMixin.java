package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.Level4;
import com.iluha168.mc4d.world.level.LevelAccessor4;
import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RespawnAnchorBlock.class)
class RespawnAnchorBlockMixin {
	@Definition(id = "RESPAWN_HORIZONTAL_OFFSETS", field = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;RESPAWN_HORIZONTAL_OFFSETS:Lcom/google/common/collect/ImmutableList;")
	@Expression("RESPAWN_HORIZONTAL_OFFSETS = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS(ImmutableList<Vec3i> offsets3d) {
		return new ImmutableList.Builder<Vec3i>()
			.addAll(offsets3d.stream().map(v -> Vec4i.from(v.getX(), v.getY(), v.getZ(), +0)).iterator())
			.addAll(offsets3d.stream().map(v -> Vec4i.from(v.getX(), v.getY(), v.getZ(), -1)).iterator())
			.addAll(offsets3d.stream().map(v -> Vec4i.from(v.getX(), v.getY(), v.getZ(), +1)).iterator())
			.build();
	}

	@Redirect(method = "useWithoutItem", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;of(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FF)Lnet/minecraft/world/level/storage/LevelData$RespawnData;"
	))
	LevelData.RespawnData useWithoutItem_respawnData(ResourceKey<Level> dimension, BlockPos pos, float yaw, float pitch) {
		return LevelData4.RespawnData.of(dimension, pos, yaw, pitch, pitch, yaw);
	}
	@Redirect(method = "useWithoutItem", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	void useWithoutItem_playSound(
		ServerLevel instance, Entity entity, double x, double y, double z, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitch,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).playSound(entity, x, y, z, Vec4i.getW(pos) + 0.5, soundEvent, soundSource, volume, pitch);
	}

	@Redirect(method = "charge", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
	))
	private static void charge(
		Level instance, Entity except, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch,
		@Local(argsOnly = true, name = "pos") BlockPos pos
	) {
		((Level4) instance).playSound(except, x, y, z, Vec4i.getW(pos) + 0.5, sound, source, volume, pitch);
	}

	@Redirect(method = "animateTick", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
	))
	void animateTick(
		Level instance, ParticleOptions particle, double x, double y, double z, double xd, double yd, double zd,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(argsOnly = true, name = "random") RandomSource random
	) {
		final double w = Vec4i.getW(pos) + 0.5 + (0.5 - random.nextDouble());
		((LevelAccessor4) instance).addParticle(particle, x, y, z, w, xd, yd, zd, zd);
	}
}
