package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.google.common.collect.Sets;
import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Direction4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import com.iluha168.mc4d.world.level.biome.BiomeSource4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mojang.datafixers.util.Pair;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(BiomeSource.class)
abstract class BiomeSourceMixin implements BiomeSource4, BiomeResolver4 {
	@Overwrite
	@Deprecated
	public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int r, Climate.Sampler sampler) {
		throw Err4.arguments3("BiomeSource4#getBiomesWithin(");
	}
	@Override
	public Set<Holder<Biome>> getBiomesWithin(int x, int y, int z, int w, int r, Climate.Sampler sampler) {
		final int x0 = QuartPos.fromBlock(x - r);
		final int y0 = QuartPos.fromBlock(y - r);
		final int z0 = QuartPos.fromBlock(z - r);
		final int w0 = QuartPos.fromBlock(w - r);
		final int x1 = QuartPos.fromBlock(x + r);
		final int y1 = QuartPos.fromBlock(y + r);
		final int z1 = QuartPos.fromBlock(z + r);
		final int w1 = QuartPos.fromBlock(w + r);
		int i = x1 - x0 + 1;
		int j = y1 - y0 + 1;
		int k = z1 - z0 + 1;
		int l = w1 - w0 + 1;
		Set<Holder<Biome>> biomeSet = Sets.newHashSet();

		for (int row = 0; row < k; row++)
			for (int column = 0; column < i; column++)
				for (int depth = 0; depth < j; depth++)
					for (int trength = 0; trength < l; trength++) {
						int noiseX = x0 + column;
						int noiseY = y0 + depth;
						int noiseZ = z0 + row;
						int noiseW = w0 + trength;
						biomeSet.add(this.getNoiseBiome(noiseX, noiseY, noiseZ, noiseW, sampler));
					}

		return biomeSet;
	}

	@Overwrite
	public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int x, int y, int z, int searchRadius, Predicate<Holder<Biome>> allowed, RandomSource random, Climate.Sampler sampler
	) {
		throw Err4.arguments3("BiomeSource4#findBiomeHorizontal");
	}
	@Override
	public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int x, int y, int z, int w, int searchRadius, Predicate<Holder<Biome>> allowed, RandomSource random, Climate.Sampler sampler
	) {
		return this.findBiomeHorizontal(x, y, z, w, searchRadius, 1, allowed, random, false, sampler);
	}

	@Redirect(method = "findClosestBiome3d", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;spiralAround(Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;)Ljava/lang/Iterable;"
	))
	Iterable<BlockPos.MutableBlockPos> findClosestBiome3d_spiral(BlockPos center, int radius, Direction firstDirection, Direction secondDirection) {
		return BlockPos4.spiralAround(center, radius, firstDirection, secondDirection, Direction4.ANA);
	}
	@Definition(id = "noiseZ", local = @Local(type = int.class, name = "noiseZ"))
	@Expression("noiseZ = @(?)")
	@Inject(method = "findClosestBiome3d", at = @At("MIXINEXTRAS:EXPRESSION"))
	void findClosestBiome3d_blockW_noiseW(
		BlockPos origin, int searchRadius, int sampleResolutionHorizontal, int sampleResolutionVertical, Predicate<Holder<Biome>> allowed, Climate.Sampler sampler, LevelReader level, CallbackInfoReturnable<Pair<BlockPos, Holder<Biome>>> cir,
		@Share("blockW") LocalIntRef blockW,
		@Share("noiseW") LocalIntRef noiseW,
		@Local(name = "sampleColumn") BlockPos.MutableBlockPos sampleColumn
	) {
		blockW.set(Vec4i.getW(origin) + Vec4i.getW(sampleColumn) * sampleResolutionHorizontal);
		noiseW.set(QuartPos.fromBlock(blockW.get()));
	}
	@Redirect(method = "findClosestBiome3d", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/biome/BiomeSource;getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;"
	))
	Holder<Biome> findClosestBiome3d_getNoiseBiome(
		BiomeSource instance, int noiseX, int noiseY, int noiseZ, Climate.Sampler sampler,
		@Share("noiseW") LocalIntRef noiseW
	) {
		return ((BiomeSource4) instance).getNoiseBiome(noiseX, noiseY, noiseZ, noiseW.get(), sampler);
	}
	@ModifyExpressionValue(method = "findClosestBiome3d", at = @At(
		value = "NEW",
		target = "(III)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos findClosestBiome3d_blockPos(BlockPos original, @Share("blockW") LocalIntRef blockW) {
		Vec4i.setW(original, blockW.get());
		return original;
	}

	@Overwrite
	@Deprecated
	public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int originX, int originY, int originZ,
		int searchRadius,
		int skipSteps,
		Predicate<Holder<Biome>> allowed,
		RandomSource random,
		boolean findClosest,
		Climate.Sampler sampler
	) {
		throw Err4.arguments3("BiomeSource4#findBiomeHorizontal");
	}
	// Warnings are in copied vanilla code, not gonna change it just in case
	@SuppressWarnings({"BooleanVariableAlwaysNegated", "UnnecessaryLocalVariable"})
	@Override
	public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(
		int originX, int originY, int originZ, int originW,
		int searchRadius, int skipSteps, Predicate<Holder<Biome>> allowed, RandomSource random, boolean findClosest, Climate.Sampler sampler
	) {
		int noiseCenterX = QuartPos.fromBlock(originX);
		int noiseCenterZ = QuartPos.fromBlock(originZ);
		int noiseCenterW = QuartPos.fromBlock(originW);
		int noiseRadius = QuartPos.fromBlock(searchRadius);
		int noiseY = QuartPos.fromBlock(originY);
		Pair<BlockPos, Holder<Biome>> result = null;
		int found = 0;
		int startRadius = findClosest ? 0 : noiseRadius;
		int currentRadius = startRadius;

		while (currentRadius <= noiseRadius) {
			for (int z = !SharedConstants.DEBUG_ONLY_GENERATE_HALF_THE_WORLD && !SharedConstants.debugGenerateSquareTerrainWithoutNoise ? -currentRadius : 0;
			     z <= currentRadius;
			     z += skipSteps
			) {
				boolean zEdge = Math.abs(z) == currentRadius;

				for (int w = -currentRadius; w <= currentRadius; w += skipSteps) {
					boolean wEdge = Math.abs(w) == currentRadius;

					for (int x = -currentRadius; x <= currentRadius; x += skipSteps) {
						if (findClosest) {
							boolean xEdge = Math.abs(x) == currentRadius;
							if (!xEdge && !zEdge && !wEdge) {
								continue;
							}
						}

						int noiseX = noiseCenterX + x;
						int noiseZ = noiseCenterZ + z;
						int noiseW = noiseCenterW + w;
						Holder<Biome> biome = this.getNoiseBiome(noiseX, noiseY, noiseZ, noiseW, sampler);
						if (allowed.test(biome)) {
							if (result == null || random.nextInt(found + 1) == 0) {
								BlockPos resultPos = BlockPos4.from(QuartPos.toBlock(noiseX), originY, QuartPos.toBlock(noiseZ), QuartPos.toBlock(noiseW));
								if (findClosest) {
									return Pair.of(resultPos, biome);
								}

								result = Pair.of(resultPos, biome);
							}

							found++;
						}
					}
				}
			}

			currentRadius += skipSteps;
		}

		return result;
	}

	@Override
	public abstract Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ, final int quartW, final Climate.Sampler sampler);
}
