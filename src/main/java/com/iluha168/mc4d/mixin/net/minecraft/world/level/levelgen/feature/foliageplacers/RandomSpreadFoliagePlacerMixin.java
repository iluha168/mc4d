package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RandomSpreadFoliagePlacer.class)
class RandomSpreadFoliagePlacerMixin extends FoliagePlacerMixin {
	@Redirect(method = "createFoliage", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos createFoliage(
		BlockPos.MutableBlockPos instance, Vec3i pos, int x, int y, int z,
		@Local(argsOnly = true, name = "random") RandomSource random,
		@Local(argsOnly = true, name = "leafRadius") int leafRadius
	) {
		return ((BlockPos4.MutableBlockPos) instance).setWithOffset(pos, x, y, z, random.nextInt(leafRadius) - random.nextInt(leafRadius));
	}

	@Overwrite
	@Deprecated
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		throw Err4.arguments3("FoliagePlacer4#shouldSkipLocation");
	}
	@Override
	public boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int dw, int currentRadius, boolean doubleTrunk) {
		return false;
	}
}
