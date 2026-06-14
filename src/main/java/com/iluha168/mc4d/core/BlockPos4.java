package com.iluha168.mc4d.core;

import com.google.common.collect.AbstractIterator;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <b>All {@link net.minecraft.core.BlockPos}</b> instances implement {@link BlockPos4}.
 */
public interface BlockPos4 {
	long PACKED_W_MASK = (1L << BlockPos.PACKED_HORIZONTAL_LENGTH) - 1L;
	int W_OFFSET = BlockPos.PACKED_Y_LENGTH + 2 * BlockPos.PACKED_HORIZONTAL_LENGTH;

	static BlockPos from(int x, int y, int z, int w) {
		BlockPos pos = new BlockPos(x, y, z);
		Vec4i.setW(pos, w);
		return pos;
	}

	static long offset(long blockNode, int stepX, int stepY, int stepZ, int stepW) {
		return BlockPos4.asLong(
			BlockPos.getX(blockNode) + stepX,
			BlockPos.getY(blockNode) + stepY,
			BlockPos.getZ(blockNode) + stepZ,
			BlockPos4.getW(blockNode) + stepW
		);
	}

	static int getW(long blockNode) {
		return (int)(blockNode << 64 - W_OFFSET - BlockPos.PACKED_HORIZONTAL_LENGTH >> 64 - BlockPos.PACKED_HORIZONTAL_LENGTH);
	}

	static BlockPos containing(double x, double y, double z, double w) {
		return BlockPos4.from(Mth.floor(x), Mth.floor(y), Mth.floor(z), Mth.floor(w));
	}

	static long asLong(int x, int y, int z, int w) {
		long node = 0L;
		node |= (x & BlockPos .PACKED_X_MASK) << BlockPos .X_OFFSET;
		node |= (y & BlockPos .PACKED_Y_MASK) << BlockPos .Y_OFFSET;
		node |= (z & BlockPos .PACKED_Z_MASK) << BlockPos .Z_OFFSET;
		node |= (w & BlockPos4.PACKED_W_MASK) << BlockPos4.W_OFFSET;
		return node;
	}

	default BlockPos offset(int x, int y, int z, int w) {
		BlockPos This = (BlockPos) this;
		return x == 0 && y == 0 && z == 0 && w == 0
			? This
			: BlockPos4.from(This.getX() + x, This.getY() + y, This.getZ() + z, Vec4i.getW(This) + w);
	}

	BlockPos ana();
	BlockPos ana(int steps);
	BlockPos kata();
	BlockPos kata(int steps);

	static Iterable<BlockPos> randomBetweenClosed(
		RandomSource random, int limit,
		int minX, int minY, int minZ, int minW,
		int maxX, int maxY, int maxZ, int maxW
	) {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		int depth = maxZ - minZ + 1;
		int trength = maxW - minW + 1;
		return () -> new AbstractIterator<>() {
			final BlockPos4.MutableBlockPos nextPos = (BlockPos4.MutableBlockPos) new BlockPos.MutableBlockPos();
			int counter = limit;

			protected BlockPos computeNext() {
				if (this.counter <= 0) {
					return this.endOfData();
				}
				final BlockPos next = this.nextPos.set(
					minX + random.nextInt(width),
					minY + random.nextInt(height),
					minZ + random.nextInt(depth),
					minW + random.nextInt(trength)
				);
				this.counter--;
				return next;
			}
		};
	}

	static Iterable<BlockPos> withinManhattan(BlockPos origin, int reachX, int reachY, int reachZ, int reachW) {
		int maxDepth = reachX + reachY + reachZ + reachW;
		int originX = origin.getX();
		int originY = origin.getY();
		int originZ = origin.getZ();
		int originW = Vec4i.getW(origin);
		return () -> new AbstractIterator<>() {
			private final BlockPos4.MutableBlockPos cursor = (BlockPos4.MutableBlockPos) new BlockPos.MutableBlockPos();
			private int currentDepth;
			private int maxX;
			private int maxY;
			private int maxW;
			private int x;
			private int y;
			private int w;
			private boolean zMirror;

			protected BlockPos computeNext() {
				if (this.zMirror) {
					this.zMirror = false;
					BlockPos.MutableBlockPos cursor3 = (BlockPos.MutableBlockPos) this.cursor;
					cursor3.setZ(originZ - (cursor3.getZ() - originZ));
					return cursor3;
				}
				BlockPos found;
				for (found = null; found == null; this.w++) {
					if (this.w > this.maxW) {
						this.y++;
						if (this.y > this.maxY) {
							this.x++;
							if (this.x > this.maxX) {
								this.currentDepth++;
								if (this.currentDepth > maxDepth) {
									return this.endOfData();
								}

								this.maxX = Math.min(reachX, this.currentDepth);
								this.x = -this.maxX;
							}

							this.maxY = Math.min(reachY, this.currentDepth - Math.abs(this.x));
							this.y = -this.maxY;
						}

						this.maxW = Math.min(reachW, this.currentDepth - Math.abs(this.x) - Math.abs(this.y));
						this.w = -this.maxW;
					}
					int xx = this.x;
					int yy = this.y;
					int ww = this.w;
					int zz = this.currentDepth - Math.abs(xx) - Math.abs(yy) - Math.abs(ww);
					if (zz <= reachZ) {
						this.zMirror = zz != 0;
						found = this.cursor.set(originX + xx, originY + yy, originZ + zz, originW + ww);
					}
				}

				return found;
			}
		};
	}

	static Stream<BlockPos> withinManhattanStream(BlockPos origin, int reachX, int reachY, int reachZ, int reachW) {
		return StreamSupport.stream(BlockPos4.withinManhattan(origin, reachX, reachY, reachZ, reachW).spliterator(), false);
	}

