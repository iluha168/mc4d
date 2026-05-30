package com.iluha168.mc4d.world.phys.shapes;

import com.iluha168.mc4d.core.AxisCycle4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.math.OctahedralGroup4;
import com.mojang.math.OctahedralGroup;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.joml.Vector4i;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Represents a hypervoxel shape.
 */
public abstract class DiscreteVoxelShape4 extends DiscreteVoxelShape {
	/** Always wrap with finally to set back to false! */
	public static boolean UNSAFE_DISABLE_3D_ERRORS = false;

	private static final Direction.Axis[] AXIS_VALUES = Direction.Axis.values();
	public final int wSize;

	protected DiscreteVoxelShape4(int xSize, int ySize, int zSize, int wSize) {
		if (wSize < 0) throw new IllegalArgumentException("Need all positive sizes: w: " + wSize);
		super(xSize, ySize, zSize);
		this.wSize = wSize;
	}

	@Override
	public @NonNull DiscreteVoxelShape rotate(@NonNull OctahedralGroup rotation) {
		if (rotation == OctahedralGroup.IDENTITY) {
			return this;
		}

		Vector4i v = OctahedralGroup4.as(rotation).rotate(new Vector4i(this.xSize, this.ySize, this.zSize, this.wSize));
		int shiftX = fixupCoordinate(v, 0);
		int shiftY = fixupCoordinate(v, 1);
		int shiftZ = fixupCoordinate(v, 2);
		int shiftW = fixupCoordinate(v, 3);
		DiscreteVoxelShape4 newShape = new BitSetDiscreteVoxelShape4(v.x, v.y, v.z, v.w);

		for (int x = 0; x < this.xSize; x++)
			for (int y = 0; y < this.ySize; y++)
				for (int z = 0; z < this.zSize; z++)
					for (int w = 0; w < this.wSize; w++) {
						if (this.isFull(x, y, z, w)) {
							Vector4i newPos = OctahedralGroup4.as(rotation).rotate(v.set(x, y, z, w));
							newShape.fill(
								shiftX + newPos.x,
								shiftY + newPos.y,
								shiftZ + newPos.z,
								shiftW + newPos.w
							);
						}
					}

		return newShape;
	}

	private static int fixupCoordinate(Vector4i v, int index) {
		int value = v.get(index);
		if (value < 0) {
			v.setComponent(index, -value);
			return -value - 1;
		} else {
			return 0;
		}
	}

	@Override
	@Deprecated
	public boolean isFullWide(@Nullable AxisCycle transform, int x, int y, int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#isFullWide instead"));
	}
	public boolean isFullWide(AxisCycle transform, int x, int y, int z, int w) {
		AxisCycle4 transform4 = AxisCycle4.as(transform);
		return this.isFullWide(
			transform4.cycle(x, y, z, w, Direction.Axis.X),
			transform4.cycle(x, y, z, w, Direction.Axis.Y),
			transform4.cycle(x, y, z, w, Direction.Axis.Z),
			transform4.cycle(x, y, z, w, Direction4.Axis.W)
		);
	}

	@Override
	@Deprecated
	public boolean isFullWide(int x, int y, int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#isFullWide instead"));
	}
	public boolean isFullWide(int x, int y, int z, int w) {
		if (x < 0 || y < 0 || z < 0 || w < 0) {
			return false;
		}
		return x < this.xSize && y < this.ySize && z < this.zSize && w < this.wSize && this.isFull(x, y, z, w);
	}

	@Override
	@Deprecated
	public boolean isFull(@Nullable AxisCycle transform, int x, int y, int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#isFull instead"));
	}
	public boolean isFull(AxisCycle transform, int x, int y, int z, int w) {
		AxisCycle4 transform4 = AxisCycle4.as(transform);
		return this.isFull(
			transform4.cycle(x, y, z, w, Direction.Axis.X),
			transform4.cycle(x, y, z, w, Direction.Axis.Y),
			transform4.cycle(x, y, z, w, Direction.Axis.Z),
			transform4.cycle(x, y, z, w, Direction4.Axis.W)
		);
	}

	@Override
	@Deprecated
	public final boolean isFull(final int x, final int y, final int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#isFull instead"));
	}
	public abstract boolean isFull(final int x, final int y, final int z, final int w);

	@Override
	@Deprecated
	public final void fill(final int x, final int y, final int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#fill instead"));
	}
	public abstract void fill(final int x, final int y, final int z, final int w);

	// `isEmpty` does not need an override.

	@Override
	public abstract int firstFull(final Direction.@NonNull Axis axis);

	@Override
	public abstract int lastFull(final Direction.@NonNull Axis axis);

