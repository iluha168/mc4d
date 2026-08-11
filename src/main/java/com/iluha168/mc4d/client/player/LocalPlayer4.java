package com.iluha168.mc4d.client.player;

import com.iluha168.mc4d.world.entity.Entity4;
import org.jetbrains.annotations.ApiStatus;

/**
 * Implemented by {@link net.minecraft.client.player.LocalPlayer}.
 */
public interface LocalPlayer4 {
	/**
	 * A version of {@link Entity4#turn} for the client.
	 * Modifies rot values straight up, with simple addition, instead of calculating Entity's reference frame first.
	 */
	@ApiStatus.Internal
	void turn_absolute(double xo, double yo, double wo, double vo);
}
