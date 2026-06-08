package com.iluha168.mc4d.math;

public interface MathHelpers {
	static int det3(
		int m00, int m01, int m02,
		int m10, int m11, int m12,
		int m20, int m21, int m22
	) {
		return (m00 * m11 - m01 * m10) * m22
			+ (m02 * m10 - m00 * m12) * m21
			+ (m01 * m12 - m02 * m11) * m20;
	}

	static int absMax(int a, int b, int c) {
		return Math.max(Math.abs(a), Math.max(Math.abs(b), Math.abs(c)));
	}

	static int chessboardDistance(
		int x0, int z0, int w0,
		int x1, int z1, int w1
	) {
		return absMax(x1 - x0, z1 - z0, w1 - w0);
	}
}
