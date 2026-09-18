package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MegaPineFoliagePlacer.class)
class MegaPineFoliagePlacerMixin extends FoliagePlacerMixin {
	@Definition(id = "BlockPos", type = BlockPos.class)
	@Expression("new BlockPos(?, ?, ?)")
	@ModifyExpressionValue(method = "createFoliage", at = @At("MIXINEXTRAS:EXPRESSION"))
	BlockPos createFoliage(BlockPos original, @Local(name = "foliagePos") BlockPos foliagePos) {
		Vec4i.setW(original, Vec4i.getW(foliagePos));
		return original;
	}

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
