package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.server.MinecraftServer4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
class MinecraftServerMixin implements MinecraftServer4 {
	@Mixin(targets = "net/minecraft/server/MinecraftServer$1")
	static class ChunkLoadStatusViewMixin implements MinecraftServer4.ChunkLoadStatusView {
		@Shadow private @Nullable ChunkMap chunkMap;

		@Shadow private int centerChunkX;
		@Shadow private int centerChunkZ;
		@Shadow @Final  int val$radius;
		@Unique private int centerChunkW;

		@Inject(method = "moveTo", at = @At("TAIL"))
		void moveTo(ResourceKey<Level> dimension, ChunkPos centerChunk, CallbackInfo ci) {
			this.centerChunkW = ChunkPos4.as(centerChunk).w();
		}

		/**
		 * @author iluha168
		 * @reason Uses 2 arguments for horizontal space. Removing the method, replacing with a method with 3 args.
		 */
		@Overwrite
		public @Nullable ChunkStatus get(int x, int z) {
			throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use MinecraftServer4::ChunkLoadStatusView#get instead"));
		}
		@Override
		public @Nullable ChunkStatus get(int x, int z, int w) {
			return this.chunkMap == null ? null : this.chunkMap.getLatestStatus(ChunkPos4.pack(
				x + this.centerChunkX - val$radius,
				z + this.centerChunkZ - val$radius,
				w + this.centerChunkW - val$radius
			));
		}
	}
}
