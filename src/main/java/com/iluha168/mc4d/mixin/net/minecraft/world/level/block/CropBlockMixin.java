package com.iluha168.mc4d.mixin.net.minecraft.world.level.block;

import com.iluha168.mc4d.core.BlockPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
class CropBlockMixin {
	@Definition(id = "zz", local = @Local(type = int.class, name = "zz"))
	@Expression("zz = @(-1)")
	@Inject(method = "getGrowthSpeed", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void getGrowthSpeed_w(BlockState cropBlockState, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir, @Share("ww") LocalIntRef ww) {
		ww.set(-1);
	}
	@Definition(id = "zz", local = @Local(type = int.class, name = "zz"))
	@Expression("zz = zz + @(1)")
	@ModifyExpressionValue(method = "getGrowthSpeed", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static int getGrowthSpeed_incrementW(int one, @Share("ww") LocalIntRef ww) {
		final int wwInc = ww.get() + 1;
		if (wwInc <= 1) {
			ww.set(wwInc);
			return 0;
		}
		ww.set(-1);
		return one;
	}
	@Redirect(method = "getGrowthSpeed", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private static BlockPos getGrowthSpeed_offsetW(BlockPos pos, int x, int y, int z, @Share("ww") LocalIntRef ww) {
		return ((BlockPos4) pos).offset(x, y, z, ww.get());
	}
	@Definition(id = "zz", local = @Local(type = int.class, name = "zz"))
	@Expression("zz != 0")
	@ModifyExpressionValue(method = "getGrowthSpeed", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean getGrowthSpeed_isNeighbourW(boolean original, @Share("ww") LocalIntRef ww) {
		return original || ww.get() != 0;
	}
	@ModifyConstant(method = "getGrowthSpeed", constant = @Constant(floatValue = 4.0F))
	private static float getGrowthSpeed_soilNeighbourDivisor(float divisor) {
		// Keep the max growth speed at vanilla's.
		return divisor * (3F*3F*3F-1F) / (3F*3F-1F);
	}
	@Definition(id = "vertical", local = @Local(type = boolean.class, name = "vertical"))
	@Expression("vertical = ?")
	@Inject(method = "getGrowthSpeed", at = @At("MIXINEXTRAS:EXPRESSION"), cancellable = true)
	private static void getGrowthSpeed_crowded(BlockState cropBlockState, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir, @Local(name = "speed") float speed) {
		final Block type = cropBlockState.getBlock();
		final BlockPos4 pos4 = (BlockPos4) pos;
		int axes = 0;
		// A crop keeps full speed only while every same-crop neighbor lies on one single axis.
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				for (int w = -1; w <= 1; w++) {
					if (x == 0 && z == 0 && w == 0) continue;
					if (!level.getBlockState(pos4.offset(x, 0, z, w)).is(type)) continue;
					if (Math.abs(x) + Math.abs(z) + Math.abs(w) > 1) {
						// A diagonal neighbor. Immediately disqualified from row buff.
						cir.setReturnValue(speed / 4F);
						return;
					}
					axes |= x != 0 ? 0b001 : z != 0 ? 0b010 : 0b100;
				}
			}
		}
		// Why 4F? I think vanilla balances around a full field yielding the same amount as a row-pattern one.
		// In 4D there is more space, so 2F from vanilla would be wrong.
		cir.setReturnValue(Integer.bitCount(axes) > 1 ? speed / 4F : speed);
	}
}
