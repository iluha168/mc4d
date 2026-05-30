package com.iluha168.mc4d.core;

import org.jetbrains.annotations.Contract;

/**
 * <b>All {@link net.minecraft.world.phys.Vec3}</b> instances implement {@link Position4}, where {@link Position4#w()} is just a constant.
 * {@link com.iluha168.mc4d.world.phys.Vec4} also implements this, but has a real {@link Position4#w()} value.
 */
public interface Position4 {
	double x();
	double y();
	double z();
	@Contract(pure = true)
	double w();
}
