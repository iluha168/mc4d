package com.iluha168.mc4d.util;

import net.minecraft.util.StaticCache2D;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public class StaticCache3D<T> extends StaticCache2D<T> {
	public final int minW;
	public final int sizeW;
	private final Object[] cache;

	public static <T> StaticCache3D<T> create(
		int centerX, int centerZ, int centerW,
		int range, StaticCache3D.Initializer<T> initializer
	) {
		int minX = centerX - range;
		int minZ = centerZ - range;
		int minW = centerW - range;
		int size = 2 * range + 1;
		return new StaticCache3D<>(minX, minZ, minW, size, size, size, initializer);
	}

	private StaticCache3D(
		int minX, int minZ, int minW,
		int sizeX, int sizeZ, int sizeW,
		StaticCache3D.Initializer<T> initializer
	) {
		//noinspection DataFlowIssue
		super(minX, minZ, sizeX, sizeZ, (_, _) -> null);
		this.minW = minW;
		this.sizeW = sizeW;
		this.cache = new Object[this.sizeX * this.sizeZ * this.sizeW];

		for (int x = minX; x < minX + sizeX; x++)
			for (int z = minZ; z < minZ + sizeZ; z++)
				for (int w = minW; w < minW + sizeW; w++)
					this.cache[this.getIndex(x, z, w)] = initializer.get(x, z, w);
	}

	@Override
	public @NonNull T get(int x, int z) {
		throw Err4.arguments2("StaticCache3D#get");
	}
	public @NonNull T get(int x, int z, int w) {
		if (this.contains(x, z, w)) {
			//noinspection unchecked
			return (T) this.cache[this.getIndex(x, z, w)];
		}
		throw new IllegalArgumentException("Requested out of range value ("+x+","+z+","+w+") from "+this);
	}

	@Override
	public boolean contains(int x, int z) {
		throw Err4.arguments2("StaticCache3D#contains");
	}
	public boolean contains(int x, int z, int w) {
		int deltaW = w - this.minW;
		return super.contains(x, z) && deltaW >= 0 && deltaW < this.sizeW;
	}

	@Override
	public @NonNull String toString() {
		return String.format(
			Locale.ROOT, "StaticCache3D[%d, %d, %d, %d, %d, %d]",
			this.minX, this.minZ, this.minW,
			this.minX + this.sizeX, this.minZ + this.sizeZ, this.minW + this.sizeW
		);
	}

	private int getIndex(int x, int z, int w) {
		int deltaX = x - this.minX;
		int deltaZ = z - this.minZ;
		int deltaW = w - this.minW;
		return (deltaX * this.sizeZ + deltaZ) * this.sizeW + deltaW;
	}

	@FunctionalInterface
	public interface Initializer<T> {
		T get(int x, int z, int w);
	}
}
