package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.BiomeSource4;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(FixedBiomeSource.class)
abstract class FixedBiomeSourceMixin implements BiomeSource4 {
	@Shadow
	@Final
	private Holder<Biome> biome;

	@Overwrite
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
		throw Err4.arguments3("BiomeResolver4#getNoiseBiome");
	}
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, int quartW, Climate.Sampler sampler) {
		return this.biome;
	}

	@Overwrite
	@Deprecated
	public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int originX, int originY, int originZ,
		int r,
		int skipStep,
		Predicate<Holder<Biome>> allowed,
		RandomSource random,
		boolean findClosest,
		Climate.Sampler sampler
	) {
		throw Err4.arguments3("BiomeSource4#findBiomeHorizontal");
	}
	@Override
	public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int originX, int originY, int originZ, int originW,
		int r,
		int skipStep,
		Predicate<Holder<Biome>> allowed,
		RandomSource random,
		boolean findClosest,
		Climate.Sampler sampler
	) {
		if (allowed.test(this.biome)) {
			return findClosest
				? Pair.of(BlockPos4.from(originX, originY, originZ, originW), this.biome)
				: Pair.of(BlockPos4.from(
					originX - r + random.nextInt(r * 2 + 1),
					originY,
					originZ - r + random.nextInt(r * 2 + 1),
					originW - r + random.nextInt(r * 2 + 1)
				), this.biome);
		}
		return null;
	}

	@Overwrite
	@Deprecated
	public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int r, Climate.Sampler sampler) {
		throw Err4.arguments3("BiomeSource4#getBiomesWithin");
	}
	@Override
	public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int w, int r, Climate.Sampler sampler) {
		return Sets.newHashSet(Set.of(this.biome));
	}
}
