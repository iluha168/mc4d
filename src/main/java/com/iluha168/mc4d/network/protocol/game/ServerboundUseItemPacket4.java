package com.iluha168.mc4d.network.protocol.game;

import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link net.minecraft.network.protocol.game.ServerboundUseItemPacket}.
 */
public interface ServerboundUseItemPacket4 {
	float getWRot();
	@ApiStatus.Internal
	void setWRot(float wRot);

	float getVRot();
	@ApiStatus.Internal
	void setVRot(float vRot);
}
