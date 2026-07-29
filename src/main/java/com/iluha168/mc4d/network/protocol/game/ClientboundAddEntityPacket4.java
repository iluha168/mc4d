package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ClientboundAddEntityPacket}.
 */
public interface ClientboundAddEntityPacket4 {
	@ApiStatus.Internal
	void init_finish(double w, float wRot, float vRot, double wHeadRot);

	double getW();
	float getWRot();
	float getVRot();
	float getWHeadRot();
}
