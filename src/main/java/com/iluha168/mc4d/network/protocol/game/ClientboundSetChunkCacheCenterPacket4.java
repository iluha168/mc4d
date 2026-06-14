package com.iluha168.mc4d.network.protocol.game;

import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket}.
 */
public interface ClientboundSetChunkCacheCenterPacket4 {
	int getW();

	@ApiStatus.Internal
	void setW(int w);
}
