package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.blending;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import com.iluha168.mc4d.world.level.levelgen.blending.BlendingData4;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.*;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Mixin(BlendingData.class)
class BlendingDataMixin implements BlendingData4 {
	@Shadow
	@Final
	private double[] heights;

	@Shadow
	@Final
	private transient double[][] densities;

	@Shadow
	@Final
	private List<@Nullable List<@Nullable Holder<Biome>>> biomes;

	@Shadow
	@Final
	private LevelHeightAccessor areaWithOldGeneration;

	@Shadow
	@Final
	private static List<Block> SURFACE_BLOCKS;

	@Shadow
	private int cellCountPerColumn() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private static double read7(ChunkAccess chunk, BlockPos.MutableBlockPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private static double read1(ChunkAccess chunk, BlockPos.MutableBlockPos pos) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private int quartCountPerColumn() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private int getCellYIndex(int cellY) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Overwrite
	@Deprecated
	public static @Nullable BlendingData getOrUpdateBlendingData(WorldGenRegion region, int chunkX, int chunkZ) {
		throw Err4.arguments2("BlendingData4#getOrUpdateBlendingData");
	}

	@Overwrite
	@Deprecated
	public static Set<Direction8> sideByGenerationAge(WorldGenLevel region, int chunkX, int chunkZ, boolean wantedOldGen) {
		throw Err4.arguments2("BlendingData4#sideByGenerationAge");
	}

	// TODO calculateData

	@Overwrite
	@Deprecated
	private void addValuesForColumn(int index, ChunkAccess chunk, int blockX, int blockZ) {
		throw Err4.arguments2(null);
	}
	@Unique
	private void addValuesForColumn(int index, ChunkAccess chunk, int blockX, int blockZ, int blockW) {
		if (this.heights[index] == Double.MAX_VALUE) {
			this.heights[index] = this.getHeightAtXZW(chunk, blockX, blockZ, blockW);
		}

		this.densities[index] = this.getDensityColumn(chunk, blockX, blockZ, blockW, Mth.floor(this.heights[index]));
		this.biomes.set(index, this.getBiomeColumn(chunk, blockX, blockZ, blockW));
	}

	@Overwrite
	@Deprecated
	private int getHeightAtXZ(ChunkAccess chunk, int blockX, int blockZ) {
		throw Err4.arguments2(null);
	}
	@Unique
	private int getHeightAtXZW(ChunkAccess chunk, int blockX, int blockZ, int blockW) {
		int height;
		if (chunk.hasPrimedHeightmap(Heightmap.Types.WORLD_SURFACE_WG)) {
			height = Math.min(((ChunkAccess4) chunk).getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockX, blockZ, blockW), this.areaWithOldGeneration.getMaxY());
		} else {
			height = this.areaWithOldGeneration.getMaxY();
		}

		int minY = this.areaWithOldGeneration.getMinY();
		BlockPos.MutableBlockPos pos = BlockPos4.MutableBlockPos.from(blockX, height, blockZ, blockW);

		while (pos.getY() > minY) {
			if (SURFACE_BLOCKS.contains(chunk.getBlockState(pos).getBlock())) {
				return pos.getY();
			}

			pos.move(Direction.DOWN);
		}

		return minY;
	}

	@Overwrite
	@Deprecated
	private double[] getDensityColumn(ChunkAccess chunk, int x, int z, int height) {
		throw Err4.arguments2(null);
	}
	@Unique
	private double[] getDensityColumn(ChunkAccess chunk, int x, int z, int w, int height) {
		double[] densities = new double[this.cellCountPerColumn()];
		Arrays.fill(densities, -1.0);
		BlockPos.MutableBlockPos pos = BlockPos4.MutableBlockPos.from(x, this.areaWithOldGeneration.getMaxY() + 1, z, w);
		double last7 = read7(chunk, pos);

		for (int cellIndex = densities.length - 2; cellIndex >= 0; cellIndex--) {
			double one = read1(chunk, pos);
			double current7 = read7(chunk, pos);
			densities[cellIndex] = (last7 + one + current7) / 15.0;
			last7 = current7;
		}

		int highestCellWithSurfaceIndex = this.getCellYIndex(Mth.floorDiv(height, 8));
		if (highestCellWithSurfaceIndex >= 0 && highestCellWithSurfaceIndex < densities.length - 1) {
			double inCellIndex = (height + 0.5) % 8.0 / 8.0;
			double amplitudeAboveToMakeSurfaceBeAtHeight = (1.0 - inCellIndex) / inCellIndex;
			double max = Math.max(amplitudeAboveToMakeSurfaceBeAtHeight, 1.0) * 0.25;
			densities[highestCellWithSurfaceIndex + 1] = -amplitudeAboveToMakeSurfaceBeAtHeight / max;
			densities[highestCellWithSurfaceIndex] = 1.0 / max;
		}

		return densities;
	}

	@Overwrite
	@Deprecated
	private List<Holder<Biome>> getBiomeColumn(ChunkAccess chunk, int blockX, int blockZ) {
		throw Err4.arguments2(null);
	}
	@Unique
	private List<Holder<Biome>> getBiomeColumn(ChunkAccess chunk, int blockX, int blockZ, int blockW) {
		ObjectArrayList<Holder<Biome>> biomes = new ObjectArrayList<>(this.quartCountPerColumn());
		biomes.size(this.quartCountPerColumn());
		ChunkAccess4 chunk4 = (ChunkAccess4) chunk;

		for (int quartIndex = 0; quartIndex < biomes.size(); quartIndex++) {
			int quartY = quartIndex + QuartPos.fromBlock(this.areaWithOldGeneration.getMinY());
			biomes.set(quartIndex, chunk4.getNoiseBiome(QuartPos.fromBlock(blockX), quartY, QuartPos.fromBlock(blockZ), QuartPos.fromBlock(blockW)));
		}

		return biomes;
	}

	// TODO getHeight
	// TODO getDensity
	// TODO iterateBiomes
	// TODO iterateHeights
	// TODO iterateDensities
	// TODO getInsideIndex
	// TODO getOutsideIndex
	// TODO getW
	// TODO? Packed
}
