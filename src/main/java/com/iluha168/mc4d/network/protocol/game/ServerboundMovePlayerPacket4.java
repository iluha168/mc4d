package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ServerboundMovePlayerPacket}.
 */
public interface ServerboundMovePlayerPacket4 {
	double getW(double fallback);
	@ApiStatus.Internal
	void setW(double w);

	float getWRot(float fallback);
	@ApiStatus.Internal
	void setWRot(float wRot);

	float getVRot(float fallback);
	@ApiStatus.Internal
	void setVRot(float vRot);
}
