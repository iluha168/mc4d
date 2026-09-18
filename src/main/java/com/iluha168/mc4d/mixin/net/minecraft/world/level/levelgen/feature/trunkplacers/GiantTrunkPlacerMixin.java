package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.iluha168.mc4d.api.net.minecraft.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BiConsumer;

@Mixin(GiantTrunkPlacer.class)
abstract class GiantTrunkPlacerMixin extends TrunkPlacerMixin {
	@WrapOperation(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/feature/trunkplacers/GiantTrunkPlacer;placeBelowTrunkBlock(Lnet/minecraft/world/level/WorldGenLevel;Ljava/util/function/BiConsumer;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;)V"
	))
	private static void placeTrunk_placeBelowTrunkBlock(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, BlockPos pos, TreeConfiguration config, Operation<Void> original) {
		original.call(level, trunkSetter, random, pos, config);
		original.call(level, trunkSetter, random, ((BlockPos4) pos).ana(), config);
	}
	@Redirect(method = "placeTrunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/feature/trunkplacers/GiantTrunkPlacer;placeLogIfFreeWithOffset(Lnet/minecraft/world/level/WorldGenLevel;Ljava/util/function/BiConsumer;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos$MutableBlockPos;Lnet/minecraft/world/level/levelgen/feature/configurations/TreeConfiguration;Lnet/minecraft/core/BlockPos;III)V"
	))
	private void placeTrunk_placeLogIfFreeWithOffset(
		GiantTrunkPlacer This, WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random,
		BlockPos.MutableBlockPos trunkPos, TreeConfiguration config, BlockPos treePos, int x, int y, int z,
		@Local(argsOnly = true, name = "treeHeight") int treeHeight
	) {
		this.placeLogIfFreeWithOffset(level, trunkSetter, random, trunkPos, config, treePos, x, y, z, 0);
		if (y < treeHeight - 1) {
			this.placeLogIfFreeWithOffset(level, trunkSetter, random, trunkPos, config, treePos, x, y, z, 1);
		}
	}

	@Overwrite
	@Deprecated
	private void placeLogIfFreeWithOffset(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, BlockPos.MutableBlockPos trunkPos, TreeConfiguration config, BlockPos treePos, int x, int y, int z) {
		throw Err4.arguments3(null);
	}
	@Unique
	private void placeLogIfFreeWithOffset(
		WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random,
		BlockPos.MutableBlockPos trunkPos, TreeConfiguration config, BlockPos treePos, int x, int y, int z, int w
	) {
		((BlockPos4.MutableBlockPos) trunkPos).setWithOffset(treePos, x, y, z, w);
		this.placeLogIfFree(level, trunkSetter, random, trunkPos, config);
	}
}
