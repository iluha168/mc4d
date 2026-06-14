package com.iluha168.mc4d.core;

import com.google.common.collect.ImmutableList;
import com.iluha168.mc4d.world.phys.Vec4;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

/**
 * <b>All {@link Direction}</b> instances implement {@link Direction4}.
 * Use {@link Direction4#as} for type-casting.
 */
public interface Direction4 {
	Direction KATA = Direction.valueOf("KATA");
	Direction ANA = Direction.valueOf("ANA");

	ImmutableList<Direction.Axis> YXZW_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z, Axis.W);
	ImmutableList<Direction.Axis> YZXW_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X, Axis.W);
	ImmutableList<Direction.Axis> YXWZ_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Axis.W, Direction.Axis.Z);
	ImmutableList<Direction.Axis> YZWX_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.Z, Axis.W, Direction.Axis.X);
	ImmutableList<Direction.Axis> YWXZ_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Axis.W, Direction.Axis.X, Direction.Axis.Z);
	ImmutableList<Direction.Axis> YWZX_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Axis.W, Direction.Axis.Z, Direction.Axis.X);

	int getStepW();

	static Direction4 as(Direction direction) {
		return (Direction4) (Object) direction;
	}

	static Direction getApproximateNearest(double dx, double dy, double dz, double dw) {
		return getApproximateNearest((float) dx, (float) dy, (float) dz, (float) dw);
	}

	static Direction getApproximateNearest(float dx, float dy, float dz, float dw) {
		Direction result = Direction.NORTH;
		float highestDot = Float.MIN_VALUE;

		for (Direction direction : Direction.values()) {
			Vec3i normal = direction.getUnitVec3i();
			float dot = dx * normal.getX()
				      + dy * normal.getY()
				      + dz * normal.getZ()
				      + dw * Vec4i.getW(normal);
			if (dot > highestDot) {
				highestDot = dot;
				result = direction;
			}
		}

		return result;
	}

	static Direction getApproximateNearest(Vec4 vec) {
		return Direction4.getApproximateNearest(vec.x, vec.y, vec.z, vec.w);
	}

	/**
	 * <b>All {@link Direction.Axis}</b> instances implement {@link Direction4.Axis}.
	 * Use {@link Direction4.Axis#as} for type-casting.
	 */
	interface Axis {
		Direction.Axis W = Direction.Axis.valueOf("W");

		int choose(final int x, final int y, final int z, final int w);
		boolean choose(final boolean x, final boolean y, final boolean z, final boolean w);
		double choose(final double x, final double y, final double z, final double w);

		static Direction4.Axis as(Direction.Axis axis) {
			return (Direction4.Axis) (Object) axis;
		}
	}
}
