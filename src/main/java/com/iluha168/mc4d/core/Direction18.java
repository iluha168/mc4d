package com.iluha168.mc4d.core;

import net.minecraft.core.Direction8;

/**
 * Implemented by {@link net.minecraft.core.Direction8}.
 */
public interface Direction18 {
	static Direction18 as(Direction8 direction) {
		return (Direction18) (Object) direction;
	}

	Direction8 KATA = Direction8.valueOf("KATA");
	Direction8 KATA_NORTH = Direction8.valueOf("KATA_NORTH");
	Direction8 KATA_EAST = Direction8.valueOf("KATA_EAST");
	Direction8 KATA_SOUTH = Direction8.valueOf("KATA_SOUTH");
	Direction8 KATA_WEST = Direction8.valueOf("KATA_WEST");
	Direction8 ANA = Direction8.valueOf("ANA");
	Direction8 ANA_NORTH = Direction8.valueOf("ANA_NORTH");
	Direction8 ANA_EAST = Direction8.valueOf("ANA_EAST");
	Direction8 ANA_SOUTH = Direction8.valueOf("ANA_SOUTH");
	Direction8 ANA_WEST = Direction8.valueOf("ANA_WEST");

	int getStepW();
}
