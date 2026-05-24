package com.iluha168.mc4d.core;

import net.minecraft.core.Direction;

public interface Direction4 {
	Direction KATA = Direction.valueOf("KATA");
	Direction ANA = Direction.valueOf("ANA");

	int getStepW();

	static Direction4 as(Direction direction) {
		return (Direction4) (Object) direction;
	}

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
