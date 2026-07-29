package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ClientboundRotateHeadPacket}.
 */
public interface ClientboundRotateHeadPacket4 {
	@ApiStatus.Internal	void setWHeadRotPacked(byte wHeadRot);
	@ApiStatus.Internal	byte getWHeadRotPacked();
	float getWHeadRot();
}
