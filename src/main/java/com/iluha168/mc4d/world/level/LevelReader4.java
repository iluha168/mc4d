package com.iluha168.mc4d.world.level;

import com.iluha168.mc4d.world.level.biome.BiomeManager4;
import com.iluha168.mc4d.world.level.chunk.ChunkAccess4;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

/**
 * All implementations of {@link net.minecraft.world.level.LevelReader} also implement {@link LevelReader4}.
 */
public interface LevelReader4 extends CollisionGetter4, BiomeManager4.NoiseBiomeSource {
	@Nullable ChunkAccess getChunk(
		final int chunkX, final int chunkZ, final int chunkW,
		final ChunkStatus targetStatus, final boolean loadOrGenerate
	);
	boolean hasChunk(int chunkX, int chunkZ, int chunkW);

	int getHeight(Heightmap.Types type, int x, int z, int w);

	@Override
	default Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, int quartW) {
		ChunkAccess chunk = this.getChunk(QuartPos.toSection(quartX), QuartPos.toSection(quartZ), QuartPos.toSection(quartW), ChunkStatus.BIOMES, false);
		return chunk != null ? ((ChunkAccess4) chunk).getNoiseBiome(quartX, quartY, quartZ, quartW) : this.getUncachedNoiseBiome(quartX, quartY, quartZ, quartW);
	}
	Holder<Biome> getUncachedNoiseBiome(int quartX, int quartY, int quartZ, int quartW);

	default ChunkAccess getChunk(int chunkX, int chunkZ, int chunkW) {
		return this.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.FULL, true);
	}

	default ChunkAccess getChunk(int chunkX, int chunkZ, int chunkW, ChunkStatus status) {
		return this.getChunk(chunkX, chunkZ, chunkW, status, true);
	}

	@Override
	default @Nullable BlockGetter getChunkForCollisions(int chunkX, int chunkZ, int chunkW) {
		return this.getChunk(chunkX, chunkZ, chunkW, ChunkStatus.EMPTY, false);
	}

	default boolean hasChunkAt(int blockX, int blockZ, int blockW) {
		return this.hasChunk(
			SectionPos.blockToSectionCoord(blockX),
			SectionPos.blockToSectionCoord(blockZ),
			SectionPos.blockToSectionCoord(blockW)
		);
	}

	default boolean hasChunksAt(int x0, int y0, int z0, int w0, int x1, int y1, int z1, int w1) {
		LevelHeightAccessor This = (LevelHeightAccessor) this;
		return y1 >= This.getMinY() && y0 <= This.getMaxY() && this.hasChunksAt4(x0, z0, w0, x1, z1, w1);
	}

	default boolean hasChunksAt4(int x0, int z0, int w0, int x1, int z1, int w1) {
		int chunkX0 = SectionPos.blockToSectionCoord(x0);
		int chunkX1 = SectionPos.blockToSectionCoord(x1);
		int chunkZ0 = SectionPos.blockToSectionCoord(z0);
		int chunkZ1 = SectionPos.blockToSectionCoord(z1);
		int chunkW0 = SectionPos.blockToSectionCoord(w0);
		int chunkW1 = SectionPos.blockToSectionCoord(w1);

		for (int chunkX = chunkX0; chunkX <= chunkX1; chunkX++)
			for (int chunkZ = chunkZ0; chunkZ <= chunkZ1; chunkZ++)
				for (int chunkW = chunkW0; chunkW <= chunkW1; chunkW++)
					if (!this.hasChunk(chunkX, chunkZ, chunkW))
						return false;
		return true;
	}
}
