package com.iluha168.mc4d.mixin.net.minecraft.client.gui.components;

import com.iluha168.mc4d.server.level.ServerChunkCache4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.LevelReader4;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(DebugScreenOverlay.class)
class DebugScreenOverlayMixin {
	// TODO everything else

	@Shadow
	private @Nullable ChunkPos lastPos;

	@Redirect(method = "getServerChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerChunkCache;getChunkFuture(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Ljava/util/concurrent/CompletableFuture;"
	))
	CompletableFuture<ChunkResult<ChunkAccess>> getServerChunk(ServerChunkCache instance, int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
		return ((ServerChunkCache4) instance).getChunkFuture(x, z, ChunkPos4.as(this.lastPos).w(), targetStatus, loadOrGenerate);
	}

	@Redirect(method = "getClientChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientLevel;getChunk(II)Lnet/minecraft/world/level/chunk/LevelChunk;"
	))
	LevelChunk getClientChunk(ClientLevel instance, int x, int z) {
		return (LevelChunk) ((LevelReader4) instance).getChunk(x, z, ChunkPos4.as(this.lastPos).w());
	}

	// TODO everything else
}
