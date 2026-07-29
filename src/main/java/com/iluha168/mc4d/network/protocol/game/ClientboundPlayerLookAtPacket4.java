package com.iluha168.mc4d.network.protocol.game;

import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link ClientboundPlayerLookAtPacket}.
 */
public interface ClientboundPlayerLookAtPacket4 {
	@ApiStatus.Internal
	void setW(double w);
	double getW();
}
