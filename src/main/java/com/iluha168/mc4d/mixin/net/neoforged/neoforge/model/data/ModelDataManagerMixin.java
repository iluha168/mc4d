package com.iluha168.mc4d.mixin.net.neoforged.neoforge.model.data;

import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.net.neoforged.neoforge.model.data.ModelDataManager4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelDataManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelDataManager.class)
class ModelDataManagerMixin implements ModelDataManager4 {
	@Final
	@Shadow
	private Thread owningThread;
	@Final
	@Shadow
	private Long2ObjectMap<Long2ObjectMap<ModelData>> modelDataCache;
	@Final
	@Shadow
	public static Long2ObjectFunction<ModelData> EMPTY_SNAPSHOT;

	@Shadow
	private void refreshAt(long section) {}
	@Shadow
	private boolean isOtherThread() { return false; }

	@Overwrite
	@Deprecated
	public Long2ObjectFunction<ModelData> snapshotSectionRegion(int sectionMinX, int sectionMinY, int sectionMinZ, int sectionMaxX, int sectionMaxY, int sectionMaxZ) {
		throw Err4.arguments3("ModelDataManager4#snapshotSectionRegion");
	}
	@Override
	public Long2ObjectFunction<ModelData> snapshotSectionRegion(
		int sectionMinX, int sectionMinY, int sectionMinZ, int sectionMinW,
		int sectionMaxX, int sectionMaxY, int sectionMaxZ, int sectionMaxW
	) {
		if (this.isOtherThread()) {
			throw new UnsupportedOperationException("Cannot snapshot active manager outside the owning thread: " + this.owningThread);
		}
		Long2ObjectMap<ModelData> cache = new Long2ObjectOpenHashMap<>();
		cache.defaultReturnValue(ModelData.EMPTY);
		for (int x = sectionMinX; x <= sectionMaxX; x++) {
			for (int y = sectionMinY; y <= sectionMaxY; y++) {
				for (int z = sectionMinZ; z <= sectionMaxZ; z++) {
					for (int w = sectionMinW; w <= sectionMaxW; w++) {
						long sectionPos = SectionPos4.asLong(x, y, z, w);
						this.refreshAt(sectionPos);
						cache.putAll(this.modelDataCache.getOrDefault(sectionPos, Long2ObjectMaps.emptyMap()));
					}
				}
			}
		}
		return cache.isEmpty() ? EMPTY_SNAPSHOT : cache;
	}

	@Redirect(method = "onChunkUnload", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	private static long onChunkUnload(int x, int y, int z, @Local(name = "chunk") ChunkPos chunk) {
		return SectionPos4.asLong(x, y, z, ChunkPos4.as(chunk).w());
	}
}