package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.iluha168.mc4d.util.Err4;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AcaciaFoliagePlacer.class)
class AcaciaFoliagePlacerMixin extends FoliagePlacerMixin {
	@Overwrite
	@Deprecated
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		throw Err4.arguments3("FoliagePlacer4#shouldSkipLocation");
	}
	@Override
	public boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int dw, int currentRadius, boolean doubleTrunk) {
		return y == 0 ? (dx > 1 || dz > 1 || dw > 1) && dx != 0 && dz != 0 && dw != 0 : dx == currentRadius && dz == currentRadius && dw == currentRadius && currentRadius > 0;
	}
}
