package com.iluha168.mc4d.world.level.border;

import net.minecraft.world.level.border.WorldBorder;

/**
 * All implementations of {@link net.minecraft.world.level.border.BorderChangeListener} must also implement {@link BorderChangeListener4}.
 */
public interface BorderChangeListener4 {
	void onSetCenter(WorldBorder border, double x, double z, double w);
}
