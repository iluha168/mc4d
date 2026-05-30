package com.iluha168.mc4d.network.protocol.game;

/**
 * All {@link net.minecraft.network.protocol.game.ServerboundMovePlayerPacket}s implement this interface.
 */
public interface ServerboundMovePlayerPacket4 {
	/**
	 * {@return a real W value, not a placeholder}
	 */
	double getW(double fallback);
}
