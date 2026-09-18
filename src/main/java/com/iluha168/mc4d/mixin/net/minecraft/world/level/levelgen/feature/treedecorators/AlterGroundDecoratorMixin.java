package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.treedecorators;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.neoforged.neoforge.event.level.AlterGroundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AlterGroundDecorator.class)
class AlterGroundDecoratorMixin {
	@Definition(id = "placeCircle", method = "Lnet/minecraft/world/level/levelgen/feature/treedecorators/AlterGroundDecorator;placeCircle(Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;Lnet/minecraft/core/BlockPos;Lnet/neoforged/neoforge/event/level/AlterGroundEvent$StateProvider;)V")
	@Definition(id = "north", method = "Lnet/minecraft/core/BlockPos;north()Lnet/minecraft/core/BlockPos;")
	@Definition(id = "south", method = "Lnet/minecraft/core/BlockPos;south(I)Lnet/minecraft/core/BlockPos;")
	@Expression({
		"this.placeCircle(?, ?.north(), ?)",
		"this.placeCircle(?, ?.south(?), ?)"
	})
	@WrapOperation(method = "lambda$place$1", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void place_placeCircle(AlterGroundDecorator self, TreeDecorator.Context context, BlockPos pos, AlterGroundEvent.StateProvider eventProvider, Operation<Void> original) {
		final BlockPos4 pos4 = (BlockPos4) pos;
		original.call(self, context, pos4.kata(), eventProvider);
		original.call(self, context, pos4.ana(2), eventProvider);
	}
	@ModifyConstant(method = "lambda$place$1", constant = @Constant(intValue = 5))
	private int place_tries(int tries) {
		return tries * 8; // Preserving density.
	}
	@ModifyConstant(method = "lambda$place$1", constant = @Constant(intValue = 8 * 8))
	private int place_nextInt(int cells) {
		return cells * 8;
	}
	@Definition(id = "placement", local = @Local(type = int.class, name = "placement"))
	@Expression("placement / 8")
	@ModifyExpressionValue(method = "lambda$place$1", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int place_ww(int zz, @Local(name = "placement") int placement, @Share("ww") LocalIntRef ww) {
		ww.set(placement / (8 * 8));
		return zz % 8;
	}
	@Definition(id = "zz", local = @Local(type = int.class, name = "zz"))
	@Expression("zz == 7")
	@ModifyExpressionValue(method = "lambda$place$1", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean place_if(boolean original, @Share("ww") LocalIntRef ww) {
		return original || ww.get() == 0 || ww.get() == 7;
	}
	@Redirect(method = "lambda$place$1", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos place_offset(BlockPos instance, int x, int y, int z, @Share("ww") LocalIntRef ww) {
		return ((BlockPos4) instance).offset(x, y, z, -3 + ww.get());
	}

	@Definition(id = "zz", local = @Local(type = int.class, name = "zz"))
	@Expression("zz = @(-2)")
	@Inject(method = "placeCircle(Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;Lnet/minecraft/core/BlockPos;Lnet/neoforged/neoforge/event/level/AlterGroundEvent$StateProvider;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void placeCircle_ww(TreeDecorator.Context context, BlockPos pos, AlterGroundEvent.StateProvider eventProvider, CallbackInfo ci, @Share("ww") LocalIntRef ww) {
		ww.set(-2);
	}
	@Definition(id = "zz", local = @Local(type = int.class, name = "zz"))
	@Expression("zz = zz + @(1)")
	@ModifyExpressionValue(method = "placeCircle(Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;Lnet/minecraft/core/BlockPos;Lnet/neoforged/neoforge/event/level/AlterGroundEvent$StateProvider;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int placeCircle_incrementWw(int one, @Share("ww") LocalIntRef ww) {
		final int wwInc = ww.get() + 1;
		if (wwInc <= 2) {
			ww.set(wwInc);
			return 0;
		}
		ww.set(-2);
		return 1;
	}
	@Expression("? != 2")
	@ModifyExpressionValue(method = "placeCircle(Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;Lnet/minecraft/core/BlockPos;Lnet/neoforged/neoforge/event/level/AlterGroundEvent$StateProvider;)V", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
	private boolean placeCircle_if(boolean original, @Share("ww") LocalIntRef ww) {
		return original || Math.abs(ww.get()) != 2;
	}
	@Redirect(method = "placeCircle(Lnet/minecraft/world/level/levelgen/feature/treedecorators/TreeDecorator$Context;Lnet/minecraft/core/BlockPos;Lnet/neoforged/neoforge/event/level/AlterGroundEvent$StateProvider;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos placeCircle_offset(BlockPos instance, int x, int y, int z, @Share("ww") LocalIntRef ww) {
		return ((BlockPos4) instance).offset(x, y, z, ww.get());
	}
}
