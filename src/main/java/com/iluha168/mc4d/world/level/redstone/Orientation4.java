package com.iluha168.mc4d.world.level.redstone;

import com.iluha168.mc4d.core.Vec4i;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * 4D analogue of vanilla {@link Orientation}.
 * {@link #up}, {@link #front} and {@link #side} are three mutually perpendicular axes (freely chosen),</li>
 * {@link #side2} is the fourth perpendicular direction.
 */
// TODO partially vibecoded, needs review from someone who actually understands this
public class Orientation4 extends Orientation {
	private final Direction side2;
	private final Map<Direction, Orientation4> withSide = new EnumMap<>(Direction.class);

	public Orientation4(Direction up, Direction front, Direction side, Orientation.SideBias sideBias) {
		super(up, front, sideBias);
		this.side = side;
		this.index = generateIndex(up, front, side, sideBias);
		Vec3i perpendicular = Vec4i.cross(front.getUnitVec3i(), up.getUnitVec3i(), side.getUnitVec3i());
		Direction side2 = Direction.getNearest(perpendicular, null);
		Objects.requireNonNull(side2);
		this.side2 = this.sideBias == Orientation.SideBias.RIGHT ? side2 : side2.getOpposite();

		this.neighbors = List.of(
			this.front.getOpposite(), this.front,
			this.side, this.side.getOpposite(),
			this.side2, this.side2.getOpposite(),
			this.up.getOpposite(), this.up
		);
		this.horizontalNeighbors = this.neighbors.stream().filter(d -> d.getAxis() != this.up.getAxis()).toList();
		this.verticalNeighbors = this.neighbors.stream().filter(d -> d.getAxis() == this.up.getAxis()).toList();
	}

	public Orientation4 withSide(Direction side) {
		return this.withSide.get(side);
	}

	@Override
	public @NonNull Orientation withFrontAdjustSideBias(@NonNull Direction front) {
		Orientation4 withFront = (Orientation4) this.withFront(front);
		return this.front == withFront.side2 ? withFront.withMirror() : withFront;
	}

	public Direction getSide2() {
		return this.side2;
	}

	@Override
	public @NonNull String toString() {
		return "[up=" + this.up + ",front=" + this.front + ",side=" + this.side + ",sideBias=" + this.sideBias + "]";
	}

	public static @NonNull Orientation4 generateContext(Orientation4 self, Orientation4[] lookup) {
		if (lookup[self.getIndex()] != null) {
			return lookup[self.getIndex()];
		}

		lookup[self.getIndex()] = self;

		for (Orientation.SideBias sideBias : Orientation.SideBias.values()) {
			self.withSideBias.put(sideBias, generateContext(new Orientation4(self.up, self.front, self.side, sideBias), lookup));
		}

		for (Direction facing : Direction.values()) {
			// Rotate `front` onto `facing`. If that axis is already occupied by `up` or `side`,
			// that vector takes over the axis `front` is vacating, keeping the frame orthonormal.
			Direction up = self.up;
			Direction side = self.side;
			if (facing.getAxis() == self.up.getAxis()) {
				up = whenDisplaced(self.front, facing, self.up);
			} else if (facing.getAxis() == self.side.getAxis()) {
				side = whenDisplaced(self.front, facing, self.side);
			}

			self.withFront.put(facing, generateContext(new Orientation4(up, facing, side, self.sideBias), lookup));
		}

		for (Direction facing : Direction.values()) {
			Direction front = self.front;
			Direction side = self.side;
			if (facing.getAxis() == self.front.getAxis()) {
				front = whenDisplaced(self.up, facing, self.front);
			} else if (facing.getAxis() == self.side.getAxis()) {
				side = whenDisplaced(self.up, facing, self.side);
			}

			self.withUp.put(facing, generateContext(new Orientation4(facing, front, side, self.sideBias), lookup));
		}

		for (Direction facing : Direction.values()) {
			Direction up = self.up;
			Direction front = self.front;
			if (facing.getAxis() == self.up.getAxis()) {
				up = whenDisplaced(self.side, facing, self.up);
			} else if (facing.getAxis() == self.front.getAxis()) {
				front = whenDisplaced(self.side, facing, self.front);
			}

			self.withSide.put(facing, generateContext(new Orientation4(up, front, facing, self.sideBias), lookup));
		}

		return self;
	}

	/**
	 * A frame vector currently along {@code moved}'s axis is being rotated onto {@code newDir}, which collides with
	 * the axis held by {@code displaced}. Returns where {@code displaced} must rotate to so the 90° rotation stays
	 * orientation-preserving (the moved vector takes the displaced axis).
	 */
	private static @NonNull Direction whenDisplaced(Direction moved, Direction newDir, Direction displaced) {
		return newDir == displaced ? moved.getOpposite() : moved;
	}

	protected static int generateIndex(Direction up, Direction front, Direction side, Orientation.SideBias sideBias) {
		Direction.Axis upAxis = up.getAxis();
		Direction.Axis frontAxis = front.getAxis();
		Direction.Axis sideAxis = side.getAxis();
		if (upAxis == frontAxis || upAxis == sideAxis || frontAxis == sideAxis) {
			throw new IllegalStateException("Up-, front- and side-vectors must all be on different axes");
		}

		int upKey = up.ordinal();
		int frontKey = axisRank(frontAxis, EnumSet.of(upAxis)) << 1 | front.getAxisDirection().ordinal();
		int sideKey = axisRank(sideAxis, EnumSet.of(upAxis, frontAxis)) << 1 | side.getAxisDirection().ordinal();
		return (((upKey * 6 + frontKey) << 2) + sideKey << 1) + sideBias.ordinal();
	}

	/** Position of {@code axis} within the axes that are not {@code excluded}, in {@link Direction.Axis} order. */
	private static int axisRank(Direction.Axis axis, EnumSet<Direction.Axis> excluded) {
		int rank = 0;
		for (Direction.Axis candidate : Direction.Axis.values()) {
			if (excluded.contains(candidate)) {
				continue;
			}
			if (candidate == axis) {
				return rank;
			}
			rank++;
		}
		throw new IllegalStateException("Unknown axis " + axis);
	}
}
