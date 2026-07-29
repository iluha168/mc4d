package com.iluha168.mc4d.world.phys;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;
import java.util.List;

import static com.iluha168.mc4d.math.MathHelpers.det3;

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

	public Vec4(Vec3i vec) {
		this(vec.getX(), vec.getY(), vec.getZ(), Vec4i.getW(vec));
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
		return new Vec4(vec.x - this.x, vec.y - this.y, vec.z - this.z, ((Vec4) vec).w - this.w);
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
		throw Err4.math("No cross product defined for 4D space");
	}

	/**
	 * {@return the vector perpendicular to the arguments}
	 */
	static public @NonNull Vec4 cross(Vec4 a, Vec4 b, Vec4 c) {
		double ax = a.x, ay = a.y, az = a.z, aw = a.w;
		double bx = b.x, by = b.y, bz = b.z, bw = b.w;
		double cx = c.x, cy = c.y, cz = c.z, cw = c.w;
		return new Vec4(
			-det3(ay, az, aw, by, bz, bw, cy, cz, cw),
			det3(ax, az, aw, bx, bz, bw, cx, cz, cw),
			-det3(ax, ay, aw, bx, by, bw, cx, cy, cw),
			det3(ax, ay, az, bx, by, bz, cx, cy, cz)
		);
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
			// Do not rely on this hack while writing mixins - redirect the calls anyway, this is planned for removal
			return this.subtract(x, y, z, z);
		}
		throw Err4.arguments3("Vec4#subtract");
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
			// Do not rely on this hack while writing mixins - redirect the calls anyway, this is planned for removal
			return this.add(x, y, z, z);
		}
		throw Err4.arguments3("Vec4#add");
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
		throw Err4.arguments3("Vec4#distanceToSqr");
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
		throw Err4.arguments3("Vec4#multiply");
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
	public @NonNull Vec4 offsetRandomXZ(@NonNull RandomSource random, float offset) {
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

	/**
	 * Rotates the plane spanned by the two given axes.
	 */
	private @NonNull Vec4 rotate(Direction.@NonNull Axis axis1, Direction.@NonNull Axis axis2, float radians) {
		final float cos = Mth.cos(radians);
		final float sin = Mth.sin(radians);
		final double va = this.get(axis1);
		final double vb = this.get(axis2);
		return this
			.with(axis1, va * cos + vb * sin)
			.with(axis2, vb * cos - va * sin);
	}

	/** Rotates the YZ plane (around the XW plane). */
	@Override
	public @NonNull Vec4 xRot(float radians) {
		return this.rotate(Direction.Axis.Y, Direction.Axis.Z, radians);
	}
	/** Rotates the XW plane (around the YZ plane). */
	public @NonNull Vec4 yzRot(float radians) {
		return this.rotate(Direction.Axis.X, Direction4.Axis.W, radians);
	}
	/** Rotates the XZ plane (around the YW plane). */
	@Override
	public @NonNull Vec4 yRot(float radians) {
		return this.rotate(Direction.Axis.X, Direction.Axis.Z, radians);
	}
	/** Rotates the YW plane (around the XZ plane). */
	public @NonNull Vec4 xzRot(float radians) {
		return this.rotate(Direction.Axis.Y, Direction4.Axis.W, radians);
	}
	/** Rotates the XY plane (around the ZW plane). */
	@Override
	public @NonNull Vec4 zRot(float radians) {
		return this.rotate(Direction.Axis.X, Direction.Axis.Y, radians);
	}
	/** Rotates the ZW plane (around the XY plane). */
	public @NonNull Vec4 xyRot(float radians) {
		return this.rotate(Direction.Axis.Z, Direction4.Axis.W, radians);
	}

	/** Rotates the XZ plane (around the YW plane). */
	@Override
	public @NonNull Vec4 rotateClockwise90() {
		return Vec4.of(super.rotateClockwise90(), this.w);
	}

	/** Alternatively called "forwards" or "look direction". */
	public static @NonNull Vec4 directionFromRotation(@NonNull RotationVec rotation) {
		return directionFromRotation(rotation.x, rotation.y, rotation.w);
	}

	/** Alternatively called "forwards" or "look direction". */
	public static @NonNull Vec4 directionFromRotation(float rotX, float rotY, float rotW) {
		final float yCos = Mth.cos(-rotY * Mth.DEG_TO_RAD - Mth.PI);
		final float ySin = Mth.sin(-rotY * Mth.DEG_TO_RAD - Mth.PI);
		final float xCos = -Mth.cos(-rotX * Mth.DEG_TO_RAD);
		final float xSin = Mth.sin(-rotX * Mth.DEG_TO_RAD);
		final float wCos = Mth.cos(rotW * Mth.DEG_TO_RAD);
		final float wSin = Mth.sin(rotW * Mth.DEG_TO_RAD);
		//noinspection SuspiciousNameCombination
		return new Vec4(ySin * xCos * wCos, xSin, yCos * xCos * wCos, -wSin * xCos);
	}

	/** {@return where "left" is in world coordinates} */
	public static @NonNull Vec4 leftFromRotation(@NonNull RotationVec rotation) {
		final float vr = rotation.v * Mth.DEG_TO_RAD;
		final Vec4 leftAtVRot0 = directionFromRotation(new RotationVec(0.0F, rotation.y - 90.0F, 0.0F, 0.0F));
		final Vec4 anthAtVRot0 = anthFromRotationAtVRot0(rotation);
		return        leftAtVRot0.scale(Mth.cos(vr))
			.subtract(anthAtVRot0.scale(Mth.sin(vr)));
	}

	/** {@return where "anth" is in world coordinates} */
	public static @NonNull Vec4 anthFromRotation(@NonNull RotationVec rotation) {
		final float vr = rotation.v * Mth.DEG_TO_RAD;
		final Vec4 leftAtVRot0 = directionFromRotation(new RotationVec(0.0F, rotation.y - 90.0F, 0.0F, 0.0F));
		final Vec4 anthAtVRot0 = anthFromRotationAtVRot0(rotation);
		return leftAtVRot0.scale(Mth.sin(vr)).add(anthAtVRot0.scale(Mth.cos(vr)));
	}

	private static @NonNull Vec4 anthFromRotationAtVRot0(@NonNull RotationVec rotation) {
		final float yr = rotation.y * Mth.DEG_TO_RAD;
		final float wr = rotation.w * Mth.DEG_TO_RAD;
		final float wSin = Mth.sin(wr);
		return new Vec4(Mth.sin(yr) * wSin, 0.0, -Mth.cos(yr) * wSin, Mth.cos(wr));
	}

	/**
	 * Best-effort inverse of {@link #directionFromRotation}.
	 * Does not work at the poles, and for non-unit vectors. Also, vRot cannot be determined only from the look direction.
	 */
	@Override
	public @NonNull RotationVec rotation() {
		final float yaw = (float) Math.atan2(-this.x, this.z) * Mth.RAD_TO_DEG;
		final float pitch = (float) Math.asin(-this.y / this.length()) * Mth.RAD_TO_DEG;
		final float wRot = (float) Math.atan2(this.w, Math.sqrt(this.x * this.x + this.z * this.z)) * Mth.RAD_TO_DEG;
		return new RotationVec(pitch, yaw, wRot, 0);
	}

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
	@Deprecated // Just access w directly
	public final double w() {
		return this.w;
	}

	// do not remove `toVector3f`, it is used in rendering

	// `projectedOn`, surprisingly, does not need an override

	/**
	 * Converts direction vector from an entity's local frame into world coordinates.
	 * Inverse of {@link #applyRotationToWorldCoordinates}.
	 * @param rotation the local frame.
	 * @param direction [forwards; up; left; anth].
	 */
	public static @NonNull Vec4 applyLocalCoordinatesToRotation(@NonNull RotationVec rotation, @NonNull Vec4 direction) {
		final Vec4 forwards = directionFromRotation(rotation);
		final Vec4 up = directionFromRotation(new RotationVec(rotation.x - 90.0F, rotation.y, rotation.w, rotation.v));
		final Vec4 left = leftFromRotation(rotation);
		final Vec4 anth = anthFromRotation(rotation);
		return   left    .scale(direction.x)
			.add(up      .scale(direction.y))
			.add(forwards.scale(direction.z))
			.add(anth    .scale(direction.w));
	}

	/**
	 * Converts a world vector to a vector in an entity's local frame.
	 * Inverse of {@link #applyLocalCoordinatesToRotation}.
	 */
	public static @NonNull Vec4 applyRotationToWorldCoordinates(@NonNull RotationVec rotation, @NonNull Vec4 world) {
		final Vec4 forwards = directionFromRotation(rotation);
		final Vec4 up = directionFromRotation(new RotationVec(rotation.x - 90.0F, rotation.y, rotation.w, rotation.v));
		final Vec4 left = leftFromRotation(rotation);
		final Vec4 anth = anthFromRotation(rotation);
		return new Vec4(
			world.dot(left),
			world.dot(up),
			world.dot(forwards),
			world.dot(anth)
		);
	}

	/**
	 * Why remove this method?
	 * To add in the local coordinates space, we need to know the local coordinate frame, obviously.
	 * To get that, we need to know all 4 rotations, vRot included.
	 * vRot does not affect the look direction, therefore, vRot cannot be determined from look direction.
	 * But direction is all we get in this signature.
	 */
	@Override
	@Deprecated
	public @NonNull Vec3 addLocalCoordinates(@NonNull Vec3 direction) {
		throw Err4.rotation("Vec4#applyLocalCoordinatesToRotation");
	}

	@Override
	public boolean isFinite() {
		return super.isFinite() && Double.isFinite(this.w);
	}
}
