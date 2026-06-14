package com.iluha168.mc4d.mixin.net.minecraft.gametest.framework;

import com.iluha168.mc4d.server.level.ServerLevel4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/gametest/framework/GameTestRunner$1")
class GameTestRunnerMixin {
	@Redirect(method = "lambda$testCompleted$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;setChunkForced(IIZ)Z"
	))
	boolean testCompleted(ServerLevel instance, int chunkX, int chunkZ, boolean forced, @Local(argsOnly = true, name = "pos") long pos) {
		return ((ServerLevel4) instance).setChunkForced(chunkX, chunkZ, ChunkPos4.getW(pos), forced);
	}

	@Redirect(method = "lambda$testFailed$0", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;setChunkForced(IIZ)Z"
	))
	boolean testFailed(ServerLevel instance, int chunkX, int chunkZ, boolean forced, @Local(argsOnly = true, name = "pos") long pos) {
		return ((ServerLevel4) instance).setChunkForced(chunkX, chunkZ, ChunkPos4.getW(pos), forced);
	}
}
