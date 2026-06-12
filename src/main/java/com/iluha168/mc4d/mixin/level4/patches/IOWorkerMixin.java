package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.IOWorker;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

import java.util.BitSet;
import java.util.concurrent.CompletableFuture;

@Mixin(IOWorker.class)
abstract
class IOWorkerMixin {
	@Shadow
	@Final
	private Long2ObjectLinkedOpenHashMap<CompletableFuture<BitSet>> regionCacheForBlender;

	@Shadow
	public abstract CompletableFuture<Void> scanChunk(ChunkPos pos, StreamTagVisitor visitor);

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	protected abstract boolean isOldChunk(CompoundTag tag);

	@Overwrite // Honestly, this would take a hundred mixins, and nobody else is going to modify this, right?
	public boolean isOldChunkAround(ChunkPos pos, int range) {
		ChunkPos from = ChunkPos4.from(pos.x() - range, pos.z() - range, ChunkPos4.as(pos).w() - range);
		ChunkPos to   = ChunkPos4.from(pos.x() + range, pos.z() + range, ChunkPos4.as(pos).w() + range);
		ChunkPos4 from4 = ChunkPos4.as(from);
		ChunkPos4 to4   = ChunkPos4.as(to);

		for (int regionX = from.getRegionX(); regionX <= to.getRegionX(); regionX++)
			for (int regionZ = from.getRegionZ(); regionZ <= to.getRegionZ(); regionZ++)
				for (int regionW = from4.getRegionW(); regionW <= to4.getRegionW(); regionW++) {
					BitSet data = this.getOrCreateOldDataForRegion(regionX, regionZ, regionW).join();
					if (!data.isEmpty()) {
						ChunkPos minChunkPos = ChunkPos4.minFromRegion(regionX, regionZ, regionW);
						ChunkPos4 minChunkPos4 = ChunkPos4.as(minChunkPos);
						int startChunkX = Math.max(from.x() - minChunkPos.x(), 0);
						int startChunkZ = Math.max(from.z() - minChunkPos.z(), 0);
						int startChunkW = Math.max(from4.w() - minChunkPos4.w(), 0);
						int endChunkX = Math.min(to.x() - minChunkPos.x(), ChunkPos.REGION_MAX_INDEX);
						int endChunkZ = Math.min(to.z() - minChunkPos.z(), ChunkPos.REGION_MAX_INDEX);
						int endChunkW = Math.min(to4.w() - minChunkPos4.w(), ChunkPos.REGION_MAX_INDEX);

						for (int x = startChunkX; x <= endChunkX; x++)
							for (int z = startChunkZ; z <= endChunkZ; z++)
								for (int w = startChunkW; w <= endChunkW; w++) {
									int chunkIndex = (w * ChunkPos.REGION_SIZE + z) * ChunkPos.REGION_SIZE + x;
									if (data.get(chunkIndex)) {
										return true;
									}
								}
					}
				}

		return false;
	}

	@Overwrite
	private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int regionX, int regionZ) {
		throw Err4.arguments2(null);
	}
	@Unique
	private CompletableFuture<BitSet> getOrCreateOldDataForRegion(int regionX, int regionZ, int regionW) {
		long regionPos = ChunkPos4.pack(regionX, regionZ, regionW);
		//noinspection SynchronizeOnNonFinalField
		synchronized (this.regionCacheForBlender) {
			CompletableFuture<BitSet> result = this.regionCacheForBlender.getAndMoveToFirst(regionPos);
			if (result == null) {
				result = this.createOldDataForRegion(regionX, regionZ, regionW);
				this.regionCacheForBlender.putAndMoveToFirst(regionPos, result);
				if (this.regionCacheForBlender.size() > ChunkPos.REGION_SIZE*ChunkPos.REGION_SIZE*ChunkPos.REGION_SIZE) {
					this.regionCacheForBlender.removeLast();
				}
			}

			return result;
		}
	}

	@Overwrite
	private CompletableFuture<BitSet> createOldDataForRegion(int regionX, int regionZ) {
		throw Err4.arguments2(null);
	}
	@Unique
	private CompletableFuture<BitSet> createOldDataForRegion(int regionX, int regionZ, int regionW) {
		return CompletableFuture.supplyAsync(
			() -> {
				ChunkPos from = ChunkPos4.minFromRegion(regionX, regionZ, regionW);
				ChunkPos to = ChunkPos4.maxFromRegion(regionX, regionZ, regionW);
				BitSet resultSet = new BitSet();
				ChunkPos.rangeClosed(from, to)
					.forEach(
						pos -> {
							CollectFields collectFields = new CollectFields(
								new FieldSelector(IntTag.TYPE, "DataVersion"), new FieldSelector(CompoundTag.TYPE, "blending_data")
							);

							try {
								this.scanChunk(pos, collectFields).join();
							} catch (Exception e) {
								LOGGER.warn("Failed to scan chunk {}", pos, e);
								return;
							}

							if (collectFields.getResult() instanceof CompoundTag chunkTag && this.isOldChunk(chunkTag)) {
								int chunkIndex = (ChunkPos4.as(pos).getRegionLocalW() * ChunkPos.REGION_SIZE + pos.getRegionLocalZ()) * ChunkPos.REGION_SIZE + pos.getRegionLocalX();
								resultSet.set(chunkIndex);
							}
						}
					);
				return resultSet;
			},
			Util.backgroundExecutor()
		);
	}
}
