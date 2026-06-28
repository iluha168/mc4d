package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.blending;

import com.iluha168.mc4d.core.Direction18;
import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.LevelReader4;
import com.iluha168.mc4d.world.level.levelgen.blending.Blender4;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Direction8;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(Blender.class)
class BlenderMixin implements Blender4 {
	@Mixin(targets = "net/minecraft/world/level/levelgen/blending/Blender$1")
	static class EMPTYMixin extends BlenderMixin {
		@Override
		public Blender.BlendingOutput blendOffsetAndFactor(int blockX, int blockZ, int blockW) {
			return new Blender.BlendingOutput(1.0, 0.0);
		}
	}

	// TODO of

	@Shadow
	@Final
	private Long2ObjectOpenHashMap<BlendingData> heightAndBiomeBlendingData;

	@Overwrite
	@Deprecated
	public Blender.BlendingOutput blendOffsetAndFactor(int blockX, int blockZ) {
		throw Err4.arguments2("Blender4#blendOffsetAndFactor");
	}
	@Override
	public Blender.BlendingOutput blendOffsetAndFactor(int blockX, int blockZ, int blockW) {
		throw Err4.unreachable("4D saves cannot have old chunks.");
	}

	// TODO blendDensity

	@Overwrite
	@Deprecated
	private double getBlendingDataValue(int cellX, int cellY, int cellZ, Blender.CellValueGetter cellValueGetter) {
		throw Err4.arguments2(null);
	}
	@Unique
	private double getBlendingDataValue(int cellX, int cellY, int cellZ, int cellW, CellValueGetter cellValueGetter) {
		int chunkX = QuartPos.toSection(cellX);
		int chunkZ = QuartPos.toSection(cellZ);
		int chunkW = QuartPos.toSection(cellW);
		boolean minX = (cellX & 3) == 0;
		boolean minZ = (cellZ & 3) == 0;
		boolean minW = (cellW & 3) == 0;
		double value = this.getBlendingDataValue(cellValueGetter, chunkX, chunkZ, chunkW, cellX, cellY, cellZ, cellW);
		if (value != Double.MAX_VALUE) {
			return value;
		}
		if (minX && minZ && minW) {
			value = this.getBlendingDataValue(cellValueGetter, chunkX - 1, chunkZ - 1, chunkW - 1, cellX, cellY, cellZ, cellW);
		}
		if (value != Double.MAX_VALUE) {
			return value;
		}
		if (minX && minZ) {
			value = this.getBlendingDataValue(cellValueGetter, chunkX - 1, chunkZ - 1, chunkW, cellX, cellY, cellZ, cellW);
		}
		if (value != Double.MAX_VALUE) {
			return value;
		}
		if (minX && minW) {
			value = this.getBlendingDataValue(cellValueGetter, chunkX - 1, chunkZ, chunkW - 1, cellX, cellY, cellZ, cellW);
		}
		if (value != Double.MAX_VALUE) {
			return value;
		}
		if (minZ && minW) {
			value = this.getBlendingDataValue(cellValueGetter, chunkX, chunkZ - 1, chunkW - 1, cellX, cellY, cellZ, cellW);
		}
		if (value != Double.MAX_VALUE) {
			return value;
		}
		if (minX) {
			value = this.getBlendingDataValue(cellValueGetter, chunkX - 1, chunkZ, chunkW, cellX, cellY, cellZ, cellW);
		}
		if (value != Double.MAX_VALUE) {
			return value;
		}
		if(minZ) {
			value = this.getBlendingDataValue(cellValueGetter, chunkX, chunkZ - 1, chunkW, cellX, cellY, cellZ, cellW);
		}
		if (value != Double.MAX_VALUE) {
			return value;
		}
		if(minW) {
			value = this.getBlendingDataValue(cellValueGetter, chunkX, chunkZ, chunkW - 1, cellX, cellY, cellZ, cellW);
		}
		return value;
	}

	@Overwrite
	@Deprecated
	private double getBlendingDataValue(Blender.CellValueGetter cellValueGetter, int chunkX, int chunkZ, int cellX, int cellY, int cellZ) {
		throw Err4.arguments3(null);
	}
	@Unique
	private double getBlendingDataValue(CellValueGetter cellValueGetter, int chunkX, int chunkZ, int chunkW, int cellX, int cellY, int cellZ, int cellW) {
		BlendingData blendingData = this.heightAndBiomeBlendingData.get(ChunkPos4.pack(chunkX, chunkZ, chunkW));
		return blendingData != null
			? cellValueGetter.get(
				blendingData,
				cellX - QuartPos.fromSection(chunkX),
				cellY,
				cellZ - QuartPos.fromSection(chunkZ),
				cellW - QuartPos.fromSection(chunkW)
			)
			: Double.MAX_VALUE;
	}

	// TODO getBiomeResolver
	// TODO blendBiome
	// TODO generateBorderTicks

	@Redirect(method = "addAroundOldChunksCarvingMaskFilter", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/WorldGenLevel;getChunk(II)Lnet/minecraft/world/level/chunk/ChunkAccess;"
	))
	private static ChunkAccess addAroundOldChunksCarvingMaskFilter(
		WorldGenLevel region, int x, int z,
		@Local(name = "chunkPos") ChunkPos chunkPos,
		@Local(name = "direction8") Direction8 direction8
	) {
		final int testChunkW = ChunkPos4.as(chunkPos).w() + Direction18.as(direction8).getStepW();
		return ((LevelReader4) region).getChunk(x, z, testChunkW);
	}

	// TODO implement these when a 3D -> 4D world converter is made?
	@Overwrite
	@Deprecated
	public static Blender.DistanceGetter makeOldChunkDistanceGetter(
		@Nullable BlendingData centerBlendingData, Map<Direction8, BlendingData> oldSidesBlendingData
	) {
		throw Err4.unreachable("4D saves cannot have old chunks.");
	}
	@Overwrite
	@Deprecated
	private static Blender.DistanceGetter makeOffsetOldChunkDistanceGetter(@Nullable Direction8 offset, BlendingData blendingData) {
		throw Err4.unreachable("4D saves cannot have old chunks.");
	}

	@Overwrite
	@Deprecated
	private static double distanceToCube(double x, double y, double z, double radiusX, double radiusY, double radiusZ) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static double distanceToCube(double x, double y, double z, double w, double radiusX, double radiusY, double radiusZ, double radiusW) {
		double deltaX = Math.abs(x) - radiusX;
		double deltaY = Math.abs(y) - radiusY;
		double deltaZ = Math.abs(z) - radiusZ;
		double deltaW = Math.abs(w) - radiusW;
		return MathHelpers.length(Math.max(0.0, deltaX), Math.max(0.0, deltaY), Math.max(0.0, deltaZ), Math.max(0.0, deltaW));
	}

	private interface CellValueGetter {
		double get(BlendingData data, int cellX, int cellY, int cellZ, int cellW);
	}

	// TODO DistanceGetter
}
