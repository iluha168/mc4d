package com.iluha168.mc4d.mixin.net.minecraft.server.level;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk.ChunkSourceMixin;
import com.iluha168.mc4d.server.level.ServerChunkCache4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.*;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkCache.class)
class ServerChunkCacheMixin extends ChunkSourceMixin implements ServerChunkCache4 {
	@Shadow
	@Final
	private Thread mainThread;

	@Shadow
	@Final
	private ServerChunkCache.MainThreadExecutor mainThreadProcessor;

	@Shadow
	@Final
	private static int CACHE_SIZE;

	@Shadow
	@Final
	private long[] lastChunkPos;

	@Shadow
	@Final
	private @Nullable ChunkStatus[] lastChunkStatus;

	@Shadow
	@Final
	private @Nullable ChunkAccess[] lastChunk;

	@Shadow
	@Nullable
	private ChunkHolder getVisibleChunkIfPresent(long key) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private void storeInCache(long pos, @Nullable ChunkAccess chunk, ChunkStatus status) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	public void addTicket(Ticket ticket, ChunkPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private boolean chunkAbsent(@Nullable ChunkHolder chunkHolder, int targetTicketLevel) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	boolean runDistanceManagerUpdates() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	public ChunkMap chunkMap;

	// TODO the rest

	@Overwrite
	@Deprecated
	public @Nullable LevelChunk getChunkNow(int x, int z) {
		throw Err4.arguments2("ChunkSource4#getChunkNow");
	}
	@Override
	public @Nullable LevelChunk getChunkNow(int x, int z, int w) {
		if (Thread.currentThread() != this.mainThread) {
			return null;
		}
		Profiler.get().incrementCounter("getChunkNow");
		long pos = ChunkPos4.pack(x, z, w);

		for (int i = 0; i < CACHE_SIZE; i++) {
			if (pos == this.lastChunkPos[i] && this.lastChunkStatus[i] == ChunkStatus.FULL) {
				ChunkAccess chunkAccess = this.lastChunk[i];
				return chunkAccess instanceof LevelChunk ? (LevelChunk)chunkAccess : null;
			}
		}

		ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(pos);
		if (chunkHolder == null) {
			return null;
		}
		if (chunkHolder.currentlyLoading != null) return chunkHolder.currentlyLoading; // Forge: If the requested chunk is loading, bypass the future chain to prevent a deadlock.
		ChunkAccess chunk = chunkHolder.getChunkIfPresent(ChunkStatus.FULL);
		if (chunk != null) {
			this.storeInCache(pos, chunk, ChunkStatus.FULL);
			if (chunk instanceof LevelChunk) {
				return (LevelChunk) chunk;
			}
		}

		return null;
	}

	@Overwrite
	@Deprecated
	public CompletableFuture<ChunkResult<ChunkAccess>> getChunkFuture(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
		throw Err4.arguments2("ServerChunkCache4#getChunkFuture");
	}
	@Override
	public CompletableFuture<ChunkResult<ChunkAccess>> getChunkFuture(int x, int z, int w, ChunkStatus targetStatus, boolean loadOrGenerate) {
		boolean isMainThread = Thread.currentThread() == this.mainThread;
		CompletableFuture<ChunkResult<ChunkAccess>> serverFuture;
		if (isMainThread) {
			serverFuture = this.getChunkFutureMainThread(x, z, w, targetStatus, loadOrGenerate);
			this.mainThreadProcessor.managedBlock(serverFuture::isDone);
		} else {
			serverFuture = CompletableFuture.
				supplyAsync(() -> this.getChunkFutureMainThread(x, z, w, targetStatus, loadOrGenerate), this.mainThreadProcessor)
				.thenCompose(chunk -> chunk);
		}
		return serverFuture;
	}

	@Overwrite
	@Deprecated
	private CompletableFuture<ChunkResult<ChunkAccess>> getChunkFutureMainThread(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
		throw Err4.arguments2(null);
	}
	@Unique
	private CompletableFuture<ChunkResult<ChunkAccess>> getChunkFutureMainThread(int x, int z, int w, ChunkStatus targetStatus, boolean loadOrGenerate) {
		ChunkPos pos = ChunkPos4.from(x, z, w);
		long key = pos.pack();
		int targetTicketLevel = ChunkLevel.byStatus(targetStatus);
		ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(key);
		if (loadOrGenerate) {
			this.addTicket(new Ticket(TicketType.UNKNOWN, targetTicketLevel), pos);
			if (this.chunkAbsent(chunkHolder, targetTicketLevel)) {
				ProfilerFiller profiler = Profiler.get();
				profiler.push("chunkLoad");
				this.runDistanceManagerUpdates();
				chunkHolder = this.getVisibleChunkIfPresent(key);
				profiler.pop();
				if (this.chunkAbsent(chunkHolder, targetTicketLevel)) {
					throw Util.pauseInIde(new IllegalStateException("No chunk holder after ticket has been added"));
				}
			}
		}

		return this.chunkAbsent(chunkHolder, targetTicketLevel)
			? GenerationChunkHolder.UNLOADED_CHUNK_FUTURE
			: chunkHolder.scheduleChunkGenerationTask(targetStatus, this.chunkMap);
	}

	@Overwrite
	@Deprecated
	public @Nullable ChunkAccess getChunk(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
		throw Err4.arguments2("ChunkSource4#getChunk");
	}
	@Override
	public @Nullable ChunkAccess getChunk(int x, int z, int w, ChunkStatus targetStatus, boolean loadOrGenerate) {
		if (Thread.currentThread() != this.mainThread) {
			return CompletableFuture.supplyAsync(() -> this.getChunk(x, z, targetStatus, loadOrGenerate), this.mainThreadProcessor).join();
		}
		ProfilerFiller profiler = Profiler.get();
		profiler.incrementCounter("getChunk");
		long pos = ChunkPos4.pack(x, z, w);

		for (int i = 0; i < CACHE_SIZE; i++) {
			if (pos == this.lastChunkPos[i] && targetStatus == this.lastChunkStatus[i]) {
				ChunkAccess chunkAccess = this.lastChunk[i];
				if (chunkAccess != null || !loadOrGenerate) {
					return chunkAccess;
				}
			}
		}

		ChunkHolder chunkholder = this.getVisibleChunkIfPresent(pos);
		if (chunkholder != null && chunkholder.currentlyLoading != null)
			return chunkholder.currentlyLoading; // Neo: If the requested chunk is loading, bypass the future chain to prevent a deadlock.

		profiler.incrementCounter("getChunkCacheMiss");
		CompletableFuture<ChunkResult<ChunkAccess>> serverFuture = this.getChunkFutureMainThread(x, z, w, targetStatus, loadOrGenerate);
		this.mainThreadProcessor.managedBlock(serverFuture::isDone);
		ChunkResult<ChunkAccess> chunkResult = serverFuture.join();
		ChunkAccess chunk = chunkResult.orElse(null);
		if (chunk == null && loadOrGenerate) {
			throw Util.pauseInIde(new IllegalStateException("Chunk not there when requested: " + chunkResult.getError()));
		}
		this.storeInCache(pos, chunk, targetStatus);
		return chunk;
	}

	// TODO the rest

	@Overwrite
	@Deprecated
	public boolean hasChunk(int x, int z) {
		throw Err4.arguments2("ChunkSource4#hasChunk");
	}
	@Override
	public boolean hasChunk(int x, int z, int w) {
		ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(ChunkPos4.from(x, z, w).pack());
		int targetTicketLevel = ChunkLevel.byStatus(ChunkStatus.FULL);
		return !this.chunkAbsent(chunkHolder, targetTicketLevel);
	}

	@SuppressWarnings({"RedundantMethodOverride", "deprecation"})
	@Overwrite
	@Deprecated
	public @Nullable LightChunk getChunkForLighting(int x, int z) {
		throw Err4.arguments2("ChunkSource4#getChunkForLighting");
	}
	@Override
	public @Nullable LightChunk getChunkForLighting(int x, int z, int w) {
		long key = ChunkPos4.pack(x, z, w);
		ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(key);
		return chunkHolder == null ? null : chunkHolder.getChunkIfPresentUnchecked(ChunkStatus.INITIALIZE_LIGHT.getParent());
	}

	@Redirect(method = "blockChanged", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/ChunkPos;pack(II)J"
	))
	long blockChanged(int x, int z, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		int wc = SectionPos.blockToSectionCoord(Vec4i.getW(pos));
		return ChunkPos4.pack(x, z, wc);
	}
}
