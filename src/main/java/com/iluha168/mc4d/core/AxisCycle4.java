package com.iluha168.mc4d.core;

import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import org.jspecify.annotations.NonNull;

/**
 * <b>All {@link AxisCycle}</b> instances implement {@link AxisCycle4}.
 * Use {@link AxisCycle4#as} for type-casting.
 */
public interface AxisCycle4 {
	AxisCycle TRANSPOSE = AxisCycle.valueOf("TRANSPOSE");

	static AxisCycle4 as(@NonNull AxisCycle axisCycle) {
		return (AxisCycle4) (Object) axisCycle;
	}

	int cycle(int x, int y, int z, int w, Direction.Axis axis);
	double cycle(double x, double y, double z, double w, Direction.Axis axis);
}
