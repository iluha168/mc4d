package com.iluha168.mc4d.api.net.minecraft.world.level.levelgen.feature.foliageplacers;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;

/**
 * Implemented by {@link FoliagePlacer}.
 */
public interface FoliagePlacer4 {
	boolean shouldSkipLocation(
		final RandomSource random,
		final int dx, final int y, final int dz, final int dw,
		final int currentRadius, final boolean doubleTrunk
	);

	boolean shouldSkipLocationSigned(
		RandomSource random,
		int dx, int y, int dz, int dw,
		int currentRadius, boolean doubleTrunk
	);
}
