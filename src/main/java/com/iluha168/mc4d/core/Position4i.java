package com.iluha168.mc4d.core;

import org.jetbrains.annotations.Contract;

/**
 * <b>All {@link net.minecraft.core.Vec3i}</b> instances implement {@link Position4i}, where {@link Position4i#getW()} is just a constant.
 * {@link Vec4i} also implements this, but has a real {@link Position4i#getW()} value.
 */
public interface Position4i {
	int getX();
	int getY();
	int getZ();
	@Contract(pure = true)
	int getW();
}
