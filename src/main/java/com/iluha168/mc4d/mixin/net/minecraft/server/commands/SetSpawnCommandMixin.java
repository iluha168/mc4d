package com.iluha168.mc4d.mixin.net.minecraft.server.commands;

import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.iluha168.mc4d.world.phys.RotationVec;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SetSpawnCommand.class)
class SetSpawnCommandMixin {
	@ModifyExpressionValue(method = "setSpawn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/commands/arguments/coordinates/Coordinates;getRotation(Lnet/minecraft/commands/CommandSourceStack;)Lnet/minecraft/world/phys/Vec2;"
	))
	private static Vec2 setSpawn_wRot_vRot(
		Vec2 rotationVector,
		@Share("wRot") LocalFloatRef wRot,
		@Share("vRot") LocalFloatRef vRot
	) {
		final RotationVec rot4 = (RotationVec) rotationVector;
		wRot.set(Mth.clamp(rot4.w, -90.0F, 90.0F));
		vRot.set(Mth.wrapDegrees(rot4.v));
		return rotationVector;
	}
	@Redirect(method = "setSpawn", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;of(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FF)Lnet/minecraft/world/level/storage/LevelData$RespawnData;"
	))
	private static LevelData.RespawnData setSpawn_of(
		ResourceKey<Level> dimension, BlockPos pos, float yaw, float pitch,
		@Share("wRot") LocalFloatRef wRot,
		@Share("vRot") LocalFloatRef vRot
	) {
		return LevelData4.RespawnData.of(dimension, pos, yaw, pitch, wRot.get(), vRot.get());
	}

	// TODO setSpawn success message
}
