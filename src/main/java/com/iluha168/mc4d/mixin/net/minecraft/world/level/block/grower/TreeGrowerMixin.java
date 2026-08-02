package com.iluha168.mc4d.mixin.net.minecraft.world.level.block.grower;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.block.grower.TreeGrower4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeGrower.class)
class TreeGrowerMixin implements TreeGrower4 {
	@Definition(id = "dz", local = @Local(type = int.class, name = "dz"))
	@Expression("dz = @(0)")
	@Inject(method = "growTree", at = @At("MIXINEXTRAS:EXPRESSION"))
	private void growTree_w(
		ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random,
		CallbackInfoReturnable<Boolean> cir, @Share("dw") LocalIntRef dw
	) {
		dw.set(0);
	}
	// This does apply properly, IDE is lying.
	@Definition(id = "dz", local = @Local(type = int.class, name = "dz"))
	@Expression("dz = dz + @(-1)")
	@ModifyExpressionValue(method = "growTree", at = @At("MIXINEXTRAS:EXPRESSION"))
	private int growTree_decrementW(int minusOne, @Share("dw") LocalIntRef dw) {
		final int dwDec = dw.get() - 1;
		if (dwDec >= -1) {
			dw.set(dwDec);
			return 0;
		}
		dw.set(0);
		return minusOne;
	}
	@Redirect(method = "growTree", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/grower/TreeGrower;isTwoByTwoSapling(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;II)Z"
	))
	private boolean growTree_isTwoByTwoByTwoSapling(BlockState state, BlockGetter level, BlockPos pos, int ox, int oz, @Share("dw") LocalIntRef dw) {
		return TreeGrower4.isTwoByTwoByTwoSapling(state, level, pos, ox, oz, dw.get());
	}
	@Redirect(method = "growTree", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos growTree_offsetW(BlockPos pos, int x, int y, int z, @Share("dw") LocalIntRef dw) {
		return ((BlockPos4) pos).offset(x, y, z, dw.get());
	}
	@WrapOperation(method = "growTree", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
	), slice = @Slice(to = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/block/grower/TreeGrower;hasFlowers(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)Z"
	)))
	private boolean growTree_setBlockAna(ServerLevel level, BlockPos pos, BlockState blockState, int updateFlags, Operation<Boolean> original) {
		final boolean set    = original.call(level, pos, blockState, updateFlags);
		final boolean setAna = original.call(level, ((BlockPos4) pos).ana(), blockState, updateFlags);
		// No short circuit allowed, setBlock side effect must be run. We are just returning a value here to comply with the signature.
		return set && setAna;
	}

	@Overwrite
	@Deprecated
	private static boolean isTwoByTwoSapling(BlockState state, BlockGetter level, BlockPos pos, int ox, int oz) {
		throw Err4.arguments2("TreeGrower4#isTwoByTwoByTwoSapling");
	}

	@ModifyExpressionValue(method = "hasFlowers", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;west(I)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos hasFlowers_kata(BlockPos from) {
		return ((BlockPos4) from).kata(2);
	}
	@ModifyExpressionValue(method = "hasFlowers", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;east(I)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos hasFlowers_ana(BlockPos to) {
		return ((BlockPos4) to).ana(2);
	}
}
