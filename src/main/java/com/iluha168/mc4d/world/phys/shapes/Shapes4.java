package com.iluha168.mc4d.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.world.phys.shapes.*;

public final class Shapes4 {
	public static VoxelShape box(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW
	) {
		if (minX > maxX || minY > maxY || minZ > maxZ || minW > maxW) {
			throw new IllegalArgumentException("The min values need to be smaller or equals to the max values");
		} else {
			return create(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
		}
	}

	public static VoxelShape create(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW
	) {
		if (maxX - minX < Shapes.EPSILON
			|| maxY - minY < Shapes.EPSILON
			|| maxZ - minZ < Shapes.EPSILON
			|| maxW - minW < Shapes.EPSILON
		) {
			return Shapes.empty();
		}
		int xBits = Shapes.findBits(minX, maxX);
		int yBits = Shapes.findBits(minY, maxY);
		int zBits = Shapes.findBits(minZ, maxZ);
		int wBits = Shapes.findBits(minW, maxW);
		if (xBits < 0 || yBits < 0 || zBits < 0 || wBits < 0) {
			return new ArrayVoxelShape4(
				(DiscreteVoxelShape4) Shapes.block().shape,
				DoubleArrayList.wrap(new double[]{minX, maxX}),
				DoubleArrayList.wrap(new double[]{minY, maxY}),
				DoubleArrayList.wrap(new double[]{minZ, maxZ}),
				DoubleArrayList.wrap(new double[]{minW, maxW})
			);
		} else if (xBits == 0 && yBits == 0 && zBits == 0 && wBits == 0) {
			return Shapes.block();
		} else {
			int xSize = 1 << xBits;
			int ySize = 1 << yBits;
			int zSize = 1 << zBits;
			int wSize = 1 << wBits;
			BitSetDiscreteVoxelShape4 voxelShape = BitSetDiscreteVoxelShape4.withFilledBounds(
				xSize,
				ySize,
				zSize,
				wSize,
				(int) Math.round(minX * xSize),
				(int) Math.round(minY * ySize),
				(int) Math.round(minZ * zSize),
				(int) Math.round(minW * wSize),
				(int) Math.round(maxX * xSize),
				(int) Math.round(maxY * ySize),
				(int) Math.round(maxZ * zSize),
				(int) Math.round(maxW * wSize)
			);
			return new CubeVoxelShape(voxelShape);
		}
	}

	public interface DoubleLineConsumer {
		void consume(
			double x1, double y1, double z1, double w1,
			double x2, double y2, double z2, double w2
		);
	}
}
