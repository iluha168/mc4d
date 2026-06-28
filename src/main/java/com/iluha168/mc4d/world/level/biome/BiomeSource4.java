package com.iluha168.mc4d.world.level.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Implemented by {@link net.minecraft.world.level.biome.BiomeSource}
 */
public interface BiomeSource4 extends BiomeResolver4 {
	Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int w, int r, Climate.Sampler sampler);

	@Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int x, int y, int z, int w,
		int searchRadius, Predicate<Holder<Biome>> allowed, RandomSource random, Climate.Sampler sampler
	);

	@Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int originX, int originY, int originZ, int originW,
		int searchRadius,
		int skipSteps,
		Predicate<Holder<Biome>> allowed,
		RandomSource random,
		boolean findClosest,
		Climate.Sampler sampler
	);
}
