package com.iluha168.mc4d.mixin.net.minecraft.client.multiplayer;

import com.iluha168.mc4d.client.multiplayer.ClientChunkCache4;
import com.iluha168.mc4d.client.renderer.LevelRenderer4;
import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.function.Consumer;

@Mixin(ClientChunkCache.class)
abstract
class ClientChunkCacheMixin implements ClientChunkCache4, ChunkSource4 {
	@Shadow
	private volatile ClientChunkCache.Storage storage;

	@Shadow
	@Final
	private LevelChunk emptyChunk;

	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	@Final
	public ClientLevel level;

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

	@Overwrite
	@Deprecated
	public void replaceBiomes(int chunkX, int chunkZ, FriendlyByteBuf readBuffer) {
		throw Err4.arguments2("ClientChunkCache4#replaceBiomes");
	}
	@Override
	public void replaceBiomes(int chunkX, int chunkZ, int chunkW, FriendlyByteBuf readBuffer) {
		if (!ClientChunkCache4.Storage.as(this.storage).inRange(chunkX, chunkZ, chunkW)) {
			//noinspection LoggingSimilarMessage
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}, {}", chunkX, chunkZ, chunkW);
		} else {
			int index = ClientChunkCache4.Storage.as(this.storage).getIndex(chunkX, chunkZ, chunkW);
			LevelChunk chunk = this.storage.getChunk(index);
			if (!isValidChunk(chunk, chunkX, chunkZ, chunkW)) {
				LOGGER.warn("Ignoring chunk since it's not present: {}, {}, {}", chunkX, chunkZ, chunkW);
			} else {
				chunk.replaceBiomes(readBuffer);
			}
		}
	}

	@Overwrite
	@Deprecated
	public @Nullable LevelChunk replaceWithPacketData(
		int chunkX,
		int chunkZ,
		FriendlyByteBuf readBuffer,
		Map<Heightmap.Types, long[]> heightmaps,
		Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> blockEntities
	) {
		throw Err4.arguments2("ClientChunkCache4#replaceWithPacketData");
	}
	@Override
	public @Nullable LevelChunk replaceWithPacketData(int chunkX, int chunkZ, int chunkW, FriendlyByteBuf readBuffer, Map<Heightmap.Types, long[]> heightmaps, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> blockEntities) {
		ClientChunkCache4.Storage storage4 = ClientChunkCache4.Storage.as(this.storage);
		if (!storage4.inRange(chunkX, chunkZ, chunkW)) {
			LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}, {}", chunkX, chunkZ, chunkW);
			return null;
		}
		int index = storage4.getIndex(chunkX, chunkZ, chunkW);
		LevelChunk chunk = this.storage.getChunk(index);
		ChunkPos pos = ChunkPos4.from(chunkX, chunkZ, chunkW);
		if (!isValidChunk(chunk, chunkX, chunkZ, chunkW)) {
			chunk = new LevelChunk(this.level, pos);
			chunk.replaceWithPacketData(readBuffer, heightmaps, blockEntities);
			this.storage.replace(index, chunk);
		} else {
			chunk.replaceWithPacketData(readBuffer, heightmaps, blockEntities);
			this.storage.refreshEmptySections(chunk);
		}

		this.level.onChunkLoaded(pos);
		//noinspection UnstableApiUsage
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.ChunkEvent.Load(chunk, false));
		return chunk;
	}

	@Overwrite
	@Deprecated
	public void updateViewCenter(int x, int z) {
		throw Err4.arguments2("ClientChunkCache4#updateViewCenter");
	}
	@Override
	public void updateViewCenter(int x, int z, int w) {
		this.storage.viewCenterX = x;
		this.storage.viewCenterZ = z;
		ClientChunkCache4.Storage.as(this.storage).setViewCenterW(w);
	}

	@ModifyExpressionValue(method = "updateViewRadius", at = @At(
		value = "NEW",
		target = "(Lnet/minecraft/client/multiplayer/ClientChunkCache;I)Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;"
	))
	ClientChunkCache.Storage updateViewRadius_viewCenterW(ClientChunkCache.Storage newStorage) {
		ClientChunkCache4.Storage.as(newStorage).setViewCenterW(ClientChunkCache4.Storage.as(this.storage).viewCenterW());
		return newStorage;
	}
	@Redirect(method = "updateViewRadius", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;inRange(II)Z"
	))
	boolean updateViewRadius_inRange(ClientChunkCache.Storage newStorage, int chunkX, int chunkZ, @Local(name = "pos") ChunkPos pos) {
		return ClientChunkCache4.Storage.as(newStorage).inRange(chunkX, chunkZ, ChunkPos4.as(pos).w());
	}
	@Redirect(method = "updateViewRadius", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;getIndex(II)I"
	))
	int updateViewRadius_getIndex(ClientChunkCache.Storage newStorage, int chunkX, int chunkZ, @Local(name = "pos") ChunkPos pos) {
		return ClientChunkCache4.Storage.as(newStorage).getIndex(chunkX, chunkZ, ChunkPos4.as(pos).w());
	}

	@Redirect(method = "onLightUpdate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/LevelRenderer;setSectionDirty(III)V"
	))
	void onLightUpdate(LevelRenderer instance, int sectionX, int sectionY, int sectionZ, @Local(argsOnly = true, name = "pos") SectionPos pos) {
		((LevelRenderer4) instance).setSectionDirty(sectionX, sectionY, sectionZ, ((SectionPos4) pos).w());
	}

	@Overwrite
	@Deprecated
	public void onSectionEmptinessChanged(int sectionX, int sectionY, int sectionZ, boolean empty) {
		throw Err4.arguments3("ClientChunkCache4#onSectionEmptinessChanged");
	}
	@Override
	public void onSectionEmptinessChanged(int sectionX, int sectionY, int sectionZ, int sectionW, boolean empty) {
		ClientChunkCache4.Storage.as(this.storage).onSectionEmptinessChanged(sectionX, sectionY, sectionZ, sectionW, empty);
	}

	@Mixin(ClientChunkCache.Storage.class)
	private static final class StorageMixin implements ClientChunkCache4.Storage {
		@Shadow @Final ClientChunkCache this$0;
		@Shadow @Final private int viewRange;
		@Shadow @Final private int chunkRadius;
		@Shadow @Final private LongOpenHashSet loadedEmptySections;
		@Shadow public volatile int viewCenterX;
		@Shadow public volatile int viewCenterZ;
		@Unique private volatile int viewCenterW;

		@Override
		public int viewCenterW() {
			return this.viewCenterW;
		}
		@Override
		public void setViewCenterW(int viewCenterW) {
			this.viewCenterW = viewCenterW;
		}

		@Definition(id = "viewRange", field = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;viewRange:I")
		@Expression("this.viewRange * this.viewRange")
		@ModifyExpressionValue(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
		int size(int size2D) {
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

		@Overwrite
		@Deprecated
		public void onSectionEmptinessChanged(int sectionX, int sectionY, int sectionZ, boolean empty) {
			throw Err4.arguments3("ClientChunkCache4.Storage#onSectionEmptinessChanged");
		}
		@Override
		public void onSectionEmptinessChanged(int sectionX, int sectionY, int sectionZ, int sectionW, boolean empty) {
			if (this.inRange(sectionX, sectionZ, sectionW)) {
				long sectionNode = SectionPos4.asLong(sectionX, sectionY, sectionZ, sectionW);
				if (empty) {
					this.loadedEmptySections.add(sectionNode);
				} else if (this.loadedEmptySections.remove(sectionNode)) {
					this$0.level.onSectionBecomingNonEmpty(sectionNode);
				}
			}
		}

		@Redirect(method = "dropEmptySections", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
		))
		long dropEmptySections(int x, int y, int z, @Local(name = "chunkPos") ChunkPos chunkPos) {
			return SectionPos4.asLong(x, y, z, ChunkPos4.as(chunkPos).w());
		}

		@Redirect(method = "addEmptySections", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
		))
		long addEmptySections(int x, int y, int z, @Local(name = "chunkPos") ChunkPos chunkPos) {
			return SectionPos4.asLong(x, y, z, ChunkPos4.as(chunkPos).w());
		}

		@Redirect(method = "refreshEmptySections", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
		))
		long refreshEmptySections(int x, int y, int z, @Local(name = "chunkPos") ChunkPos chunkPos) {
			return SectionPos4.asLong(x, y, z, ChunkPos4.as(chunkPos).w());
		}

		@Overwrite
		private boolean inRange(int chunkX, int chunkZ) {
			throw Err4.arguments2("ClientChunkCache4.Storage#inRange");
		}
		@Override
		public boolean inRange(int chunkX, int chunkZ, int chunkW) {
			return Math.abs(chunkX - this.viewCenterX  ) <= this.chunkRadius
				&& Math.abs(chunkZ - this.viewCenterZ  ) <= this.chunkRadius
				&& Math.abs(chunkW - this.viewCenterW()) <= this.chunkRadius;
		}

		// TODO dumpChunks, seems to be a debug method? not used anywhere
	}
}
