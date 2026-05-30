package com.iluha168.mc4d.world.phys.shapes;

import com.iluha168.mc4d.core.Direction4;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.IndexMerger;
import org.jspecify.annotations.NonNull;

import java.util.BitSet;

public class BitSetDiscreteVoxelShape4 extends DiscreteVoxelShape4 {
	private final BitSet storage;
	private int xMin;
	private int yMin;
	private int zMin;
	private int wMin;
	private int xMax;
	private int yMax;
	private int zMax;
	private int wMax;

	public BitSetDiscreteVoxelShape4(int xSize, int ySize, int zSize, int wSize) {
		super(xSize, ySize, zSize, wSize);
		this.storage = new BitSet(xSize * ySize * zSize * wSize);
		this.xMin = xSize;
		this.yMin = ySize;
		this.zMin = zSize;
		this.wMin = wSize;
	}

	public static BitSetDiscreteVoxelShape4 withFilledBounds(
		int xSize, int ySize, int zSize, int wSize,
		int xMin, int yMin, int zMin, int wMin,
		int xMax, int yMax, int zMax, int wMax
	) {
		BitSetDiscreteVoxelShape4 shape = new BitSetDiscreteVoxelShape4(xSize, ySize, zSize, wSize);
		shape.xMin = xMin;
		shape.yMin = yMin;
		shape.zMin = zMin;
		shape.wMin = wMin;
		shape.xMax = xMax;
		shape.yMax = yMax;
		shape.zMax = zMax;
		shape.wMax = wMax;

		for (int x = xMin; x < xMax; x++)
			for (int y = yMin; y < yMax; y++)
				for (int z = zMin; z < zMax; z++)
					for (int w = wMin; w < wMax; w++)
						shape.fillUpdateBounds(x, y, z, w, false);

		return shape;
	}

	public BitSetDiscreteVoxelShape4(DiscreteVoxelShape4 voxelShape) {
		super(voxelShape.xSize, voxelShape.ySize, voxelShape.zSize, voxelShape.wSize);
		if (voxelShape instanceof BitSetDiscreteVoxelShape4 bitSetVoxelShape) {
			this.storage = (BitSet) bitSetVoxelShape.storage.clone();
		} else {
			this.storage = new BitSet(this.xSize * this.ySize * this.zSize  * this.wSize);

			for (int x = 0; x < this.xSize; x++)
				for (int y = 0; y < this.ySize; y++)
					for (int z = 0; z < this.zSize; z++)
						for (int w = 0; w < this.wSize; w++)
							if (voxelShape.isFull(x, y, z, w))
								this.storage.set(this.getIndex(x, y, z, w));
		}

		this.xMin = voxelShape.firstFull(Direction.Axis.X);
		this.yMin = voxelShape.firstFull(Direction.Axis.Y);
		this.zMin = voxelShape.firstFull(Direction.Axis.Z);
		this.xMax = voxelShape.lastFull(Direction.Axis.X);
		this.yMax = voxelShape.lastFull(Direction.Axis.Y);
		this.zMax = voxelShape.lastFull(Direction.Axis.Z);
	}

	protected int getIndex(int x, int y, int z, int w) {
		return ((x * this.ySize + y) * this.zSize + z) * this.wSize + w;
	}

	@Override
	public boolean isFull(int x, int y, int z, int w) {
		return this.storage.get(this.getIndex(x, y, z, w));
	}

	private void fillUpdateBounds(int x, int y, int z, int w, boolean updateBounds) {
		this.storage.set(this.getIndex(x, y, z, w));
		if (updateBounds) {
			this.xMin = Math.min(this.xMin, x);
			this.yMin = Math.min(this.yMin, y);
			this.zMin = Math.min(this.zMin, z);
			this.wMin = Math.min(this.wMin, w);
			this.xMax = Math.max(this.xMax, x + 1);
			this.yMax = Math.max(this.yMax, y + 1);
			this.zMax = Math.max(this.zMax, z + 1);
			this.wMax = Math.max(this.wMax, w + 1);
		}
	}

	@Override
	public void fill(int x, int y, int z, int w) {
		this.fillUpdateBounds(x, y, z, w, true);
	}

	@Override
	public boolean isEmpty() {
		return this.storage.isEmpty();
	}

	@Override
	public int firstFull(Direction.@NonNull Axis axis) {
		return Direction4.Axis.as(axis).choose(this.xMin, this.yMin, this.zMin, this.wMin);
	}

	@Override
	public int lastFull(Direction.@NonNull Axis axis) {
		return Direction4.Axis.as(axis).choose(this.xMax, this.yMax, this.zMax, this.wMax);
	}

