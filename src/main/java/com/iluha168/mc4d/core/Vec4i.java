package com.iluha168.mc4d.core;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.jspecify.annotations.NonNull;

import static com.iluha168.mc4d.math.MathHelpers.det3;

/**
 * All {@link Vec3i} instances are secretly {@link Vec4i} instances.
 */
public interface Vec4i {
	static Vec3i from(int x, int y, int z, int w) {
		Vec3i vec =  new Vec3i(x, y, z);
		Vec4i.setW(vec, w);
		return vec;
	}

	int getX();
	int getY();
	int getZ();
	int getW();
	Vec4i setW(int w);

	/** Helper to not cast all the time. */
	static int getW(Vec3i vec) {
		return ((Vec4i) vec).getW();
	}
	/** Helper to not cast all the time. */
	static void setW(Vec3i vec, int w) {
		((Vec4i) vec).setW(w);
	}

	default @NonNull Vec3i offset(int x, int y, int z, int w) {
		return x == 0 && y == 0 && z == 0 && w == 0
			? (Vec3i) this
			: Vec4i.from(this.getX() + x, this.getY() + y, this.getZ() + z, this.getW() + w);
	}

	default @NonNull Vec3i multiply(int xScale, int yScale, int zScale, int wScale) {
		return Vec4i.from(this.getX() * xScale, this.getY() * yScale, this.getZ() * zScale, this.getW() * wScale);
	}

	default @NonNull Vec3i ana() {
		return this.ana(1);
	}

	default @NonNull Vec3i ana(int steps) {
		return this.relative(Direction4.ANA, steps);
	}

	default @NonNull Vec3i kata() {
		return this.kata(1);
	}

	default @NonNull Vec3i kata(int steps) {
		return this.relative(Direction4.KATA, steps);
	}

	Vec3i relative(Direction direction, int steps);

	/**
	 * {@return the vector perpendicular to the arguments}
	 */
	static @NonNull Vec3i cross(Vec3i a, Vec3i b, Vec3i c) {
		int ax = a.getX(), ay = a.getY(), az = a.getZ(), aw = Vec4i.getW(a);
		int bx = b.getX(), by = b.getY(), bz = b.getZ(), bw = Vec4i.getW(b);
		int cx = c.getX(), cy = c.getY(), cz = c.getZ(), cw = Vec4i.getW(c);
		return Vec4i.from(
			-det3(ay, az, aw, by, bz, bw, cy, cz, cw),
			 det3(ax, az, aw, bx, bz, bw, cx, cz, cw),
			-det3(ax, ay, aw, bx, by, bw, cx, cy, cw),
			 det3(ax, ay, az, bx, by, bz, cx, cy, cz)
		);
	}

	default double distToCenterSqr(double x, double y, double z, double w) {
		double dx = this.getX() + 0.5 - x;
		double dy = this.getY() + 0.5 - y;
		double dz = this.getZ() + 0.5 - z;
		double dw = this.getW() + 0.5 - w;
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	default double distToLowCornerSqr(double x, double y, double z, double w) {
		double dx = this.getX() - x;
		double dy = this.getY() - y;
		double dz = this.getZ() - z;
		double dw = this.getW() - w;
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}
}
