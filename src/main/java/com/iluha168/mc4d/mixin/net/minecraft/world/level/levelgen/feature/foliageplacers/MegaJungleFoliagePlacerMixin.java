package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.iluha168.mc4d.util.Err4;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MegaJungleFoliagePlacer.class)
class MegaJungleFoliagePlacerMixin extends FoliagePlacerMixin {
	@Overwrite
	@Deprecated
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		throw Err4.arguments3("FoliagePlacer4#shouldSkipLocation");
	}
	@Override
	public boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int dw, int currentRadius, boolean doubleTrunk) {
		return dx + dz + dw >= LeavesBlock.DECAY_DISTANCE || dx * dx + dz * dz + dw * dw > currentRadius * currentRadius;
	}
}