	static Stream<BlockPos> betweenClosedStream(
		int minX, int minY, int minZ, int minW,
		int maxX, int maxY, int maxZ, int maxW
	) {
		return StreamSupport.stream(BlockPos4.betweenClosed(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW).spliterator(), false);
	}

	static Iterable<BlockPos> betweenClosed(
		int minX, int minY, int minZ, int minW,
		int maxX, int maxY, int maxZ, int maxW
	) {
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		int depth = maxZ - minZ + 1;
		int trength = maxW - minW + 1;
		int end = width * height * depth * trength;
		return () -> new AbstractIterator<>() {
			private final BlockPos4.MutableBlockPos cursor = (BlockPos4.MutableBlockPos) new BlockPos.MutableBlockPos();
			private int index;

			protected BlockPos computeNext() {
				if (this.index == end) {
					return this.endOfData();
				}
				final int x = this.index % width;
				int slice = this.index / width;
				final int y = slice % height;
				slice = slice / height;
				final int z = slice % depth;
				final int w = slice / depth;
				this.index++;
				return this.cursor.set(minX + x, minY + y, minZ + z, minW + w);
			}
		};
	}

	static Iterable<BlockPos.MutableBlockPos> spiralAround(BlockPos center, int radius, Direction firstDirection, Direction secondDirection, Direction thirdDirection) {
		Validate.validState(
			firstDirection.getAxis() != secondDirection.getAxis() &&
			firstDirection.getAxis() != thirdDirection.getAxis() &&
			secondDirection.getAxis() != thirdDirection.getAxis(),
			"The 3 directions cannot be on the same axis"
		);
		return () -> new AbstractIterator<>() {
			private final Direction[] directions = new Direction[]{
				firstDirection, secondDirection, thirdDirection,
				firstDirection.getOpposite(), secondDirection.getOpposite(), thirdDirection.getOpposite()
			};
			private final BlockPos.MutableBlockPos cursor = center.mutable().move(secondDirection).move(thirdDirection);
			private final int legs = directions.length * radius;
			private int leg = -1;
			private int legSize;
			private int legIndex;
			private int lastX = this.cursor.getX();
			private int lastY = this.cursor.getY();
			private int lastZ = this.cursor.getZ();
			private int lastW = Vec4i.getW(this.cursor);

			protected BlockPos.MutableBlockPos computeNext() {
				((BlockPos4.MutableBlockPos) this.cursor)
					.set(this.lastX, this.lastY, this.lastZ, this.lastW)
					.move(this.directions[(this.leg + directions.length) % directions.length]);
				this.lastX = this.cursor.getX();
				this.lastY = this.cursor.getY();
				this.lastZ = this.cursor.getZ();
				this.lastW = Vec4i.getW(this.cursor);
				if (this.legIndex >= this.legSize) {
					if (this.leg >= this.legs) {
						return this.endOfData();
					}

					this.leg++;
					this.legIndex = 0;
					this.legSize = this.leg / 2 + 1;
				}

				this.legIndex++;
				return this.cursor;
			}
		};
	}

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

				((BlockPos4.MutableBlockPos) this.cursor).set(
					startCornerX + this.dirX0 * this.index0 + this.dirX1 * this.index1 + this.dirX2 * this.index2 + this.dirX3 * this.index3,
					startCornerY + this.dirY0 * this.index0 + this.dirY1 * this.index1 + this.dirY2 * this.index2 + this.dirY3 * this.index3,
					startCornerZ + this.dirZ0 * this.index0 + this.dirZ1 * this.index1 + this.dirZ2 * this.index2 + this.dirZ3 * this.index3,
					startCornerW + this.dirW0 * this.index0 + this.dirW1 * this.index1 + this.dirW2 * this.index2 + this.dirW3 * this.index3
				);

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

	interface MutableBlockPos {
		static BlockPos.MutableBlockPos from(int x, int y, int z, int w) {
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
			Vec4i.setW(pos, w);
			return pos;
		}

		default BlockPos.MutableBlockPos set(int x, int y, int z, int w) {
			BlockPos.MutableBlockPos This = (BlockPos.MutableBlockPos) this;
			This.setX(x);
			This.setY(y);
			This.setZ(z);
			Vec4i.setW(This, w);
			return This;
		}

		default BlockPos.MutableBlockPos set(double x, double y, double z, double w) {
			return this.set(Mth.floor(x), Mth.floor(y), Mth.floor(z), Mth.floor(w));
		}

		default BlockPos.MutableBlockPos set(AxisCycle transform, int x, int y, int z, int w) {
			AxisCycle4 transform4 = AxisCycle4.as(transform);
			return this.set(
				transform4.cycle(x, y, z, w, Direction.Axis.X),
				transform4.cycle(x, y, z, w, Direction.Axis.Y),
				transform4.cycle(x, y, z, w, Direction.Axis.Z),
				transform4.cycle(x, y, z, w, Direction4.Axis.W)
			);
		}

		default BlockPos.MutableBlockPos setWithOffset(Vec3i pos, int x, int y, int z, int w) {
			return this.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z, Vec4i.getW(pos) + w);
		}

		default BlockPos.MutableBlockPos move(int x, int y, int z, int w) {
			BlockPos.MutableBlockPos This = (BlockPos.MutableBlockPos) this;
			return this.set(This.getX() + x, This.getY() + y, This.getZ() + z, Vec4i.getW(This) + w);
		}

		default BlockPos.MutableBlockPos setW(int w) {
			BlockPos.MutableBlockPos This = (BlockPos.MutableBlockPos) this;
			Vec4i.setW(This, w);
			return This;
		}
	}
}
