package com.iluha168.mc4d.world.level.levelgen;

import com.iluha168.mc4d.server.level.ColumnPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.levelgen.blending.Blender4;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;

public class NoiseChunk4 extends NoiseChunk implements DensityFunction4.FunctionContext {
	public final int chunkW;
	public final int firstCellW;
	public final int firstNoiseW;
	public int cellStartBlockW;
	public int inCellW;

	public NoiseChunk4(
		int cellCountXZ,
		RandomState randomState,
		int chunkMinBlockX,
		int chunkMinBlockZ,
		int chunkMinBlockW,
		NoiseSettings noiseSettings,
		DensityFunctions.BeardifierOrMarker beardifier,
		NoiseGeneratorSettings settings,
		Aquifer.FluidPicker globalFluidPicker,
		Blender blender
	) {
		this.chunkW = SectionPos.blockToSectionCoord(chunkMinBlockW);
		this.firstNoiseW = QuartPos.fromBlock(chunkMinBlockW);
		super(cellCountXZ, randomState, chunkMinBlockX, chunkMinBlockZ, noiseSettings, beardifier, settings, globalFluidPicker, blender);
		this.firstCellW = Math.floorDiv(chunkMinBlockW, this.cellWidth);
	}

	public int inCellW() {
		return this.inCellW;
	}

	@Override
	public int blockW() {
		return this.cellStartBlockW + this.inCellW;
	}

	@Override
	@Deprecated
	public int maxPreliminarySurfaceLevel(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
		throw Err4.arguments2("NoiseChunk4#maxPreliminarySurfaceLevel");
	}
	public int maxPreliminarySurfaceLevel(int minBlockX, int minBlockZ, int minBlockW, int maxBlockX, int maxBlockZ, int maxBlockW) {
		int maxY = Integer.MIN_VALUE;

		for (int blockZ = minBlockZ; blockZ <= maxBlockZ; blockZ += 4)
			for (int blockX = minBlockX; blockX <= maxBlockX; blockX += 4)
				for (int blockW = minBlockW; blockW <= maxBlockW; blockW += 4) {
					int surfaceLevel = this.preliminarySurfaceLevel(blockX, blockZ, blockW);
					if (surfaceLevel > maxY) {
						maxY = surfaceLevel;
					}
				}

		return maxY;
	}

	@Override
	@Deprecated
	public int preliminarySurfaceLevel(int sampleX, int sampleZ) {
		throw Err4.arguments2("NoiseChunk4#preliminarySurfaceLevel");
	}
	public int preliminarySurfaceLevel(int sampleX, int sampleZ, int sampleW) {
		int quantizedX = QuartPos.toBlock(QuartPos.fromBlock(sampleX));
		int quantizedZ = QuartPos.toBlock(QuartPos.fromBlock(sampleZ));
		int quantizedW = QuartPos.toBlock(QuartPos.fromBlock(sampleW));
		return this.preliminarySurfaceLevelCache.computeIfAbsent(ColumnPos4.asLong(quantizedX, quantizedZ, quantizedW), this::computePreliminarySurfaceLevel);
	}

	@Override
	@Deprecated
	public void selectCellYZ(int cellYIndex, int cellZIndex) {
		throw Err4.arguments2("NoiseChunk4#selectCellYZW");
	}
	public void selectCellYZW(int cellYIndex, int cellZIndex, int cellWIndex) {
		for (NoiseChunk.NoiseInterpolator i : this.interpolators) {
			((NoiseInterpolator) i).selectCellYZW(cellYIndex, cellZIndex, cellWIndex);
		}

		this.fillingCell = true;
		this.cellStartBlockY = (cellYIndex + this.cellNoiseMinY) * this.cellHeight;
		this.cellStartBlockZ = (this.firstCellZ + cellZIndex) * this.cellWidth;
		this.cellStartBlockW = (this.firstCellW + cellWIndex) * this.cellWidth;
		this.arrayInterpolationCounter++;

		for (NoiseChunk.CacheAllInCell cellCache : this.cellCaches) {
			cellCache.noiseFiller.fillArray(cellCache.values, this);
		}

		this.arrayInterpolationCounter++;
		this.fillingCell = false;
	}

	public void updateForW(int posW, double factorW) {
		this.inCellW = posW - this.cellStartBlockW;
		this.interpolationCounter++;

		for (NoiseChunk.NoiseInterpolator i : this.interpolators) {
			((NoiseInterpolator) i).updateForW(factorW);
		}
	}

	public Blender.BlendingOutput getOrComputeBlendingOutput(int blockX, int blockZ, int blockW) {
		long pos2D = ChunkPos4.pack(blockX, blockZ, blockW);
		if (this.lastBlendingDataPos == pos2D) {
			return this.lastBlendingOutput;
		} else {
			this.lastBlendingDataPos = pos2D;
			Blender.BlendingOutput output = ((Blender4) this.blender).blendOffsetAndFactor(blockX, blockZ, blockW);
			this.lastBlendingOutput = output;
			return output;
		}
	}

	/**
	 * Implemented by {@link NoiseChunk.NoiseInterpolator}.
	 */
	public interface NoiseInterpolator {
		double[][][] slice0();
		double[][][] slice1();

		void selectCellYZW(int cellYIndex, int cellZIndex, int cellWIndex);

		void updateForW(double factorW);
	}
}
