package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.iluha168.mc4d.util.Err4;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DarkOakFoliagePlacer.class)
class DarkOakFoliagePlacerMixin extends FoliagePlacerMixin {
	@SuppressWarnings({"RedundantMethodOverride", "deprecation"})
	@Overwrite
	@Deprecated
	protected boolean shouldSkipLocationSigned(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		throw Err4.arguments3("FoliagePlacer4#shouldSkipLocationSigned");
	}
	@Override
	public boolean shouldSkipLocationSigned(RandomSource random, int dx, int y, int dz, int dw, int currentRadius, boolean doubleTrunk) {
		return y == 0 && doubleTrunk
			&& (dx == -currentRadius || dx >= currentRadius)
			&& (dz == -currentRadius || dz >= currentRadius)
			&& (dw == -currentRadius || dw >= currentRadius)
			|| super.shouldSkipLocationSigned(random, dx, y, dz, dw, currentRadius, doubleTrunk);
	}

	@Overwrite
	@Deprecated
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		throw Err4.arguments3("FoliagePlacer4#shouldSkipLocation");
	}
	@Override
	public boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int dw, int currentRadius, boolean doubleTrunk) {
		if (y == -1 && !doubleTrunk) {
			return dx == currentRadius && dz == currentRadius && dw == currentRadius;
		}
		return y == 1 && dx + dz + dw > currentRadius * 3 - 2;
	}
}
