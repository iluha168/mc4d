package com.iluha168.mc4d.api.net.minecraft.client.renderer;

import net.minecraft.client.renderer.Octree;

/**
 * Implemented by {@link Octree}.
 * <p>
 * 2**4 children per {@link Octree.Branch} instead of 2**3.
 */
public interface Octree4 {
	boolean isClose(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW,
		int closeDistance
	);

	/**
	 * Implemented by {@link Octree.AxisSorting}.
	 */
	interface AxisSorting {
		Octree.AxisSorting XYZW = Octree.AxisSorting.XYZ;
		Octree.AxisSorting XZYW = Octree.AxisSorting.XZY;
		Octree.AxisSorting YXZW = Octree.AxisSorting.YXZ;
		Octree.AxisSorting YZXW = Octree.AxisSorting.YZX;
		Octree.AxisSorting ZXYW = Octree.AxisSorting.ZXY;
		Octree.AxisSorting ZYXW = Octree.AxisSorting.ZYX;
		Octree.AxisSorting XYWZ = Octree.AxisSorting.valueOf("XYWZ");
		Octree.AxisSorting XZWY = Octree.AxisSorting.valueOf("XZWY");
		Octree.AxisSorting XWYZ = Octree.AxisSorting.valueOf("XWYZ");
		Octree.AxisSorting XWZY = Octree.AxisSorting.valueOf("XWZY");
		Octree.AxisSorting YXWZ = Octree.AxisSorting.valueOf("YXWZ");
		Octree.AxisSorting YZWX = Octree.AxisSorting.valueOf("YZWX");
		Octree.AxisSorting YWXZ = Octree.AxisSorting.valueOf("YWXZ");
		Octree.AxisSorting YWZX = Octree.AxisSorting.valueOf("YWZX");
		Octree.AxisSorting ZXWY = Octree.AxisSorting.valueOf("ZXWY");
		Octree.AxisSorting ZYWX = Octree.AxisSorting.valueOf("ZYWX");
		Octree.AxisSorting ZWXY = Octree.AxisSorting.valueOf("ZWXY");
		Octree.AxisSorting ZWYX = Octree.AxisSorting.valueOf("ZWYX");
		Octree.AxisSorting WXYZ = Octree.AxisSorting.valueOf("WXYZ");
		Octree.AxisSorting WXZY = Octree.AxisSorting.valueOf("WXZY");
		Octree.AxisSorting WYXZ = Octree.AxisSorting.valueOf("WYXZ");
		Octree.AxisSorting WYZX = Octree.AxisSorting.valueOf("WYZX");
		Octree.AxisSorting WZXY = Octree.AxisSorting.valueOf("WZXY");
		Octree.AxisSorting WZYX = Octree.AxisSorting.valueOf("WZYX");

		int wShift();

		static Octree4.AxisSorting as(Octree.AxisSorting sorting) {
			return (Octree4.AxisSorting) (Object) sorting;
		}

		static Octree.AxisSorting getAxisSorting(int absXDiff, int absYDiff, int absZDiff, int absWDiff) {
			if (absXDiff > absYDiff) { // xy
				if (absYDiff > absZDiff) { // xyz
					if (absZDiff > absWDiff) return XYZW;
					// xyz wz => xywz || wxyz || xwyz
					if (absWDiff > absXDiff) return WXYZ;
					if (absYDiff > absWDiff) return XYWZ;
					return XWYZ;
				} else { // xy zy
					if (absYDiff > absWDiff) { // xyw zyw
						if (absXDiff > absZDiff) return XZYW;
						return ZXYW;
					} else { // xy zy wy => ???y
						if (absXDiff > absZDiff) { // xz xy zy wy => xwzy || wxzy || xzwy
							if (absWDiff > absXDiff) return WXZY;
							if (absZDiff > absWDiff) return XZWY;
							return XWZY;
						} else { // zx xy zy wy => zwxy || wzxy || zxwy
							if (absWDiff > absZDiff) return WZXY;
							if (absXDiff > absWDiff) return ZXWY;
							return ZWXY;
						}
					}
				}
			} else { // yx
				if (absXDiff > absZDiff) { // yxz
					if (absZDiff > absWDiff) return YXZW;
					// yxz wz => yxwz || wyxz || ywxz
					if (absWDiff > absYDiff) return WYXZ;
					if (absXDiff > absWDiff) return YXWZ;
					return YWXZ;
				} else { // yx zx
					if (absXDiff > absWDiff) { // yxw zxw
						if (absYDiff > absZDiff) return YZXW;
						return ZYXW;
					} else { // yx zx wx => ???x
						if (absYDiff > absZDiff) { // yz yx zx wx => ywzx || wyzx || yzwx
							if (absWDiff > absYDiff) return WYZX;
							if (absZDiff > absWDiff) return YZWX;
							return YWZX;
						} else { // zy yx zx wx => zwyx || wzyx || zywx
							if (absWDiff > absZDiff) return WZYX;
							if (absYDiff > absWDiff) return ZYWX;
							return ZWYX;
						}
					}
				}
			}
		}
	}
}
