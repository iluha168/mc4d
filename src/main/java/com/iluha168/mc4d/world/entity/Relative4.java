package com.iluha168.mc4d.world.entity;

import net.minecraft.world.entity.Relative;

import java.util.EnumSet;
import java.util.Set;

/**
 * {@link Relative} implements this interface.
 */
public interface Relative4 {
	Relative W = Relative.valueOf("W");
	Relative DELTA_W = Relative.valueOf("DELTA_W");

	static Set<Relative> position(boolean relativeX, boolean relativeY, boolean relativeZ, boolean relativeW) {
		Set<Relative> relatives = EnumSet.noneOf(Relative.class);
		if (relativeX) relatives.add(Relative.X);
		if (relativeY) relatives.add(Relative.Y);
		if (relativeZ) relatives.add(Relative.Z);
		if (relativeW) relatives.add(Relative4.W);
		return relatives;
	}

	static Set<Relative> direction(boolean relativeX, boolean relativeY, boolean relativeZ, boolean relativeW) {
		Set<Relative> relatives = EnumSet.noneOf(Relative.class);
		if (relativeX) relatives.add(Relative.DELTA_X);
		if (relativeY) relatives.add(Relative.DELTA_Y);
		if (relativeZ) relatives.add(Relative.DELTA_Z);
		if (relativeW) relatives.add(Relative4.DELTA_W);
		return relatives;
	}
}
