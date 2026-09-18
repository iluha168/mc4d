package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.iluha168.mc4d.util.Err4;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CherryFoliagePlacer.class)
class CherryFoliagePlacerMixin extends FoliagePlacerMixin {
	@Shadow
	@Final
	private float wideBottomLayerHoleChance;

	@Shadow
	@Final
	private float cornerHoleChance;

	@Overwrite
	@Deprecated
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		throw Err4.arguments3("FoliagePlacer4#shouldSkipLocation");
	}
	@Override
	public boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int dw, int currentRadius, boolean doubleTrunk) {
		if (y == -1 && (dx == currentRadius || dz == currentRadius || dw == currentRadius) && random.nextFloat() < this.wideBottomLayerHoleChance) {
			return true;
		}
		boolean corner = dx == currentRadius && dz == currentRadius && dw == currentRadius;
		return currentRadius > 2
			? corner || dx + dz + dw > currentRadius * 3 - 2 && random.nextFloat() < this.cornerHoleChance
			: corner && random.nextFloat() < this.cornerHoleChance;
	}
}
