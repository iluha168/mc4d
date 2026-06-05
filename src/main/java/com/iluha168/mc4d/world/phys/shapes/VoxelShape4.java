package com.iluha168.mc4d.world.phys.shapes;

import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * <b>All {@link net.minecraft.world.phys.shapes.VoxelShape}</b> instances implement {@link VoxelShape4}.
 */
public interface VoxelShape4 {
	void forAllEdges(Shapes4.DoubleLineConsumer consumer);
	void forAllBoxes(Shapes4.DoubleLineConsumer consumer);

	VoxelShape move(double dx, double dy, double dz, double dw);
}
