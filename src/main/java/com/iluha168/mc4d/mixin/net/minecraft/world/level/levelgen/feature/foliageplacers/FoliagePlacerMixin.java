package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Direction4;
import com.iluha168.mc4d.api.net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(FoliagePlacer.class)
abstract class FoliagePlacerMixin implements FoliagePlacer4 {
	@Shadow
	private static boolean tryPlaceExtension(WorldGenLevel level, FoliagePlacer.FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, float chance, BlockPos logPos, BlockPos.MutableBlockPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Overwrite
	@Deprecated
	protected boolean shouldSkipLocationSigned(RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk) {
		throw Err4.arguments3("FoliagePlacer4#shouldSkipLocationSigned");
	}
	@Override
	public boolean shouldSkipLocationSigned(RandomSource random, int dx, int y, int dz, int dw, int currentRadius, boolean doubleTrunk) {
		int minDx;
		int minDz;
		int minDw;
		if (doubleTrunk) {
			minDx = Math.min(Math.abs(dx), Math.abs(dx - 1));
			minDz = Math.min(Math.abs(dz), Math.abs(dz - 1));
			minDw = Math.min(Math.abs(dw), Math.abs(dw - 1));
		} else {
			minDx = Math.abs(dx);
			minDz = Math.abs(dz);
			minDw = Math.abs(dw);
		}

		return this.shouldSkipLocation(random, minDx, y, minDz, minDw, currentRadius, doubleTrunk);
	}

	@Definition(id = "dx", local = @Local(type = int.class, name = "dx"))
	@Definition(id = "currentRadius", local = @Local(type = int.class, name = "currentRadius", argsOnly = true))
	@Expression("dx = @(-currentRadius)")
	@Inject(method = "placeLeavesRow", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void placeLeavesRow_dw(
		WorldGenLevel level, FoliagePlacer.FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config,
		BlockPos origin, int currentRadius, int y, boolean doubleTrunk, CallbackInfo ci, @Share("dw") LocalIntRef dw
	) {
		dw.set(-currentRadius);
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "dx", local = @Local(type = int.class, name = "dx"))
	@Expression("dx = dx + @(1)")
	@ModifyExpressionValue(method = "placeLeavesRow", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int placeLeavesRow_incrementDw(int one, @Local(name = "currentRadius", argsOnly = true) int currentRadius, @Share("dw") LocalIntRef dw) {
		dw.set(dw.get() + 1);
		if (dw.get() <= currentRadius) return 0;
		dw.set(-currentRadius);
		return 1;
	}
	@Redirect(method = "placeLeavesRow", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/feature/foliageplacers/FoliagePlacer;shouldSkipLocationSigned(Lnet/minecraft/util/RandomSource;IIIIZ)Z"
	))
	boolean placeLeavesRow_shouldSkipLocationSigned(FoliagePlacer instance, RandomSource random, int dx, int y, int dz, int currentRadius, boolean doubleTrunk, @Share("dw") LocalIntRef dw) {
		return ((FoliagePlacer4) instance).shouldSkipLocationSigned(random, dx, y, dz, dw.get(), currentRadius, doubleTrunk);
	}
	@Redirect(method = "placeLeavesRow", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	BlockPos.MutableBlockPos placeLeavesRow_setWithOffset(BlockPos.MutableBlockPos instance, Vec3i pos, int x, int y, int z, @Share("dw") LocalIntRef dw) {
		return ((BlockPos4.MutableBlockPos) instance).setWithOffset(pos, x, y, z, dw.get());
	}

	@Inject(method = "placeLeavesRowWithHangingLeavesBelow", cancellable = true, at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction$Plane;iterator()Ljava/util/Iterator;"
	))
	void placeLeavesRowWithHangingLeavesBelow(
		WorldGenLevel level, FoliagePlacer.FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, BlockPos origin, int currentRadius, int y, boolean doubleTrunk, float hangingLeavesChance, float hangingLeavesExtensionChance, CallbackInfo ci,
		@Local(name = "offset") final int offset,
		@Local(name = "logPos") final BlockPos logPos,
		@Local(name = "pos") final BlockPos.MutableBlockPos pos
	) {
		ci.cancel();
		final BlockPos4.MutableBlockPos pos4 = (BlockPos4.MutableBlockPos) pos;

		for (final Direction alongEdge : Direction.Plane.HORIZONTAL) {
			// This is 2 directions in 4D.
			final Direction[] toEdge = Arrays
				.stream(Direction4.as(alongEdge).getHorizontalPerpendiculars())
				.filter(direction -> direction.getAxisDirection() == Direction.AxisDirection.POSITIVE)
				.toArray(Direction[]::new);
			final int offsetToEdge = alongEdge.getAxisDirection() == Direction.AxisDirection.POSITIVE ? currentRadius + offset : currentRadius;

			for (int offsetAlongEdge0 = -currentRadius; offsetAlongEdge0 < currentRadius + offset; offsetAlongEdge0++)
				for (int offsetAlongEdge1 = -currentRadius; offsetAlongEdge1 < currentRadius + offset; offsetAlongEdge1++) {
					pos4.setWithOffset(origin, 0, y - 1, 0, 0)
						.move(alongEdge, offsetToEdge)
						.move(toEdge[0], offsetAlongEdge0)
						.move(toEdge[1], offsetAlongEdge1);

					final boolean leavesAbove = foliageSetter.isSet(pos.move(Direction.UP));
					pos.move(Direction.DOWN);
					//noinspection ConstantValue
					if (leavesAbove && tryPlaceExtension(level, foliageSetter, random, config, hangingLeavesChance, logPos, pos)) {
						pos.move(Direction.DOWN);
						tryPlaceExtension(level, foliageSetter, random, config, hangingLeavesExtensionChance, logPos, pos);
					}
				}
		}
	}
}
