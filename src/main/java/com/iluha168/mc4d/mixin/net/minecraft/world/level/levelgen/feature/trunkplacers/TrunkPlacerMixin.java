package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.feature.trunkplacers;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.BiConsumer;

@Mixin(TrunkPlacer.class)
abstract class TrunkPlacerMixin {
	@Shadow
	protected abstract void placeLogIfFree(WorldGenLevel level, BiConsumer<BlockPos, BlockState> trunkSetter, RandomSource random, BlockPos.MutableBlockPos pos, TreeConfiguration config);
}
