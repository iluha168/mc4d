package com.iluha168.mc4d.mixin.net.minecraft.client.color.block;

import com.iluha168.mc4d.client.color.block.BlockTintCache4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Mixin(BlockTintCache.class)
class BlockTintCacheMixin implements BlockTintCache4 {
	@Shadow
	@Final
	private ReentrantReadWriteLock lock;

	@Shadow
	@Final
	private Long2ObjectLinkedOpenHashMap<BlockTintCache.CacheData> cache;

	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 16*16))
	int expectedSize(int constant) {
		return 16*16*16;
	}

	@ModifyExpressionValue(method = "getColor", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/color/block/BlockTintCache$CacheData;isInvalidated()Z"
	))
	boolean getColor_ifDifferent(
		boolean original,
		@Local(argsOnly = true, name = "pos") BlockPos pos,
		@Local(name = "chunkInfo") BlockTintCache.LatestCacheInfo chunkInfo,
		@Share("chunkW") LocalIntRef chunkW
	) {
		chunkW.set(SectionPos.blockToSectionCoord(Vec4i.getW(pos)));
		return original || ((BlockTintCache4.LatestCacheInfo) chunkInfo).w() != chunkW.get();
	}
	@Definition(id = "findOrCreateChunkCache", method = "Lnet/minecraft/client/color/block/BlockTintCache;findOrCreateChunkCache(II)Lnet/minecraft/client/color/block/BlockTintCache$CacheData;")
	@Expression("this.findOrCreateChunkCache(?, ?)")
	@Redirect(method = "getColor", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockTintCache.CacheData getColor_findOrCreateChunkCache(
		BlockTintCache This, int x, int z,
		@Local(name = "chunkInfo") BlockTintCache.LatestCacheInfo chunkInfo,
		@Share("chunkW") LocalIntRef chunkW
	) {
		((BlockTintCache4.LatestCacheInfo) chunkInfo).setW(chunkW.get());
		return this.findOrCreateChunkCache(x, z, chunkW.get());
	}
	@Definition(id = "index", local = @Local(type = int.class, name = "index"))
	@Expression("index = @(?)")
	@ModifyExpressionValue(method = "getColor", at = @At("MIXINEXTRAS:EXPRESSION"))
	int getColor_index(int original, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		final int w = SectionPos.sectionRelative(Vec4i.getW(pos));
		return original | w << 8;
	}

	@Overwrite
	@Deprecated
	public void invalidateForChunk(int chunkX, int chunkZ) {
		throw Err4.arguments2("BlockTintCache4#invalidateForChunk");
	}
	@Override
	public void invalidateForChunk(int chunkX, int chunkZ, int chunkW) {
		try {
			this.lock.writeLock().lock();

			for (int offsetX = -1; offsetX <= 1; offsetX++)
				for (int offsetZ = -1; offsetZ <= 1; offsetZ++)
					for (int offsetW = -1; offsetW <= 1; offsetW++) {
						long key = ChunkPos4.pack(chunkX + offsetX, chunkZ + offsetZ, chunkW + offsetW);
						BlockTintCache.CacheData removed = this.cache.remove(key);
						if (removed != null) {
							removed.invalidate();
						}
					}
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Overwrite
	@Deprecated
	private BlockTintCache.CacheData findOrCreateChunkCache(int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private BlockTintCache.CacheData findOrCreateChunkCache(int x, int z, int w) {
		long key = ChunkPos4.pack(x, z, w);
		this.lock.readLock().lock();

		try {
			BlockTintCache.CacheData existing = this.cache.get(key);
			if (existing != null) {
				return existing;
			}
		} finally {
			this.lock.readLock().unlock();
		}

		this.lock.writeLock().lock();

		BlockTintCache.CacheData newCache;
		try {
			BlockTintCache.CacheData existingNow = this.cache.get(key);
			if (existingNow == null) {
				newCache = new BlockTintCache.CacheData();
				if (this.cache.size() >= 16*16*16) {
					BlockTintCache.CacheData cacheData = this.cache.removeFirst();
					if (cacheData != null) {
						cacheData.invalidate();
					}
				}

				this.cache.put(key, newCache);
				return newCache;
			}

			newCache = existingNow;
		} finally {
			this.lock.writeLock().unlock();
		}

		return newCache;
	}

	@Mixin(BlockTintCache.CacheData.class)
	private static class CacheDataMixin {
		@Redirect(method = "<clinit>", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/Mth;square(I)I"
		))
		private static int BLOCKS_PER_LAYER(int x) {
			return x * x * x;
		}
	}

	@Mixin(BlockTintCache.LatestCacheInfo.class)
	private static class LatestCacheInfoMixin implements BlockTintCache4.LatestCacheInfo {
		@Unique private int w = Integer.MIN_VALUE;

		@Inject(method = "<init>", at = @At("TAIL"))
		void init(CallbackInfo ci) {
			this.w = Integer.MIN_VALUE;
		}

		@Override
		public int w() {
			return this.w;
		}
		@Override
		public void setW(int w) {
			this.w = w;
		}
	}
}
