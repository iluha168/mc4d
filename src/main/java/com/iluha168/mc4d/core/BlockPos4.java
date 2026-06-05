package com.iluha168.mc4d.core;

import com.google.common.collect.AbstractIterator;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.List;

/**
 * <b>All {@link net.minecraft.core.BlockPos}</b> instances implement {@link BlockPos4}.
 */
public interface BlockPos4 {
	static Iterable<BlockPos> betweenCornersInDirection(
		int firstCornerX, int firstCornerY, int firstCornerZ, int firstCornerW,
		int secondCornerX, int secondCornerY, int secondCornerZ, int secondCornerW,
		Vec4 direction
	) {
		int minCornerX = Math.min(firstCornerX, secondCornerX);
		int minCornerY = Math.min(firstCornerY, secondCornerY);
		int minCornerZ = Math.min(firstCornerZ, secondCornerZ);
		int minCornerW = Math.min(firstCornerW, secondCornerW);

		int maxCornerX = Math.max(firstCornerX, secondCornerX);
		int maxCornerY = Math.max(firstCornerY, secondCornerY);
		int maxCornerZ = Math.max(firstCornerZ, secondCornerZ);
		int maxCornerW = Math.max(firstCornerW, secondCornerW);

		int diffX = maxCornerX - minCornerX;
		int diffY = maxCornerY - minCornerY;
		int diffZ = maxCornerZ - minCornerZ;
		int diffW = maxCornerW - minCornerW;

		int startCornerX = direction.x >= 0.0 ? minCornerX : maxCornerX;
		int startCornerY = direction.y >= 0.0 ? minCornerY : maxCornerY;
		int startCornerZ = direction.z >= 0.0 ? minCornerZ : maxCornerZ;
		int startCornerW = direction.w >= 0.0 ? minCornerW : maxCornerW;

		List<Direction.Axis> axes = Direction.axisStepOrder(direction);
		Direction.Axis visitAxis0 = axes.get(0);
		Direction.Axis visitAxis1 = axes.get(1);
		Direction.Axis visitAxis2 = axes.get(2);
		Direction.Axis visitAxis3 = axes.get(3);

		Direction visitDir0 = direction.get(visitAxis0) >= 0.0 ? visitAxis0.getPositive() : visitAxis0.getNegative();
		Direction visitDir1 = direction.get(visitAxis1) >= 0.0 ? visitAxis1.getPositive() : visitAxis1.getNegative();
		Direction visitDir2 = direction.get(visitAxis2) >= 0.0 ? visitAxis2.getPositive() : visitAxis2.getNegative();
		Direction visitDir3 = direction.get(visitAxis3) >= 0.0 ? visitAxis3.getPositive() : visitAxis3.getNegative();

		int max0 = Direction4.Axis.as(visitAxis0).choose(diffX, diffY, diffZ, diffW);
		int max1 = Direction4.Axis.as(visitAxis1).choose(diffX, diffY, diffZ, diffW);
		int max2 = Direction4.Axis.as(visitAxis2).choose(diffX, diffY, diffZ, diffW);
		int max3 = Direction4.Axis.as(visitAxis3).choose(diffX, diffY, diffZ, diffW);

		return () -> new AbstractIterator<>() {
			private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
			private int index0;
			private int index1;
			private int index2;
			private int index3;

			private boolean end;

			private final int dirX0 = visitDir0.getStepX();
			private final int dirY0 = visitDir0.getStepY();
			private final int dirZ0 = visitDir0.getStepZ();
			private final int dirW0 = Direction4.as(visitDir0).getStepW();

			private final int dirX1 = visitDir1.getStepX();
			private final int dirY1 = visitDir1.getStepY();
			private final int dirZ1 = visitDir1.getStepZ();
			private final int dirW1 = Direction4.as(visitDir1).getStepW();

			private final int dirX2 = visitDir2.getStepX();
			private final int dirY2 = visitDir2.getStepY();
			private final int dirZ2 = visitDir2.getStepZ();
			private final int dirW2 = Direction4.as(visitDir2).getStepW();

			private final int dirX3 = visitDir3.getStepX();
			private final int dirY3 = visitDir3.getStepY();
			private final int dirZ3 = visitDir3.getStepZ();
			private final int dirW3 = Direction4.as(visitDir3).getStepW();

			protected BlockPos computeNext() {
				if (this.end) {
					return this.endOfData();
				}

				this.cursor.set(
					startCornerX + this.dirX0 * this.index0 + this.dirX1 * this.index1 + this.dirX2 * this.index2 + this.dirX3 * this.index3,
					startCornerY + this.dirY0 * this.index0 + this.dirY1 * this.index1 + this.dirY2 * this.index2 + this.dirY3 * this.index3,
					startCornerZ + this.dirZ0 * this.index0 + this.dirZ1 * this.index1 + this.dirZ2 * this.index2 + this.dirZ3 * this.index3
				);
				// TODO: setW when 4D world

				if (this.index3 < max3) {
					this.index3++;
				} else if (this.index2 < max2) {
					this.index2++;
					this.index3 = 0;
				} else if (this.index1 < max1) {
					this.index1++;
					this.index3 = 0;
					this.index2 = 0;
				} else if (this.index0 < max0) {
					this.index0++;
					this.index3 = 0;
					this.index2 = 0;
					this.index1 = 0;
				} else {
					this.end = true;
				}

				return this.cursor;
			}
		};
	}
}