	@Override
	@Deprecated
	public int firstFull(Direction.@Nullable Axis aAxis, int b, int c) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#firstFull instead."));
	}
	public int firstFull(Direction.Axis aAxis, int b, int c, int d) {
		int aSize = this.getSize(aAxis);
		if (b < 0 || c < 0 || d < 0) {
			return aSize;
		}
		Direction.Axis bAxis = AxisCycle.FORWARD.cycle(aAxis);
		Direction.Axis cAxis = AxisCycle4.TRANSPOSE.cycle(aAxis);
		Direction.Axis dAxis = AxisCycle.BACKWARD.cycle(aAxis);

		if (b < this.getSize(bAxis) && c < this.getSize(cAxis) && d < this.getSize(dAxis)) {
			AxisCycle transform = AxisCycle.between(Direction.Axis.X, aAxis);
			for (int a = 0; a < aSize; a++) {
				if (this.isFull(transform, a, b, c, d)) {
					return a;
				}
			}
		}
		return aSize;
	}

	@Override
	@Deprecated
	public int lastFull(Direction.@Nullable Axis aAxis, int b, int c) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#lastFull instead."));
	}
	public int lastFull(Direction.Axis aAxis, int b, int c, int d) {
		if (b < 0 || c < 0 || d < 0) {
			return 0;
		}
		Direction.Axis bAxis = AxisCycle.FORWARD.cycle(aAxis);
		Direction.Axis cAxis = AxisCycle4.TRANSPOSE.cycle(aAxis);
		Direction.Axis dAxis = AxisCycle.BACKWARD.cycle(aAxis);

		if (b < this.getSize(bAxis) && c < this.getSize(cAxis) && d < this.getSize(dAxis)) {
			int aSize = this.getSize(aAxis);
			AxisCycle transform = AxisCycle.between(Direction.Axis.X, aAxis);
			for (int a = aSize - 1; a >= 0; a--) {
				if (this.isFull(transform, a, b, c, d)) {
					return a + 1;
				}
			}
		}
		return 0;
	}

	@Override
	public int getSize(Direction.@NonNull Axis axis) {
		return Direction4.Axis.as(axis).choose(this.xSize, this.ySize, this.zSize, this.wSize);
	}

	// getXSize, getYSize, getZSize do not need an override.
	public int getWSize() {
		return this.getSize(Direction4.Axis.W);
	}

	@Override
	@Deprecated
	public void forAllEdges(DiscreteVoxelShape.@NonNull IntLineConsumer consumer, boolean mergeNeighbors) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#forAllEdges instead."));
	}
	public void forAllEdges(DiscreteVoxelShape4.@NonNull IntLineConsumer consumer, boolean mergeNeighbors) {
		this.forAllAxisEdges(consumer, AxisCycle.NONE, mergeNeighbors);
		this.forAllAxisEdges(consumer, AxisCycle.FORWARD, mergeNeighbors);
		this.forAllAxisEdges(consumer, AxisCycle4.TRANSPOSE, mergeNeighbors);
		this.forAllAxisEdges(consumer, AxisCycle.BACKWARD, mergeNeighbors);
	}

	private void forAllAxisEdges(DiscreteVoxelShape4.IntLineConsumer consumer, AxisCycle transform, boolean mergeNeighbors) {
		AxisCycle inverse = transform.inverse();
		AxisCycle4 inverse4 = AxisCycle4.as(inverse);
		int aSize = this.getSize(inverse.cycle(Direction.Axis.X));
		int bSize = this.getSize(inverse.cycle(Direction.Axis.Y));
		int cSize = this.getSize(inverse.cycle(Direction.Axis.Z));
		int dSize = this.getSize(inverse.cycle(Direction4.Axis.W));

		for (int a = 0; a <= aSize; a++)
			for (int b = 0; b <= bSize; b++)
				for (int c = 0; c <= cSize; c++) {
					int lastStart = -1;

					for (int d = 0; d <= dSize; d++) {
						int fullSectors = 0;
						int oddSectors = 0;

						for (int da = 0; da <= 1; da++)
							for (int db = 0; db <= 1; db++)
								for (int dc = 0; dc <= 1; dc++) {
									if (this.isFullWide(inverse, a + da - 1, b + db - 1, c + dc - 1, d)) {
										fullSectors++;
										oddSectors ^= da ^ db ^ dc;
									}
								}

						if (fullSectors == 1 || fullSectors == 3 || fullSectors == 2 && (oddSectors & 1) == 0) { // TODO? not sure how to generalize this
							if (mergeNeighbors) {
								if (lastStart == -1) {
									lastStart = d;
								}
							} else {
								consumer.consume(
									inverse4.cycle(a, b, c, d, Direction.Axis.X),
									inverse4.cycle(a, b, c, d, Direction.Axis.Y),
									inverse4.cycle(a, b, c, d, Direction.Axis.Z),
									inverse4.cycle(a, b, c, d, Direction4.Axis.W),
									inverse4.cycle(a, b, c, d + 1, Direction.Axis.X),
									inverse4.cycle(a, b, c, d + 1, Direction.Axis.Y),
									inverse4.cycle(a, b, c, d + 1, Direction.Axis.Z),
									inverse4.cycle(a, b, c, d + 1, Direction4.Axis.W)
								);
							}
						} else if (lastStart != -1) {
							consumer.consume(
								inverse4.cycle(a, b, c, lastStart, Direction.Axis.X),
								inverse4.cycle(a, b, c, lastStart, Direction.Axis.Y),
								inverse4.cycle(a, b, c, lastStart, Direction.Axis.Z),
								inverse4.cycle(a, b, c, lastStart, Direction4.Axis.W),
								inverse4.cycle(a, b, c, d, Direction.Axis.X),
								inverse4.cycle(a, b, c, d, Direction.Axis.Y),
								inverse4.cycle(a, b, c, d, Direction.Axis.Z),
								inverse4.cycle(a, b, c, d, Direction4.Axis.W)
							);
							lastStart = -1;
						}
					}
				}
	}

	@Override
	@Deprecated
	public void forAllBoxes(DiscreteVoxelShape.@NonNull IntLineConsumer consumer, boolean mergeNeighbors) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#forAllBoxes instead."));
	}
	public void forAllBoxes(DiscreteVoxelShape4.IntLineConsumer consumer, boolean mergeNeighbors) {
		BitSetDiscreteVoxelShape4.forAllBoxes(this, consumer, mergeNeighbors);
	}

	@Override
	@Deprecated
	public void forAllFaces(DiscreteVoxelShape.@Nullable IntFaceConsumer consumer) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: use DiscreteVoxelShape4#forAllFaces instead."));
	}
	public void forAllFaces(DiscreteVoxelShape4.IntFaceConsumer consumer) {
		this.forAllAxisFaces(consumer, AxisCycle.NONE);
		this.forAllAxisFaces(consumer, AxisCycle.FORWARD);
		this.forAllAxisFaces(consumer, AxisCycle4.TRANSPOSE);
		this.forAllAxisFaces(consumer, AxisCycle.BACKWARD);
	}

	private void forAllAxisFaces(DiscreteVoxelShape4.IntFaceConsumer consumer, AxisCycle transform) {
		AxisCycle inverse = transform.inverse();
		AxisCycle4 inverse4 = AxisCycle4.as(inverse);
		Direction.Axis dAxis = inverse.cycle(Direction4.Axis.W);
		int aSize = this.getSize(inverse.cycle(Direction.Axis.X));
		int bSize = this.getSize(inverse.cycle(Direction.Axis.Y));
		int cSize = this.getSize(inverse.cycle(Direction.Axis.Z));
		int dSize = this.getSize(dAxis);
		Direction negative = Direction.fromAxisAndDirection(dAxis, Direction.AxisDirection.NEGATIVE);
		Direction positive = Direction.fromAxisAndDirection(dAxis, Direction.AxisDirection.POSITIVE);

		for (int a = 0; a < aSize; a++)
			for (int b = 0; b < bSize; b++)
				for (int c = 0; c < cSize; c++) {
					boolean lastFull = false;

					for (int d = 0; d <= dSize; d++) {
						boolean full = d != dSize && this.isFull(inverse, a, b, c, d);
						if (!lastFull && full) {
							consumer.consume(
								negative,
								inverse4.cycle(a, b, c, d, Direction.Axis.X),
								inverse4.cycle(a, b, c, d, Direction.Axis.Y),
								inverse4.cycle(a, b, c, d, Direction.Axis.Z),
								inverse4.cycle(a, b, c, d, Direction4.Axis.W)
							);
						}

						if (lastFull && !full) {
							consumer.consume(
								positive,
								inverse4.cycle(a, b, c, d - 1, Direction.Axis.X),
								inverse4.cycle(a, b, c, d - 1, Direction.Axis.Y),
								inverse4.cycle(a, b, c, d - 1, Direction.Axis.Z),
								inverse4.cycle(a, b, c, d - 1, Direction4.Axis.W)
							);
						}

						lastFull = full;
					}
				}
	}

	public interface IntFaceConsumer {
		void consume(Direction direction, int x, int y, int z, int w);
	}

	public interface IntLineConsumer {
		void consume(
			int x1, int y1, int z1, int w1,
			int x2, int y2, int z2, int w2
		);
	}
}
