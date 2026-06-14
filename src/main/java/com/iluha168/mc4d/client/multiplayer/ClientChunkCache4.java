package com.iluha168.mc4d.client.multiplayer;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public interface ClientChunkCache4 {
	void replaceBiomes(int chunkX, int chunkZ, int chunkW, FriendlyByteBuf readBuffer);
	@Nullable LevelChunk replaceWithPacketData(
		int chunkX, int chunkZ, int chunkW,
		FriendlyByteBuf readBuffer,
		Map<Heightmap.Types, long[]> heightmaps,
		Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> blockEntities
	);
	void updateViewCenter(int x, int z, int w);

	interface Storage {
		static ClientChunkCache4.Storage as(ClientChunkCache.Storage storage) {
			return (ClientChunkCache4.Storage) (Object) storage;
		}

		int viewCenterW();
		void setViewCenterW(int viewCenterW);

		int getIndex(int chunkX, int chunkZ, int chunkW);
		void onSectionEmptinessChanged(int sectionX, int sectionY, int sectionZ, int sectionW, boolean empty);
		boolean inRange(int chunkX, int chunkZ, int chunkW);
	}
}
