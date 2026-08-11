package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SetWorldSpawnCommand.class)
class SetWorldSpawnCommandMixin {
	@Redirect(method = "setSpawn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;of(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FF)Lnet/minecraft/world/level/storage/LevelData$RespawnData;"
	))
	private static LevelData.RespawnData setSpawn(
		ResourceKey<Level> dimension, BlockPos pos, float yaw, float pitch,
		@Local(name = "rotationVector") Vec2 rotationVector
	) {
		final RotationVec rot4 = (RotationVec) rotationVector;
		return LevelData4.RespawnData.of(dimension, pos, yaw, pitch, rot4.w, rot4.v);
	}

	// TODO setSpawn success message
}
