package com.iluha168.mc4d.world.phys;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.network.LpVec4;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;
import java.util.List;

/**
 * Adds 4th dimension to 3D vectors.
 * Any operation on Vec4 returns a Vec4: Vec3 + Vec4 = Vec3; Vec4 + Vec3 = Vec4.
 * Callers should be oblivious that they work with 4D vectors.
 */
public class Vec4 extends Vec3 implements Position4 {
	public static final Codec<Vec4> CODEC = Codec.DOUBLE
		.listOf()
		.comapFlatMap(
			input -> Util
				.fixedSize(input, 4)
				.map(doubles -> new Vec4(doubles.getFirst(), doubles.get(1), doubles.get(2), doubles.get(3))),
			pos -> List.of(pos.x(), pos.y(), pos.z(), pos.w())
		);

	public static final StreamCodec<ByteBuf, Vec4> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public @NonNull Vec4 decode(ByteBuf input) {
			return new Vec4(input.readDouble(), input.readDouble(), input.readDouble(), input.readDouble());
		}

		@Override
		public void encode(ByteBuf output, Vec4 value) {
			output.writeDouble(value.x());
			output.writeDouble(value.y());
			output.writeDouble(value.z());
			output.writeDouble(value.w());
		}
	};

	public static final StreamCodec<ByteBuf, Vec4> LP_STREAM_CODEC = StreamCodec.of(LpVec4::write, LpVec4::read);

	public static final Vec4 ZERO = new Vec4(0.0, 0.0, 0.0, 0.0);
	public static final Vec4 X_AXIS = new Vec4(1.0, 0.0, 0.0, 0.0);
	public static final Vec4 Y_AXIS = new Vec4(0.0, 1.0, 0.0, 0.0);
	public static final Vec4 Z_AXIS = new Vec4(0.0, 0.0, 1.0, 0.0);
	public static final Vec4 W_AXIS = new Vec4(0.0, 0.0, 0.0, 1.0);

	public final double w;

	public static Vec4 atLowerCornerWithOffset(Vec3i pos, double x, double y, double z, double w) {
		return new Vec4(pos.getX() + x, pos.getY() + y, pos.getZ() + z, Vec4i.getW(pos) + w);
	}

	public Vec4(double x, double y, double z, double w) {
		super(x, y, z);
		this.w = w;
	}

	/**
	 * Backwards compatibility helper. Adds 4th coordinate to a 3D/4D vector, if it does not have one.
	 */
	public static Vec4 of(Vec3 vec, double w) {
		if (vec instanceof Vec4 vec4) return vec4;
		return new Vec4(vec.x, vec.y, vec.z, w);
	}

	@Override
	public @NonNull Vec4 vectorTo(Vec3 vec) {
		return new Vec4(vec.x - this.x, vec.y - this.y, vec.z - this.z, ((Position4) vec).w() - this.w);
	}

	@Override
	public @NonNull Vec4 normalize() {
		double dist = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
		return dist < Mth.EPSILON ? ZERO : new Vec4(this.x / dist, this.y / dist, this.z / dist, this.w / dist);
	}

	@Override
	public double dot(Vec3 vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z + this.w * ((Position4) vec).w();
	}

	@Override
	@Deprecated
	public @NonNull Vec4 cross(@NonNull Vec3 vec) {
		throw Util.pauseInIde(new ArithmeticException("No cross product defined for 4D space"));
	}

	@Override
	public @NonNull Vec4 subtract(Vec3 vec) {
		return this.subtract(vec.x, vec.y, vec.z, ((Position4) vec).w());
	}

	@Override
	public @NonNull Vec4 subtract(double value) {
		return this.subtract(value, value, value, value);
	}

	@Override
	@Deprecated
	public @NonNull Vec4 subtract(double x, double y, double z) {
		if (x == 0 && z == 0) {
			// Call site intends to modify only the Y axis
			return this.subtract(x, y, z, z);
		}
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: subtraction"));
	}

	public @NonNull Vec4 subtract(double x, double y, double z, double w) {
		return this.add(-x, -y, -z, -w);
	}

	@Override
	public @NonNull Vec4 add(double value) {
		return this.add(value, value, value, value);
	}

	@Override
	public @NonNull Vec4 add(Vec3 vec) {
		return this.add(vec.x, vec.y, vec.z, ((Position4) vec).w());
	}

	@Override
	@Deprecated
	public @NonNull Vec4 add(double x, double y, double z) {
		if (x == 0 && z == 0) {
			// Call site intends to modify only the Y axis
			return this.add(x, y, z, z);
		}
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: addition"));
	}

	public @NonNull Vec4 add(double x, double y, double z, double w) {
		return new Vec4(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	@Override
	public boolean closerThan(net.minecraft.core.Position pos, double distance) {
		return this.distanceToSqr(pos.x(), pos.y(), pos.z(), ((Position4) pos).w()) < distance * distance;
	}

	@Override
	public double distanceTo(@NonNull Vec3 vec) {
		return Math.sqrt(this.distanceToSqr(vec));
	}

	@Override
	public double distanceToSqr(Vec3 vec) {
		double xd = vec.x - this.x;
		double yd = vec.y - this.y;
		double zd = vec.z - this.z;
		double wd = ((Position4) vec).w() - this.w;
		return xd * xd + yd * yd + zd * zd + wd * wd;
	}

	@Override
	@Deprecated
	public double distanceToSqr(double x, double y, double z) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: distance to sqr"));
	}

	public double distanceToSqr(double x, double y, double z, double w) {
		double xd = x - this.x;
		double yd = y - this.y;
		double zd = z - this.z;
		double wd = w - this.w;
		return xd * xd + yd * yd + zd * zd + wd * wd;
	}

	@Override
	public boolean closerThan(Vec3 vec, double distanceXZW, double distanceY) {
		double dx = vec.x() - this.x;
		double dy = vec.y() - this.y;
		double dz = vec.z() - this.z;
		double dw = ((Position4) vec).w() - this.w;
		return Mth.lengthSquared(dx, dz, dw) < Mth.square(distanceXZW) && Math.abs(dy) < distanceY;
	}

	@Override
	public @NonNull Vec4 scale(double scale) {
		return this.multiply(scale, scale, scale, scale);
	}

	@Override
	public @NonNull Vec4 reverse() {
		return this.scale(-1.0);
	}

	@Override
	public @NonNull Vec4 multiply(Vec3 scale) {
		return this.multiply(scale.x, scale.y, scale.z, ((Position4) scale).w());
	}

	@Override
	@Deprecated
	public @NonNull Vec4 multiply(double xScale, double yScale, double zScale) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: multiplication"));
	}

	public @NonNull Vec4 multiply(double xScale, double yScale, double zScale, double wScale) {
		return new Vec4(this.x * xScale, this.y * yScale, this.z * zScale, this.w * wScale);
	}

	@Override
	public @NonNull Vec4 horizontal() {
		return new Vec4(this.x, 0.0, this.z, this.w);
	}

	@Override
	public @NonNull Vec4 offsetRandom(RandomSource random, float offset) {
		return this.add(
			(random.nextFloat() - 0.5F) * offset,
			(random.nextFloat() - 0.5F) * offset,
			(random.nextFloat() - 0.5F) * offset,
			(random.nextFloat() - 0.5F) * offset
		);
	}

	@Override
	@Deprecated
	public @NonNull Vec4 offsetRandomXZ(@NonNull RandomSource random, float offset) {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: offsetRandomXZ"));
	}

	public @NonNull Vec4 offsetRandomXZW(RandomSource random, float offset) {
		return this.add(
			(random.nextFloat() - 0.5F) * offset,
			0.0,
			(random.nextFloat() - 0.5F) * offset,
			(random.nextFloat() - 0.5F) * offset
		);
	}

	@Override
	public double length() {
		return Math.sqrt(this.lengthSqr());
	}

	@Override
	public double lengthSqr() {
		return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
	}

	@Override
	public double horizontalDistance() {
		return Math.sqrt(this.horizontalDistanceSqr());
	}

	@Override
	public double horizontalDistanceSqr() {
		return this.x * this.x + this.z * this.z + this.w * this.w;
	}

	@Override
	public boolean equals(@NonNull Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Vec3 vec3)) {
			return false;
		} else {
			return Double.compare(vec3.x, this.x) == 0
				&& Double.compare(vec3.y, this.y) == 0
				&& Double.compare(vec3.z, this.z) == 0
				&& Double.compare(((Position4) vec3).w(), this.w) == 0;
		}
	}

	@Override
	public int hashCode() {
		int result = Double.hashCode(this.x);
		result = 31 * result + Double.hashCode(this.y);
		result = 31 * result + Double.hashCode(this.z);
		return 31 * result + Double.hashCode(this.w);
	}

	@Override
	public @NonNull String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
	}

	@Override
	public @NonNull Vec4 lerp(Vec3 vec, double alpha) {
		return new Vec4(
			Mth.lerp(alpha, this.x, vec.x),
			Mth.lerp(alpha, this.y, vec.y),
			Mth.lerp(alpha, this.z, vec.z),
			Mth.lerp(alpha, this.w, ((Position4) vec).w())
		);
	}

	@Override
	@Deprecated
	public @NonNull Vec4 xRot(float radians) { // We are assuming rotation around XW.
		return Vec4.of(super.xRot(radians), this.w);
	}

	@Override
	@Deprecated
	public @NonNull Vec4 yRot(float radians) { // We are assuming rotation around YW.
		return Vec4.of(super.yRot(radians), this.w);
	}

	@Override
	@Deprecated
	public @NonNull Vec4 zRot(float radians) { // We are assuming rotation around ZW.
		return Vec4.of(super.zRot(radians), this.w);
	}

	@Override
	@Deprecated
	public @NonNull Vec4 rotateClockwise90() {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: rotation around Y axis is ambiguous"));
	}

	// do not touch `rotation` for now

	@Override
	public @NonNull Vec4 align(@NonNull EnumSet<Direction.Axis> axes) {
		double x = axes.contains(Direction .Axis.X) ? Mth.floor(this.x) : this.x;
		double y = axes.contains(Direction .Axis.Y) ? Mth.floor(this.y) : this.y;
		double z = axes.contains(Direction .Axis.Z) ? Mth.floor(this.z) : this.z;
		double w = axes.contains(Direction4.Axis.W) ? Mth.floor(this.w) : this.w;
		return new Vec4(x, y, z, w);
	}

	@Override
	public double get(Direction.@NonNull Axis axis) {
		return Direction4.Axis.as(axis).choose(this.x, this.y, this.z, this.w);
	}

	@Override
	public @NonNull Vec4 with(Direction.@NonNull Axis axis, double value) {
		double x = axis == Direction .Axis.X ? value : this.x;
		double y = axis == Direction .Axis.Y ? value : this.y;
		double z = axis == Direction .Axis.Z ? value : this.z;
		double w = axis == Direction4.Axis.W ? value : this.w;
		return new Vec4(x, y, z, w);
	}

	@Override
	public @NonNull Vec4 relative(@NonNull Direction direction, double distance) {
		Vec3i normal = direction.getUnitVec3i();
		return new Vec4(
			this.x + distance * normal.getX(),
			this.y + distance * normal.getY(),
			this.z + distance * normal.getZ(),
			this.w + distance * Vec4i.getW(normal)
		);
	}

	@Override
	public final double w() {
		return this.w;
	}

	@Override
	@Deprecated
	public @NonNull Vector3f toVector3f() {
		throw Util.pauseInIde(new IllegalArgumentException("Not patched 3D space: toVector3f"));
	}

	// `projectedOn`, surprisingly, does not need an override

	@SuppressWarnings("SuspiciousNameCombination")
	public static @NonNull Vec4 applyLocalCoordinatesToRotation(Vec2 rotation, Vec4 direction) {
		float yCos = Mth.cos((rotation.y + 90.0F) * (float) (Math.PI / 180.0));
		float ySin = Mth.sin((rotation.y + 90.0F) * (float) (Math.PI / 180.0));
		float xCos = Mth.cos(-rotation.x * (float) (Math.PI / 180.0));
		float xSin = Mth.sin(-rotation.x * (float) (Math.PI / 180.0));
		float xCosUp = Mth.cos((-rotation.x + 90.0F) * (float) (Math.PI / 180.0));
		float xSinUp = Mth.sin((-rotation.x + 90.0F) * (float) (Math.PI / 180.0));
		Vec3 forwards = new Vec3(yCos * xCos, xSin, ySin * xCos);
		Vec3 up = new Vec3(yCos * xCosUp, xSinUp, ySin * xCosUp);
		Vec3 left = forwards.cross(up).scale(-1.0);
		double xa = forwards.x * direction.z + up.x * direction.y + left.x * direction.x;
		double ya = forwards.y * direction.z + up.y * direction.y + left.y * direction.x;
		double za = forwards.z * direction.z + up.z * direction.y + left.z * direction.x;
		return new Vec4(xa, ya, za, direction.w); // Literally what am I supposed to do? The rotation happens in 3D.
	}

	@Override
	@Deprecated
	public @NonNull Vec3 addLocalCoordinates(@NonNull Vec3 direction) {
		return direction instanceof Vec4 direction4
			? Vec4.applyLocalCoordinatesToRotation(this.rotation(), direction4)
		    : super.addLocalCoordinates(direction);
	}

	@Override
	public boolean isFinite() {
		return super.isFinite() && Double.isFinite(this.w);
	}
}
