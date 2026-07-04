package com.iluha168.mc4d.math;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

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
	static double det3(
		double m00, double m01, double m02,
		double m10, double m11, double m12,
		double m20, double m21, double m22
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

	static int cube(int x) {
		return x * x * x;
	}

	static double absMax(double a, double b, double c) {
		return Math.max(Math.abs(a), Math.max(Math.abs(b), Math.abs(c)));
	}

	/** {@link Mth#lerp3} */
	static double lerp4(
		double alpha1, double alpha2, double alpha3, double alpha4,
		double x0000, double x1000, double x0100, double x1100, double x0010, double x1010, double x0110, double x1110,
		double x0001, double x1001, double x0101, double x1101, double x0011, double x1011, double x0111, double x1111
	) {
		return Mth.lerp(
			alpha4,
			Mth.lerp3(alpha1, alpha2, alpha3, x0000, x1000, x0100, x1100, x0010, x1010, x0110, x1110),
			Mth.lerp3(alpha1, alpha2, alpha3, x0001, x1001, x0101, x1101, x0011, x1011, x0111, x1111)
		);
	}

	static double length(double x, double y, double z, double w) {
		return Math.sqrt(lengthSquared(x, y, z, w));
	}
	static double lengthSquared(double x, double y, double z, double w) {
		return x * x + y * y + z * z + w * w;
	}

	/**
	 * Returns the {@code index}-th of {@code total} points spread as evenly as possible over the surface of a unit sphere.
	 *
	 * @param index the point's index, expected in {@code [0, total)}
	 * @param total how many points are being distributed over the sphere; must be positive
	 * @return a point on the unit sphere, as a unit-length {@link Vec3}
	 */
	static Vec3 fibonacciSphere(int index, double total) {
		final double phi = Math.acos(1.0 - 2.0 * ((index + 0.5) / total));
		final double theta = Math.PI * (1.0 + Math.sqrt(5.0)) * index;
		final double sinPhi = Math.sin(phi);
		return new Vec3(sinPhi * Math.cos(theta), sinPhi * Math.sin(theta), Math.cos(phi));
	}
}
