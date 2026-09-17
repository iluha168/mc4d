package com.iluha168.mc4d.api.net.minecraft.world.entity.player;

import net.minecraft.world.entity.player.Input;

/**
 * Implemented by {@link Input}.
 */
public interface Input4 {
	static Input4 as(Input input) {
		return (Input4) (Object) input;
	}

	boolean ana();
	boolean kata();

	void setAna(boolean ana);
	void setKata(boolean kata);
}
