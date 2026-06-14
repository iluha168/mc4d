package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;

import java.util.function.Consumer;

/**
 * Implemented by {@link ClientboundLevelChunkPacketData}.
 */
public interface ClientboundLevelChunkPacketData4 {
	Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> getBlockEntitiesTagsConsumer(int x, int z, int w);
}
