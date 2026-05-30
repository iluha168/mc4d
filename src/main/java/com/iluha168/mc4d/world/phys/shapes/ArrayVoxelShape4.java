package com.iluha168.mc4d.world.phys.shapes;

import com.iluha168.mc4d.core.Direction4;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.phys.shapes.ArrayVoxelShape;
import org.jspecify.annotations.NonNull;

public class ArrayVoxelShape4 extends ArrayVoxelShape {
	private final DoubleList ws;

	public ArrayVoxelShape4(DiscreteVoxelShape4 shape, DoubleList xs, DoubleList ys, DoubleList zs, DoubleList ws) {
		super(shape, xs, ys, zs);
		int wSize = shape.getWSize() + 1;
		if (wSize == ws.size()) {
			this.ws = ws;
		} else {
			throw Util.pauseInIde(
				new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.")
			);
		}
	}

	@Override
	public @NonNull DoubleList getCoords(Direction.@NonNull Axis axis) {
		if (axis == Direction4.Axis.W) return this.ws;
		return super.getCoords(axis);
	}
}
