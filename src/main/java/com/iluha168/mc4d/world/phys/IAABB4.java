package com.iluha168.mc4d.world.phys;

import org.jetbrains.annotations.Contract;

/**
 * <b>All {@link net.minecraft.world.phys.AABB}</b> instances implement {@link IAABB4}, where {@link IAABB4#minW()} and {@link IAABB4#maxW()} are just constants.
 * {@link AABB4} also implements this, but does not have placeholder W values.
 */
public interface IAABB4 {
	@Contract(pure = true)
	double minW();
	@Contract(pure = true)
	double maxW();
}
