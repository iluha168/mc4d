package com.iluha168.mc4d.core;

import net.minecraft.core.Cursor3D;

public class Cursor4D extends Cursor3D {
	public static final int TYPE_CORNER4 = 4;

	private final int originW;
	private final int sizeW;
	private int w;

	public Cursor4D(
		int minX, int minY, int minZ, int minW,
		int maxX, int maxY, int maxZ, int maxW
	) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.originW = minW;
		this.sizeW = maxW - minW + 1;
		this.end *= this.sizeW;
	}

	public boolean advance() {
		if (this.index == this.end) {
			return false;
		}

		this.x = this.index % this.width;
		int rest = this.index / this.width;

		this.y = rest % this.height;
		rest = rest / this.height;

		this.z = rest % this.depth;
		this.w = rest / this.depth;

		this.index++;
		return true;
	}


	public int nextW() {
		return this.originW + this.w;
	}

	@Override
	public int getNextType() {
		int type = super.getNextType();

		if (this.w == 0 || this.w == this.sizeW - 1) {
			type++;
		}

		return type;
	}
}
