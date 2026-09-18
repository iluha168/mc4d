package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.api.net.minecraft.core.Direction4;
import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.UpwardsBranchingTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiConsumer;

@Mixin(UpwardsBranchingTrunkPlacer.class)
class UpwardsBranchingTrunkPlacerMixin {
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos placeTrunk(
		BlockPos.MutableBlockPos logPos, int x, int y, int z,
		@Local(argsOnly = true, name = "origin") BlockPos origin
	) {
		return ((BlockPos4.MutableBlockPos) logPos).set(x, y, z, Vec4i.getW(origin));
	}

	@Inject(method = "placeBranch", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;getX()I"
	))
	private void placeBranch_logW(
		WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, int treeHeight, TreeConfiguration config, List<FoliagePlacer.FoliageAttachment> attachments, BlockPos.MutableBlockPos logPos, int currentHeight, Direction branchDir, int branchPos, int branchSteps, CallbackInfo ci,
		@Share("logW") LocalIntRef logW
	) {
		logW.set(Vec4i.getW(logPos));
	}
	@Inject(method = "placeBranch", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;getStepX()I"
	))
	private void placeBranch_getStepW(
		WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, int treeHeight, TreeConfiguration config, List<FoliagePlacer.FoliageAttachment> attachments, BlockPos.MutableBlockPos logPos, int currentHeight, Direction branchDir, int branchPos, int branchSteps, CallbackInfo ci,
	    @Share("logW") LocalIntRef logW
	) {
		logW.set(logW.get() + Direction4.as(branchDir).getStepW());
	}
	@Redirect(method = "placeBranch", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private BlockPos.MutableBlockPos placeBranch_logPos(BlockPos.MutableBlockPos logPos, int x, int y, int z, @Share("logW") LocalIntRef logW) {
		return ((BlockPos4.MutableBlockPos) logPos).set(x, y, z, logW.get());
	}
	@ModifyExpressionValue(method = "placeBranch", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	private BlockPos placeBranch_foliagePos(BlockPos original, @Share("logW") LocalIntRef logW) {
		Vec4i.setW(original, logW.get());
		return original;
	}
}
