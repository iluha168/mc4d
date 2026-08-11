package com.iluha168.mc4d.mixin.net.minecraft.gametest.framework;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.gametest.framework.GameTestServer4;
import com.iluha168.mc4d.world.level.storage.LevelData4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameTestServer.class)
class GameTestServerMixin implements GameTestServer4 {
	@Shadow
	@Final
	private static int TEST_POSITION_RANGE;

	// TODO the rest

	@ModifyConstant(method = "startTests", constant = @Constant(intValue = -14999992))
	int startTests_negRANGE(int constant) {
		return -GameTestServer4.TEST_POSITION_RANGE;
	}
	@ModifyConstant(method = "startTests", constant = @Constant(intValue = 14999992))
	int startTests_posRANGE(int constant) {
		return GameTestServer4.TEST_POSITION_RANGE;
	}
	@ModifyExpressionValue(method = "startTests", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos startTests_startPos(BlockPos startPos, @Local(name = "random") RandomSource random) {
		Vec4i.setW(startPos, random.nextIntBetweenInclusive(-GameTestServer4.TEST_POSITION_RANGE, GameTestServer4.TEST_POSITION_RANGE));
		return startPos;
	}
	@Redirect(method = "startTests", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/storage/LevelData$RespawnData;of(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FF)Lnet/minecraft/world/level/storage/LevelData$RespawnData;"
	))
	LevelData.RespawnData startTests_of(ResourceKey<Level> dimension, BlockPos pos, float yaw, float pitch) {
		return LevelData4.RespawnData.of(dimension, pos, yaw, pitch, pitch, yaw);
	}

	// TODO the rest
}
