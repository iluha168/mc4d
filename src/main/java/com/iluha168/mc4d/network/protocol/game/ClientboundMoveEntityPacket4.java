package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ClientboundMoveEntityPacket}.
 */
public interface ClientboundMoveEntityPacket4 {
	@ApiStatus.Internal
	void setWa(short wa);
	short getWa();

	@ApiStatus.Internal	void setWRotPacked(byte wRot);
	@ApiStatus.Internal	byte getWRotPacked();
	float getWRot();

	@ApiStatus.Internal	void setVRotPacked(byte vRot);
	@ApiStatus.Internal	byte getVRotPacked();
	float getVRot();
}
