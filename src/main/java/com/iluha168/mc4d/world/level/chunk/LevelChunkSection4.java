package com.iluha168.mc4d.world.level.chunk;

import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * All {@link net.minecraft.world.level.chunk.LevelChunkSection} instances implement {@link LevelChunkSection4}.
 */
public interface LevelChunkSection4 {
	BlockState getBlockState(int sectionX, int sectionY, int sectionZ, int sectionW);
	FluidState getFluidState(int sectionX, int sectionY, int sectionZ, int sectionW);

	default BlockState setBlockState(int sectionX, int sectionY, int sectionZ, int sectionW, BlockState state) {
		return this.setBlockState(sectionX, sectionY, sectionZ, sectionW, state, true);
	}
	BlockState setBlockState(int sectionX, int sectionY, int sectionZ, int sectionW, BlockState state, boolean checkThreading);

	Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, int quartW);
	void fillBiomesFromNoise(BiomeResolver4 biomeResolver, Climate.Sampler sampler, int quartMinX, int quartMinY, int quartMinZ, int quartMinW);
}
