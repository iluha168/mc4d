package com.iluha168.mc4d.world.phys;

import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Position4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Adds 4th dimension to 3D AABBs.
 * Any operation on AABB4 returns an AABB4: AABB + AABB4 = AABB; AABB4 + AABB = AABB4.
 * Callers should be oblivious that they work with 4D boxes.
 */
public class AABB4 extends AABB implements IAABB4 {
	public static final double EPSILON = 1.0E-7;
	public static final AABB4 INFINITE = new AABB4(
		Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
		Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
	);

	public final double minW;
	public final double maxW;

	public AABB4(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW
	) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.minW = Math.min(minW, maxW);
		this.maxW = Math.max(minW, maxW);
	}

	public AABB4(Vec4 begin, Vec4 end) {
		this(
			begin.x, begin.y, begin.z, begin.w,
			end.x, end.y, end.z, end.w
		);
	}

	@Override
	public double minW() {
		return this.minW;
	}
	@Override
	public double maxW() {
		return this.maxW;
	}

	@Override
	public @NonNull AABB4 setMinX(double minX) {
		return new AABB4(minX, this.minY, this.minZ, this.minW, this.maxX, this.maxY, this.maxZ, this.maxW);
	}

	@Override
	public @NonNull AABB4 setMinY(double minY) {
		return new AABB4(this.minX, minY, this.minZ, this.minW, this.maxX, this.maxY, this.maxZ, this.maxW);
	}

	@Override
	public @NonNull AABB4 setMinZ(double minZ) {
		return new AABB4(this.minX, this.minY, minZ, this.minW, this.maxX, this.maxY, this.maxZ, this.maxW);
	}

	public @NonNull AABB4 setMinW(double minW) {
		return new AABB4(this.minX, this.minY, this.minZ, minW, this.maxX, this.maxY, this.maxZ, this.maxW);
	}

	@Override
	public @NonNull AABB4 setMaxX(double maxX) {
		return new AABB4(this.minX, this.minY, this.minZ, this.minW, maxX, this.maxY, this.maxZ, this.maxW);
	}

	@Override
	public @NonNull AABB4 setMaxY(double maxY) {
		return new AABB4(this.minX, this.minY, this.minZ, this.minW, this.maxX, maxY, this.maxZ, this.maxW);
	}

	@Override
	public @NonNull AABB4 setMaxZ(double maxZ) {
		return new AABB4(this.minX, this.minY, this.minZ, this.minW, this.maxX, this.maxY, maxZ, this.maxW);
	}

	public @NonNull AABB4 setMaxW(double maxW) {
		return new AABB4(this.minX, this.minY, this.minZ, this.minW, this.maxX, this.maxY, this.maxZ, maxW);
	}

	@Override
	public double min(Direction.@NonNull Axis axis) {
		return Direction4.Axis.as(axis).choose(this.minX, this.minY, this.minZ, this.minW);
	}

	@Override
	public double max(Direction.@NonNull Axis axis) {
		return Direction4.Axis.as(axis).choose(this.maxX, this.maxY, this.maxZ, this.maxW);
	}

	@Override
	public boolean equals(@NonNull Object o) {
		return this == o || (
			o instanceof AABB aabb
			&& Double.compare(aabb.minX, this.minX) == 0
			&& Double.compare(aabb.minY, this.minY) == 0
			&& Double.compare(aabb.minZ, this.minZ) == 0
			&& Double.compare(((IAABB4) aabb).minW(), this.minW) == 0
			&& Double.compare(aabb.maxX, this.maxX) == 0
			&& Double.compare(aabb.maxY, this.maxY) == 0
			&& Double.compare(aabb.maxZ, this.maxZ) == 0
			&& Double.compare(((IAABB4) aabb).maxW(), this.maxW) == 0
		);
	}

	@Override
	public int hashCode() {
		int result = Double.hashCode(this.minX);
		result = 31 * result + Double.hashCode(this.minY);
		result = 31 * result + Double.hashCode(this.minZ);
		result = 31 * result + Double.hashCode(this.minW);
		result = 31 * result + Double.hashCode(this.maxX);
		result = 31 * result + Double.hashCode(this.maxY);
		result = 31 * result + Double.hashCode(this.maxZ);
		return 31 * result + Double.hashCode(this.maxW);
	}

	@Override
	@Deprecated
	public @NonNull AABB4 contract(double xa, double ya, double za) {
		throw Err4.arguments3("AABB4#contract");
	}

	public @NonNull AABB4 contract(double xa, double ya, double za, double wa) {
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		double minW = this.minW;
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;
		double maxW = this.maxW;

		if (xa < 0.0) {
			minX -= xa;
		} else if (xa > 0.0) {
			maxX -= xa;
		}
		if (ya < 0.0) {
			minY -= ya;
		} else if (ya > 0.0) {
			maxY -= ya;
		}
		if (za < 0.0) {
			minZ -= za;
		} else if (za > 0.0) {
			maxZ -= za;
		}
		if (wa < 0.0) {
			minW -= wa;
		} else if (wa > 0.0) {
			maxW -= wa;
		}

		return new AABB4(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Override
	public @NonNull AABB4 expandTowards(Vec3 delta) {
		return this.expandTowards(delta.x, delta.y, delta.z, ((Position4) delta).w());
	}

	@Override
	@Deprecated
	public @NonNull AABB4 expandTowards(double xa, double ya, double za) {
		if (xa == 0 && za == 0) {
			// Call site intends to modify only the Y axis
			return this.expandTowards(xa, ya, za, za);
		}
		throw Err4.arguments3("AABB4#expandTowards");
	}

	public @NonNull AABB4 expandTowards(double xa, double ya, double za, double wa) {
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		double minW = this.minW;
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;
		double maxW = this.maxW;

		if (xa < 0.0) {
			minX += xa;
		} else if (xa > 0.0) {
			maxX += xa;
		}
		if (ya < 0.0) {
			minY += ya;
		} else if (ya > 0.0) {
			maxY += ya;
		}
		if (za < 0.0) {
			minZ += za;
		} else if (za > 0.0) {
			maxZ += za;
		}
		if (wa < 0.0) {
			minW += wa;
		} else if (wa > 0.0) {
			maxW += wa;
		}

		return new AABB4(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Override
	@Deprecated
	public @NonNull AABB4 inflate(double xAdd, double yAdd, double zAdd) {
		if (xAdd == 0 && zAdd == 0) {
			// Y-only
			return this.inflate(xAdd, yAdd, zAdd, zAdd);
		}
		throw Err4.arguments3("AABB4#inflate");
	}

	public @NonNull AABB4 inflate(double xAdd, double yAdd, double zAdd, double wAdd) {
		double minX = this.minX - xAdd;
		double minY = this.minY - yAdd;
		double minZ = this.minZ - zAdd;
		double minW = this.minW - wAdd;
		double maxX = this.maxX + xAdd;
		double maxY = this.maxY + yAdd;
		double maxZ = this.maxZ + zAdd;
		double maxW = this.maxW + wAdd;
		return new AABB4(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Override
	public @NonNull AABB4 inflate(double amountToAddInAllDirections) {
		return this.inflate(amountToAddInAllDirections, amountToAddInAllDirections, amountToAddInAllDirections, amountToAddInAllDirections);
	}

	@Override
	public @NonNull AABB4 intersect(AABB other) {
		double minX = Math.max(this.minX, other.minX);
		double minY = Math.max(this.minY, other.minY);
		double minZ = Math.max(this.minZ, other.minZ);
		double minW = Math.max(this.minW, ((IAABB4) other).minW());
		double maxX = Math.min(this.maxX, other.maxX);
		double maxY = Math.min(this.maxY, other.maxY);
		double maxZ = Math.min(this.maxZ, other.maxZ);
		double maxW = Math.min(this.maxW, ((IAABB4) other).maxW());
		return new AABB4(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Override
	public @NonNull AABB4 minmax(AABB other) {
		double minX = Math.min(this.minX, other.minX);
		double minY = Math.min(this.minY, other.minY);
		double minZ = Math.min(this.minZ, other.minZ);
		double minW = Math.min(this.minW, ((IAABB4) other).minW());
		double maxX = Math.max(this.maxX, other.maxX);
		double maxY = Math.max(this.maxY, other.maxY);
		double maxZ = Math.max(this.maxZ, other.maxZ);
		double maxW = Math.max(this.maxW, ((IAABB4) other).maxW());
		return new AABB4(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Override
	@Deprecated
	public @NonNull AABB4 move(double xa, double ya, double za) {
		if (xa == 0 && za == 0) {
			// Call site intends to modify only the Y axis
			return this.move(xa, ya, za, za);
		}
		throw Err4.arguments3("AABB4#move");
	}

	public @NonNull AABB4 move(double xa, double ya, double za, double wa) {
		return new AABB4(
			this.minX + xa, this.minY + ya, this.minZ + za, this.minW + wa,
			this.maxX + xa, this.maxY + ya, this.maxZ + za, this.maxW + wa
		);
	}

	@Override
	public @NonNull AABB4 move(BlockPos pos) {
		return new AABB4(
			this.minX + pos.getX(), this.minY + pos.getY(), this.minZ + pos.getZ(), this.minW + Vec4i.getW(pos),
			this.maxX + pos.getX(), this.maxY + pos.getY(), this.maxZ + pos.getZ(), this.maxW + Vec4i.getW(pos)
		);
	}

	@Override
	public @NonNull AABB4 move(Vec3 pos) {
		return this.move(pos.x, pos.y, pos.z, ((Position4) pos).w());
	}

	@Override
	@Deprecated
	public @NonNull AABB4 move(@NonNull Vector3f pos) {
		throw Err4.arguments3("AABB4#move");
	}

	public boolean intersects(AABB aabb) {
		IAABB4 aabb4 = (IAABB4) aabb;
		return this.intersects(
			aabb.minX, aabb.minY, aabb.minZ, aabb4.minW(),
			aabb.maxX, aabb.maxY, aabb.maxZ, aabb4.maxW()
		);
	}

	@Override
	@Deprecated
	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		throw Err4.arguments3("AABB4#intersects");
	}

	public boolean intersects(double minX, double minY, double minZ, double minW, double maxX, double maxY, double maxZ, double maxW) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ && this.minW < maxW && this.maxW > minW;
	}

	@Override
	public boolean intersects(Vec3 min, Vec3 max) {
		return this.intersects(
			Math.min(min.x, max.x),
			Math.min(min.y, max.y),
			Math.min(min.z, max.z),
			Math.min(((Position4) min).w(), ((Position4) max).w()),
			Math.max(min.x, max.x),
			Math.max(min.y, max.y),
			Math.max(min.z, max.z),
			Math.max(((Position4) min).w(), ((Position4) max).w())
		);
	}

	@Override
	public boolean intersects(BlockPos pos) {
		return this.intersects(
			pos.getX(), pos.getY(), pos.getZ(), Vec4i.getW(pos),
			pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, Vec4i.getW(pos) + 1
		);
	}

	@Override
	public boolean contains(Vec3 vec) {
		return this.contains(vec.x, vec.y, vec.z, ((Position4) vec).w());
	}

	@Override
	@Deprecated
	public boolean contains(double x, double y, double z) {
		throw Err4.arguments3("AABB4#contains");
	}

	public boolean contains(double x, double y, double z, double w) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ && w >= this.minW && w < this.maxW;
	}

	@Override
	public double getSize() {
		double xs = this.getXsize();
		double ys = this.getYsize();
		double zs = this.getZsize();
		double ws = this.getWsize();
		return (xs + ys + zs + ws) / 4.0;
	}

	// `getXsize`, `getYsize`, `getZsize` do not need overrides.
	public double getWsize() {
		return this.maxW - this.minW;
	}

	@Override
	@Deprecated
	public @NonNull AABB4 deflate(double xSubtract, double ySubtract, double zSubtract) {
		throw Err4.arguments3("AABB4#deflate");
	}

	public @NonNull AABB4 deflate(double xSubtract, double ySubtract, double zSubtract, double wSubtract) {
		return this.inflate(-xSubtract, -ySubtract, -zSubtract, -wSubtract);
	}

	@Override
	public @NonNull AABB4 deflate(double amount) {
		return this.inflate(-amount);
	}

	@Override
	public @NonNull Optional<Vec3> clip(@NonNull Vec3 from, @NonNull Vec3 to) {
		return clip(this.minX, this.minY, this.minZ, this.minW, this.maxX, this.maxY, this.maxZ, this.maxW, from, to);
	}

	public static Optional<Vec3> clip(
		double minX, double minY, double minZ, double minW,
		double maxX, double maxY, double maxZ, double maxW,
		Vec3 from, Vec3 to
	) {
		double[] scaleReference = new double[]{1.0};
		double dx = to.x - from.x;
		double dy = to.y - from.y;
		double dz = to.z - from.z;
		double dw = ((Position4) to).w() - ((Position4) from).w();
		Direction direction = getDirection(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW, from, scaleReference, null, dx, dy, dz, dw);
		if (direction == null) {
			return Optional.empty();
		} else {
			double scale = scaleReference[0];
			return Optional.of(new Vec4(scale * dx, scale * dy, scale * dz, scale * dw).add(from));
		}
	}

	public static @Nullable Direction getDirection(
		double minX,
		double minY,
		double minZ,
		double minW,
		double maxX,
		double maxY,
		double maxZ,
		double maxW,
		Vec3 from,
		double[] scaleReference,
		@Nullable Direction direction,
		double dx,
		double dy,
		double dz,
		double dw
	) {
		final double fromW = ((Position4) from).w();
		if (dx > EPSILON) {
			direction = clipPoint(scaleReference, direction, dx, dy, dz, dw, minX, minY, maxY, minZ, maxZ, minW, maxW, Direction.WEST, from.x, from.y, from.z, fromW);
		} else if (dx < -EPSILON) {
			direction = clipPoint(scaleReference, direction, dx, dy, dz, dw, maxX, minY, maxY, minZ, maxZ, minW, maxW, Direction.EAST, from.x, from.y, from.z, fromW);
		}
		if (dy > EPSILON) {
			direction = clipPoint(scaleReference, direction, dy, dz, dw, dx, minY, minZ, maxZ, minW, maxW, minX, maxX, Direction.DOWN, from.y, from.z, fromW, from.x);
		} else if (dy < -EPSILON) {
			direction = clipPoint(scaleReference, direction, dy, dz, dw, dx, maxY, minZ, maxZ, minW, maxW, minX, maxX, Direction.UP, from.y, from.z, fromW, from.x);
		}
		if (dz > EPSILON) {
			direction = clipPoint(scaleReference, direction, dz, dw, dx, dy, minZ, minW, maxW, minX, maxX, minY, maxY, Direction.NORTH, from.z, fromW, from.x, from.y);
		} else if (dz < -EPSILON) {
			direction = clipPoint(scaleReference, direction, dz, dw, dx, dy, maxZ, minW, maxW, minX, maxX, minY, maxY, Direction.SOUTH, from.z, fromW, from.x, from.y);
		}
		if (dw > EPSILON) {
			direction = clipPoint(scaleReference, direction, dw, dx, dy, dz, minW, minX, maxX, minY, maxY, minZ, maxZ, Direction4.KATA, fromW, from.x, from.y, from.z);
		} else if (dw < -EPSILON) {
			direction = clipPoint(scaleReference, direction, dw, dx, dy, dz, maxW, minX, maxX, minY, maxY, minZ, maxZ, Direction4.ANA, fromW, from.x, from.y, from.z);
		}
		return direction;
	}

	private static @Nullable Direction clipPoint(
		double[] scaleReference,
		@Nullable Direction direction,
		double da,
		double db,
		double dc,
		double dd,
		double point,
		double minB,
		double maxB,
		double minC,
		double maxC,
		double minD,
		double maxD,
		Direction newDirection,
		double fromA,
		double fromB,
		double fromC,
		double fromD
	) {
		double s = (point - fromA) / da;
		double pb = fromB + s * db;
		double pc = fromC + s * dc;
		double pd = fromD + s * dd;
		if (0.0 < s && s < scaleReference[0]
			&& minB - EPSILON < pb && pb < maxB + EPSILON
			&& minC - EPSILON < pc && pc < maxC + EPSILON
			&& minD - EPSILON < pd && pd < maxD + EPSILON
		) {
			scaleReference[0] = s;
			return newDirection;
		} else {
			return direction;
		}
	}

	@Override
	public boolean collidedAlongVector(@NonNull Vec3 vector, List<AABB> aabbs) {
		Vec4 from = this.getCenter();
		Vec4 to = from.add(vector);

		for (AABB shapePart : aabbs) {
			if (!(shapePart instanceof AABB4 shapePart4)) throw Err4.container3();
			AABB4 inflated = shapePart4.inflate(
				this.getXsize() * 0.5 - EPSILON,
				this.getYsize() * 0.5 - EPSILON,
				this.getZsize() * 0.5 - EPSILON,
				this.getWsize() * 0.5 - EPSILON
			);
			if (inflated.contains(to) || inflated.contains(from)) {
				return true;
			}

			if (inflated.clip(from, to).isPresent()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public double distanceToSqr(Vec3 point) {
		double dx = Math.max(Math.max(this.minX - point.x, point.x - this.maxX), 0.0);
		double dy = Math.max(Math.max(this.minY - point.y, point.y - this.maxY), 0.0);
		double dz = Math.max(Math.max(this.minZ - point.z, point.z - this.maxZ), 0.0);
		double pointW = ((Position4) point).w();
		double dw = Math.max(Math.max(this.minW - pointW , pointW  - this.maxW), 0.0);
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	@Override
	public double distanceToSqr(AABB boundingBox) {
		double dx = Math.max(Math.max(this.minX - boundingBox.maxX, boundingBox.minX - this.maxX), 0.0);
		double dy = Math.max(Math.max(this.minY - boundingBox.maxY, boundingBox.minY - this.maxY), 0.0);
		double dz = Math.max(Math.max(this.minZ - boundingBox.maxZ, boundingBox.minZ - this.maxZ), 0.0);
		IAABB4 bounding4 = (IAABB4) boundingBox;
		double dw = Math.max(Math.max(this.minW - bounding4.maxW(), bounding4.minW() - this.maxW), 0.0);
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	@Override
	public @NonNull String toString() {
		return "AABB4["
			+ this.minX + ", " + this.minY + ", " + this.minZ + ", " + this.minW
			+ "] -> ["
			+ this.maxX + ", " + this.maxY + ", " + this.maxZ + ", " + this.maxW
			+ "]";
	}

	@Override
	public boolean hasNaN() {
		return Double.isNaN(this.minX)
			|| Double.isNaN(this.minY)
			|| Double.isNaN(this.minZ)
			|| Double.isNaN(this.minW)
			|| Double.isNaN(this.maxX)
			|| Double.isNaN(this.maxY)
			|| Double.isNaN(this.maxZ)
			|| Double.isNaN(this.maxW);
	}

	@Override
	public @NonNull Vec4 getCenter() {
		return new Vec4(
			Mth.lerp(0.5, this.minX, this.maxX),
			Mth.lerp(0.5, this.minY, this.maxY),
			Mth.lerp(0.5, this.minZ, this.maxZ),
			Mth.lerp(0.5, this.minW, this.maxW)
		);
	}

	@Override
	public @NonNull Vec4 getBottomCenter() {
		return new Vec4(
			Mth.lerp(0.5, this.minX, this.maxX),
			this.minY,
			Mth.lerp(0.5, this.minZ, this.maxZ),
			Mth.lerp(0.5, this.minW, this.maxW)
		);
	}

	@Override
	public @NonNull Vec4 getMinPosition() {
		return new Vec4(this.minX, this.minY, this.minZ, this.minW);
	}

	@Override
	public @NonNull Vec4 getMaxPosition() {
		return new Vec4(this.maxX, this.maxY, this.maxZ, this.maxW);
	}

	public static AABB4 ofSize(Vec4 center, double sizeX, double sizeY, double sizeZ, double sizeW) {
		double halfX = sizeX / 2.0;
		double halfY = sizeY / 2.0;
		double halfZ = sizeZ / 2.0;
		double halfW = sizeW / 2.0;
		return new AABB4(
			center.x - halfX, center.y - halfY, center.z - halfZ, center.w - halfW,
			center.x + halfX, center.y + halfY, center.z + halfZ, center.w + halfW
		);
	}

	/**
	 * {@return true if this AABB4 is infinite in all directions}
	 */
	public boolean isInfinite() {
		return this == INFINITE || (
			Double.isInfinite(this.minX) && Double.isInfinite(this.minY) && Double.isInfinite(this.minZ) && Double.isInfinite(this.minW) &&
			Double.isInfinite(this.maxX) && Double.isInfinite(this.maxY) && Double.isInfinite(this.maxZ) && Double.isInfinite(this.maxW)
		);
	}
}
