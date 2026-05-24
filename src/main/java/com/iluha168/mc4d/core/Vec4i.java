package com.iluha168.mc4d.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Util;
import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector3i;
import org.jspecify.annotations.NonNull;

import java.util.stream.IntStream;

public class Vec4i extends Vec3i implements Comparable<Vec3i>, Position4i {
	public static final Codec<Vec4i> CODEC = Codec.INT_STREAM
		.comapFlatMap(
			input -> Util
				.fixedSize(input, 4)
				.map(ints -> new Vec4i(ints[0], ints[1], ints[2], ints[3])),
			pos -> IntStream
				.of(pos.getX(), pos.getY(), pos.getZ(), pos.getW())
		);


	public static final StreamCodec<ByteBuf, Vec4i> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, Vec4i::getX,
		ByteBufCodecs.VAR_INT, Vec4i::getY,
		ByteBufCodecs.VAR_INT, Vec4i::getZ,
		ByteBufCodecs.VAR_INT, Vec4i::getW,
		Vec4i::new
	);
	public static final Vec4i ZERO = new Vec4i(0, 0, 0, 0);
	private int w;

	public Vec4i(int x, int y, int z, int w) {
		super(x, y, z);
		this.w = w;
	}

	@Override
	public boolean equals(@NonNull Object o) {
		if (this == o) {
			return true;
		} else {
			return o instanceof Vec3i vec3i
				&& this.getX() == vec3i.getX()
				&& this.getY() == vec3i.getY()
				&& this.getZ() == vec3i.getZ()
				&& this.getW() == ((Position4i) vec3i).getW();
		}
	}

	@Override
	public int hashCode() {
		int result = this.getW();
		result = result * 31 + this.getZ();
		result = result * 31 + this.getY();
		result = result * 31 + this.getX();
		return result;
	}

	@Override
	public int compareTo(Vec3i pos) {
		if (this.getY() != pos.getY()) {
			return this.getY() - pos.getY();
		}
		if (this.getZ() != pos.getZ()) {
			return this.getZ() - pos.getZ();
		}
		if (this.getW() != ((Position4i) pos).getW()) {
			return this.getW() - ((Position4i) pos).getW();
		}
		return this.getX() - pos.getX();
	}

	@Override
	public int getW() {
		return this.w;
	}

	@Override
	protected @NonNull Vec4i setX(int x) {
		return (Vec4i) super.setX(x);
	}

	@Override
	protected @NonNull Vec4i setY(int y) {
		return (Vec4i) super.setY(y);
	}

	@Override
	protected @NonNull Vec4i setZ(int z) {
		return (Vec4i) super.setZ(z);
	}

	protected @NonNull Vec4i setW(int w) {
		this.w = w;
		return this;
	}

	@Override
	public @NonNull Vec4i offset(int x, int y, int z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: offset"));
	}

	public @NonNull Vec4i offset(int x, int y, int z, int w) {
		return x == 0 && y == 0 && z == 0 && w == 0
			? this
			: new Vec4i(this.getX() + x, this.getY() + y, this.getZ() + z, this.getW() + w);
	}

	@Override
	public @NonNull Vec4i offset(Vec3i vec) {
		return this.offset(vec.getX(), vec.getY(), vec.getZ(), ((Position4i) vec).getW());
	}

	@Override
	public @NonNull Vec4i subtract(Vec3i vec) {
		return this.offset(-vec.getX(), -vec.getY(), -vec.getZ(), -((Position4i) vec).getW());
	}

	@Override
	public @NonNull Vec4i multiply(int scale) {
		if (scale == 1) {
			return this;
		}
		return scale == 0 ? ZERO : new Vec4i(this.getX() * scale, this.getY() * scale, this.getZ() * scale, this.getW() * scale);
	}

	@Override
	public @NonNull Vec4i multiply(int xScale, int yScale, int zScale) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: multiply"));
	}

	public Vec4i multiply(int xScale, int yScale, int zScale, int wScale) {
		return new Vec4i(this.getX() * xScale, this.getY() * yScale, this.getZ() * zScale, this.getW() * wScale);
	}

	@Override
	public @NonNull Vec4i above() {
		return this.above(1);
	}

	@Override
	public @NonNull Vec4i above(int steps) {
		return this.relative(Direction.UP, steps);
	}

	@Override
	public @NonNull Vec4i below() {
		return this.below(1);
	}

	@Override
	public @NonNull Vec4i below(int steps) {
		return this.relative(Direction.DOWN, steps);
	}

	@Override
	public @NonNull Vec4i north() {
		return this.north(1);
	}

	@Override
	public @NonNull Vec4i north(int steps) {
		return this.relative(Direction.NORTH, steps);
	}

	@Override
	public @NonNull Vec4i south() {
		return this.south(1);
	}

	@Override
	public @NonNull Vec4i south(int steps) {
		return this.relative(Direction.SOUTH, steps);
	}

	@Override
	public @NonNull Vec4i west() {
		return this.west(1);
	}

	@Override
	public @NonNull Vec4i west(int steps) {
		return this.relative(Direction.WEST, steps);
	}

	@Override
	public @NonNull Vec4i east() {
		return this.east(1);
	}

	@Override
	public @NonNull Vec4i east(int steps) {
		return this.relative(Direction.EAST, steps);
	}

	public @NonNull Vec4i ana() {
		return this.ana(1);
	}

	public @NonNull Vec4i ana(int steps) {
		return this.relative(Direction4.ANA, steps);
	}

	public @NonNull Vec4i kata() {
		return this.kata(1);
	}

	public @NonNull Vec4i kata(int steps) {
		return this.relative(Direction4.KATA, steps);
	}

	@Override
	public @NonNull Vec4i relative(@NonNull Direction direction) {
		return this.relative(direction, 1);
	}

	@Override
	public @NonNull Vec4i relative(@NonNull Direction direction, int steps) {
		return steps == 0
			? this
			: new Vec4i(
				this.getX() + direction.getStepX() * steps,
				this.getY() + direction.getStepY() * steps,
				this.getZ() + direction.getStepZ() * steps,
				this.getW() + Direction4.as(direction).getStepW() * steps
		);
	}

	@Override
	public @NonNull Vec4i relative(Direction.@NonNull Axis axis, int steps) {
		if (steps == 0) {
			return this;
		}
		int xStep = axis == Direction .Axis.X ? steps : 0;
		int yStep = axis == Direction .Axis.Y ? steps : 0;
		int zStep = axis == Direction .Axis.Z ? steps : 0;
		int wStep = axis == Direction4.Axis.W ? steps : 0;
		return new Vec4i(this.getX() + xStep, this.getY() + yStep, this.getZ() + zStep, this.getW() + wStep);
	}

	@Override
	public @NonNull Vec4i cross(@NonNull Vec3i upVector) {
		throw Util.pauseInIde(new ArithmeticException("No cross product defined for 4D space"));
	}

	// `closerThan` does not need an override.
	// `closerToCenterThan` does not need an override.

	@Override
	public double distSqr(Vec3i pos) {
		return this.distToLowCornerSqr(pos.getX(), pos.getY(), pos.getZ(), ((Position4i) pos).getW());
	}

	@Override
	public double distToCenterSqr(Position pos) {
		return this.distToCenterSqr(pos.x(), pos.y(), pos.z(), ((Position4) pos).w());
	}

	@Override
	public double distToCenterSqr(double x, double y, double z) {
		throw new IllegalArgumentException("Not patched 3D space: distance to center sqr");
	}

	public double distToCenterSqr(double x, double y, double z, double w) {
		double dx = this.getX() + 0.5 - x;
		double dy = this.getY() + 0.5 - y;
		double dz = this.getZ() + 0.5 - z;
		double dw = this.getW() + 0.5 - w;
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	@Override
	public double distToLowCornerSqr(double x, double y, double z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: distance to low corner sqr"));
	}

	public double distToLowCornerSqr(double x, double y, double z, double w) {
		double dx = this.getX() - x;
		double dy = this.getY() - y;
		double dz = this.getZ() - z;
		double dw = this.getW() - w;
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	@Override
	public int distManhattan(Vec3i pos) {
		float xd = Math.abs(pos.getX() - this.getX());
		float yd = Math.abs(pos.getY() - this.getY());
		float zd = Math.abs(pos.getZ() - this.getZ());
		float wd = Math.abs(((Position4i) pos).getW() - this.getW());
		return (int)(xd + yd + zd + wd);
	}

	@Override
	public int distChessboard(Vec3i pos) {
		int xd = Math.abs(this.getX() - pos.getX());
		int yd = Math.abs(this.getY() - pos.getY());
		int zd = Math.abs(this.getZ() - pos.getZ());
		int wd = Math.abs(this.getW() - ((Position4i) pos).getW());
		return Math.max(Math.max(Math.max(xd, yd), zd), wd);
	}

	@Override
	public int get(Direction.@NonNull Axis axis) {
		return Direction4.Axis.as(axis).choose(this.getX(), this.getY(), this.getZ(), this.getW());
	}

	@Override
	public @NonNull Vector3i toMutable() {
		// TODO
		throw Util.pauseInIde(new NotImplementedException());
	}

	@Override
	public @NonNull String toString() {
		return MoreObjects.toStringHelper(this)
			.add("x", this.getX())
			.add("y", this.getY())
			.add("z", this.getZ())
			.add("w", this.getW())
			.toString();
	}

	@Override
	public @NonNull String toShortString() {
		return this.getX() + ", " + this.getY() + ", " + this.getZ() + ", " + this.getW();
	}
}
