package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import com.iluha168.mc4d.world.level.chunk.LevelChunkSection4;
import com.iluha168.mc4d.world.level.chunk.PalettedContainer4;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunkSection.class)
class LevelChunkSectionMixin implements LevelChunkSection4 {
	@Shadow
	@Final
	private PalettedContainer<BlockState> states;

	@Shadow
	private short nonEmptyBlockCount;

	@Shadow
	private short tickingBlockCount;

	@Shadow
	private short fluidCount;

	@Shadow
	private short tickingFluidCount;

	@Shadow
	private PalettedContainerRO<Holder<Biome>> biomes;

	@Overwrite
	public BlockState getBlockState(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3("LevelChunkSection4#getBlockState");
	}
	@Override
	public BlockState getBlockState(int sectionX, int sectionY, int sectionZ, int sectionW) {
		//noinspection unchecked
		return ((PalettedContainer4<BlockState>) this.states).get(sectionX, sectionY, sectionZ, sectionW);
	}

	@Overwrite
	public FluidState getFluidState(int sectionX, int sectionY, int sectionZ) {
		throw Err4.arguments3("LevelChunkSection4#getFluidState");
	}
	@Override
	public FluidState getFluidState(int sectionX, int sectionY, int sectionZ, int sectionW) {
		//noinspection unchecked
		return ((PalettedContainer4<BlockState>) this.states).get(sectionX, sectionY, sectionZ, sectionW).getFluidState();
	}

	@Overwrite
	public BlockState setBlockState(int sectionX, int sectionY, int sectionZ, BlockState state) {
		throw Err4.arguments3("LevelChunkSection4#setBlockState");
	}

	@Overwrite
	public BlockState setBlockState(int sectionX, int sectionY, int sectionZ, BlockState state, boolean checkThreading) {
		throw Err4.arguments3("LevelChunkSection4#setBlockState");
	}
	@Override
	public BlockState setBlockState(int sectionX, int sectionY, int sectionZ, int sectionW, BlockState state, boolean checkThreading) {
		@SuppressWarnings("unchecked") BlockState previous = checkThreading
			? ((PalettedContainer4<BlockState>) this.states).getAndSet(sectionX, sectionY, sectionZ, sectionW, state)
			: ((PalettedContainer4<BlockState>) this.states).getAndSetUnchecked(sectionX, sectionY, sectionZ, sectionW, state);

		if (!previous.isEmpty()) { // Neo: Fix MC-232360 for modded blocks (Makes modded isAir blocks not be replaced with Blocks.AIR in all-air chunk sections)
			this.nonEmptyBlockCount--;
			if (previous.isRandomlyTicking()) {
				this.tickingBlockCount--;
			}

			FluidState previousFluid = previous.getFluidState();
			if (!previousFluid.isEmpty()) {
				this.fluidCount--;
				if (previousFluid.isRandomlyTicking()) {
					this.tickingFluidCount--;
				}
			}
		}

		if (!state.isEmpty()) { // Neo: Fix MC-232360 for modded blocks (Makes modded isAir blocks not be replaced with Blocks.AIR in all-air chunk sections)
			this.nonEmptyBlockCount++;
			if (state.isRandomlyTicking()) {
				this.tickingBlockCount++;
			}

			FluidState fluid = state.getFluidState();
			if (!fluid.isEmpty()) {
				this.fluidCount++;
				if (fluid.isRandomlyTicking()) {
					this.tickingFluidCount++;
				}
			}
		}

		return previous;
	}

	@Overwrite
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ) {
		throw Err4.arguments3("LevelChunkSection4#getNoiseBiome");
	}
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, int quartW) {
		//noinspection unchecked
		return ((PalettedContainer4<Holder<Biome>>) this.biomes).get(quartX, quartY, quartZ, quartW);
	}

	@Overwrite
	public void fillBiomesFromNoise(BiomeResolver biomeResolver, Climate.Sampler sampler, int quartMinX, int quartMinY, int quartMinZ) {
		throw Err4.arguments3("LevelChunkSection4#fillBiomesFromNoise");
	}
	@Override
	public void fillBiomesFromNoise(BiomeResolver4 biomeResolver, Climate.Sampler sampler, int quartMinX, int quartMinY, int quartMinZ, int quartMinW) {
		PalettedContainer<Holder<Biome>> newBiomes = this.biomes.recreate();
		@SuppressWarnings("unchecked") PalettedContainer4<Holder<Biome>> newBiomes4 = (PalettedContainer4<Holder<Biome>>) newBiomes;
		final int size = 4;
		for (int x = 0; x < size; x++)
			for (int y = 0; y < size; y++)
				for (int z = 0; z < size; z++)
					for (int w = 0; w < size; w++)
						newBiomes4.getAndSetUnchecked(
							x, y, z, w,
							biomeResolver.getNoiseBiome(quartMinX + x, quartMinY + y, quartMinZ + z, quartMinW + w, sampler)
						);
		this.biomes = newBiomes;
	}
}
