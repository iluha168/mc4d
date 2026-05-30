package com.iluha168.mc4d.world.phys.shapes;

import com.iluha168.mc4d.core.Direction4;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public final class SubShape4 extends DiscreteVoxelShape4 {
	private final DiscreteVoxelShape4 parent;
	private final int startX;
	private final int startY;
	private final int startZ;
	private final int startW;
	private final int endX;
	private final int endY;
	private final int endZ;
	private final int endW;

	public SubShape4(
		DiscreteVoxelShape4 parent,
		int startX, int startY, int startZ, int startW,
		int endX, int endY, int endZ, int endW
	) {
		super(endX - startX, endY - startY, endZ - startZ, endW - startW);
		this.parent = parent;
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.startW = startW;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
		this.endW = endW;
	}

	@Override
	public boolean isFull(int x, int y, int z, int w) {
		return this.parent.isFull(this.startX + x, this.startY + y, this.startZ + z, this.startW + w);
	}

	@Override
	public void fill(int x, int y, int z, int w) {
		this.parent.fill(this.startX + x, this.startY + y, this.startZ + z, this.startW + w);
	}

	@Override
	public int firstFull(Direction.@NonNull Axis axis) {
		return this.clampToShape(Direction4.Axis.as(axis), this.parent.firstFull(axis));
	}

	@Override
	public int lastFull(Direction.@NonNull Axis axis) {
		return this.clampToShape(Direction4.Axis.as(axis), this.parent.lastFull(axis));
	}

	private int clampToShape(Direction4.Axis axis, int parentResult) {
		int start = axis.choose(this.startX, this.startY, this.startZ, this.startW);
		int end = axis.choose(this.endX, this.endY, this.endZ, this.endW);
		return Mth.clamp(parentResult, start, end) - start;
	}
}
