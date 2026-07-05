package com.iluha168.mc4d.world.phys.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * <b>All {@link net.minecraft.world.phys.shapes.VoxelShape}</b> instances implement {@link VoxelShape4}.
 */
public interface VoxelShape4 {
	void forAllEdges(Shapes4.DoubleLineConsumer consumer);
	void forAllBoxes(Shapes4.DoubleLineConsumer consumer);

	double min(Direction.Axis aAxis, double b, double c, double d);
	double max(Direction.Axis aAxis, double b, double c, double d);

	VoxelShape move(double dx, double dy, double dz, double dw);

	/** The 3D slice of this shape at {@code w}. Custom method, not a vanilla fix. */
	VoxelShape sliceW(double w);
}
