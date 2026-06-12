package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.client.multiplayer.ClientChunkCache4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientChunkCache.class)
abstract
class ClientChunkCacheMixin implements ChunkSource4 {
	@Shadow
	private volatile ClientChunkCache.Storage storage;

	@Shadow
	@Final
	private LevelChunk emptyChunk;

	@Redirect(method = "<init>", at = @At(value = "NEW", target = "(II)Lnet/minecraft/world/level/ChunkPos;"))
	ChunkPos emptyChunkPos(int x, int z) {
		assert x == 0 && z == 0;
		return ChunkPos4.from(x, z, z);
	}

	@Overwrite
	private static boolean isValidChunk(@Nullable LevelChunk chunk, int x, int z) {
		throw Err4.arguments2(null);
	}
	@Unique
	private static boolean isValidChunk(@Nullable LevelChunk chunk, int x, int z, int w) {
		if (chunk == null) {
			return false;
		}
		ChunkPos pos = chunk.getPos();
		return pos.x() == x && pos.z() == z && ChunkPos4.as(pos).w() == w;
	}

	@Overwrite
	public @Nullable LevelChunk getChunk(int x, int z, ChunkStatus targetStatus, boolean loadOrGenerate) {
		throw Err4.arguments2("ChunkSource4#getChunk");
	}
	@Override
	public @Nullable LevelChunk getChunk(int x, int z, int w, ChunkStatus targetStatus, boolean loadOrGenerate) {
		ClientChunkCache4.Storage storage4 = ClientChunkCache4.Storage.as(this.storage);
		assert storage4 != null;
		if (storage4.inRange(x, z, w)) {
			LevelChunk chunk = this.storage.getChunk(storage4.getIndex(x, z, w));
			if (isValidChunk(chunk, x, z, w)) {
				return chunk;
			}
		}

		return loadOrGenerate ? this.emptyChunk : null;
	}

	// TODO the rest

	@Mixin(ClientChunkCache.Storage.class)
	private static final class Storage implements ClientChunkCache4.Storage {
		@Shadow @Final private int viewRange;
		@Shadow @Final private int chunkRadius;
		@Shadow private volatile int viewCenterX;
		@Shadow private volatile int viewCenterZ;
		@Unique private volatile int viewCenterW;
		@Unique private volatile boolean viewCenterWNotSet;

		@Override
		public int viewCenterW() {
			if (viewCenterWNotSet) {
				throw Err4.field4missing("viewCenterW");
			}
			return this.viewCenterW;
		}
		@Override
		public void setViewCenterW(int viewCenterW) {
			this.viewCenterW = viewCenterW;
			this.viewCenterWNotSet = false;
		}

		@Definition(id = "viewRange", field = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;viewRange:I")
		@Expression("this.viewRange * this.viewRange")
		@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
		int size(int size2D) {
			this.viewCenterWNotSet = true;
			return size2D * this.viewRange;
		}

		@Overwrite
		private int getIndex(int chunkX, int chunkZ) {
			throw Err4.arguments2(null);
		}
		@Override
		public int getIndex(int chunkX, int chunkZ, int chunkW) {
			int index = Math.floorMod(chunkZ, this.viewRange);
			index = index * this.viewRange + Math.floorMod(chunkX, this.viewRange);
			index = index * this.viewRange + Math.floorMod(chunkW, this.viewRange);
			return index;
		}

		// TODO onSectionEmptinessChanged
		// TODO dropEmptySections
		// TODO addEmptySections
		// TODO refreshEmptySections

		@Overwrite
		private boolean inRange(int chunkX, int chunkZ) {
			throw Err4.arguments2(null);
		}
		@Override
		public boolean inRange(int chunkX, int chunkZ, int chunkW) {
			return Math.abs(chunkX - this.viewCenterX  ) <= this.chunkRadius
				&& Math.abs(chunkZ - this.viewCenterZ  ) <= this.chunkRadius
				&& Math.abs(chunkW - this.viewCenterW()) <= this.chunkRadius;
		}

		// TODO dumpChunks
	}
}