	public static BitSetDiscreteVoxelShape4 join(
		DiscreteVoxelShape4 first,
		DiscreteVoxelShape4 second,
		IndexMerger xMerger,
		IndexMerger yMerger,
		IndexMerger zMerger,
		IndexMerger wMerger,
		BooleanOp op
	) {
		BitSetDiscreteVoxelShape4 shape = new BitSetDiscreteVoxelShape4(
			xMerger.size() - 1,
			yMerger.size() - 1,
			zMerger.size() - 1,
			wMerger.size() - 1
		);
		int[] bounds = new int[]{
			Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, // 0 - 3
			Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, // 4 - 7
		};
		xMerger.forMergedIndexes((x1, x2, xr) -> {
			boolean[] updatedHyperSlice = new boolean[]{false};
			yMerger.forMergedIndexes((y1, y2, yr) -> {
				boolean[] updatedSlice = new boolean[]{false};
				zMerger.forMergedIndexes((z1, z2, zr) -> {
					boolean[] updatedColumn = new boolean[]{false};
					wMerger.forMergedIndexes((w1, w2, wr) -> {
						if (op.apply(first.isFullWide(x1, y1, z1, w1), second.isFullWide(x2, y2, z2, w2))) {
							shape.storage.set(shape.getIndex(xr, yr, zr, wr));
							bounds[3] = Math.min(bounds[3], wr);
							bounds[7] = Math.max(bounds[7], wr);
							updatedColumn[0] = true;
						}
						return true;
					});
					if (updatedColumn[0]) {
						bounds[2] = Math.min(bounds[2], zr);
						bounds[6] = Math.max(bounds[6], zr);
						updatedSlice[0] = true;
					}
					return true;
				});
				if (updatedSlice[0]) {
					bounds[1] = Math.min(bounds[1], yr);
					bounds[5] = Math.max(bounds[5], yr);
					updatedHyperSlice[0] = true;
				}
				return true;
			});
			if (updatedHyperSlice[0]) {
				bounds[0] = Math.min(bounds[0], xr);
				bounds[4] = Math.max(bounds[4], xr);
			}
			return true;
		});
		shape.xMin = bounds[0];
		shape.yMin = bounds[1];
		shape.zMin = bounds[2];
		shape.wMin = bounds[3];
		shape.xMax = bounds[4] + 1;
		shape.yMax = bounds[5] + 1;
		shape.zMax = bounds[6] + 1;
		shape.wMax = bounds[7] + 1;
		return shape;
	}

	protected static void forAllBoxes(
		DiscreteVoxelShape4 voxelShape,
		DiscreteVoxelShape4.IntLineConsumer consumer,
		boolean mergeNeighbors
	) {
		BitSetDiscreteVoxelShape4 shape = new BitSetDiscreteVoxelShape4(voxelShape);

		for (int y = 0; y < shape.ySize; y++)
			for (int x = 0; x < shape.xSize; x++)
				for (int z = 0; z < shape.zSize; z++) {
					int lastStartW = -1;

					for (int w = 0; w <= shape.wSize; w++) {
						if (shape.isFullWide(x, y, z, w)) {
							if (mergeNeighbors) {
								if (lastStartW == -1) {
									lastStartW = w;
								}
							} else {
								consumer.consume(x, y, z, w, x + 1, y + 1, z + 1, w + 1);
							}
						} else if (lastStartW != -1) {
							int endX = x;
							int endY = y;
							int endZ = z;
							shape.clearWStrip(lastStartW, w, x, y, z);

							while (shape.isWStripFull(lastStartW, w, endX + 1, y, z)) {
								shape.clearWStrip(lastStartW, w, endX + 1, y, z);
								endX++;
							}

							while (shape.isXWRectangleFull(x, endX + 1, lastStartW, w, endY + 1, z)) {
								for (int cx = x; cx <= endX; cx++)
									shape.clearWStrip(lastStartW, w, cx, endY + 1, z);

								endY++;
							}

							while (shape.isXYWCuboidFull(x, endX + 1, y, endY + 1, lastStartW, w, endZ + 1)) {
								for (int cx = x; cx <= endX; cx++)
									for (int cy = y; cy <= endY; cy++)
										shape.clearWStrip(lastStartW, w, cx, cy, endZ + 1);

								endZ++;
							}

							consumer.consume(x, y, z, lastStartW, endX + 1, endY + 1, endZ + 1, w);
							lastStartW = -1;
						}
					}
				}
	}

	private boolean isWStripFull(int startW, int endW, int x, int y, int z) {
		return x < this.xSize && y < this.ySize && z < this.zSize &&
			this.storage.nextClearBit(this.getIndex(x, y, z, startW)) >= this.getIndex(x, y, z, endW);
	}

	private boolean isXWRectangleFull(int startX, int endX, int startW, int endW, int y, int z) {
		for (int x = startX; x < endX; x++)
			if (!this.isWStripFull(startW, endW, x, y, z))
				return false;
		return true;
	}

	private boolean isXYWCuboidFull(int startX, int endX, int startY, int endY, int startW, int endW, int z) {
		for (int x = startX; x < endX; x++)
			for (int y = startY; y < endY; y++)
				if (!this.isWStripFull(startW, endW, x, y, z))
					return false;
		return true;
	}

	private void clearWStrip(int startW, int endW, int x, int y, int z) {
		this.storage.clear(this.getIndex(x, y, z, startW), this.getIndex(x, y, z, endW));
	}

	public boolean isInterior(int x, int y, int z, int w) {
		boolean isInterior = x > 0 && x < this.xSize - 1
			&& y > 0 && y < this.ySize - 1
			&& z > 0 && z < this.zSize - 1
			&& w > 0 && w < this.wSize - 1;
		return isInterior
			&& this.isFull(x, y, z, w)
			&& this.isFull(x - 1, y, z, w)
			&& this.isFull(x + 1, y, z, w)
			&& this.isFull(x, y - 1, z, w)
			&& this.isFull(x, y + 1, z, w)
			&& this.isFull(x, y, z - 1, w)
			&& this.isFull(x, y, z + 1, w)
			&& this.isFull(x, y, z, w - 1)
			&& this.isFull(x, y, z, w + 1);
	}
}
