package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import net.minecraft.server.level.*;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkCache.class)
abstract
class ServerChunkCacheMixin implements ChunkSource4 {
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
	protected abstract ChunkHolder getVisibleChunkIfPresent(long key);

	@Shadow
	protected abstract void storeInCache(long pos, @Nullable ChunkAccess chunk, ChunkStatus status);

	@Shadow
	public abstract void addTicket(Ticket ticket, ChunkPos pos);

	@Shadow
	protected abstract boolean chunkAbsent(@Nullable ChunkHolder chunkHolder, int targetTicketLevel);

	@Shadow
	abstract boolean runDistanceManagerUpdates();

	@Shadow
	@Final
	public ChunkMap chunkMap;

	// TODO the rest

	@Overwrite
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

		//noinspection DataFlowIssue
		return this.chunkAbsent(chunkHolder, targetTicketLevel)
			? GenerationChunkHolder.UNLOADED_CHUNK_FUTURE
			: chunkHolder.scheduleChunkGenerationTask(targetStatus, this.chunkMap);
	}

	@Overwrite
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
		//noinspection ConstantValue
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
	public @Nullable LightChunk getChunkForLighting(int x, int z) {
		throw Err4.arguments2("ChunkSource4#getChunkForLighting");
	}
	@Override
	public @Nullable LightChunk getChunkForLighting(int x, int z, int w) {
		long key = ChunkPos4.pack(x, z, w);
		ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(key);
		return chunkHolder == null ? null : chunkHolder.getChunkIfPresentUnchecked(ChunkStatus.INITIALIZE_LIGHT.getParent());
	}

	// TODO the rest
}
