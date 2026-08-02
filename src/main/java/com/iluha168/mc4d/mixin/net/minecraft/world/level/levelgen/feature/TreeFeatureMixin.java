package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature;

import com.iluha168.mc4d.core.BlockPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TreeFeature.class)
class TreeFeatureMixin {
	// TODO doPlace

	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Definition(id = "r", local = @Local(type = int.class, name = "r"))
	@Expression("z = @(-r)")
	@ModifyExpressionValue(method = "getMaxFreeTreeHeight", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int getMaxFreeTreeHeight_w(int minusR, @Share("w") LocalIntRef w) {
		w.set(minusR);
		return minusR;
	}
	@Definition(id = "z", local = @Local(type = int.class, name = "z"))
	@Expression("z = z + @(1)")
	@ModifyExpressionValue(method = "getMaxFreeTreeHeight", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int getMaxFreeTreeHeight_incrementW(int one, @Share("w") LocalIntRef w, @Local(name = "r") int r) {
		final int wInc = w.get() + 1;
		if (wInc <= r) {
			w.set(wInc);
			return 0;
		}
		w.set(-r);
		return 1;
	}
	@Redirect(method = "getMaxFreeTreeHeight", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos getMaxFreeTreeHeight_posW(BlockPos.MutableBlockPos blockPos, Vec3i pos, int x, int y, int z, @Share("w") LocalIntRef w) {
		return ((BlockPos4.MutableBlockPos) blockPos).setWithOffset(pos, x, y, z, w.get());
	}

	// TODO place
	// TODO updateLeaves
}
