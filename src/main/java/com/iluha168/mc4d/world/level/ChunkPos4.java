package com.iluha168.mc4d.world.level;

import com.iluha168.mc4d.math.MathHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.ApiStatus;

/**
 * All {@link ChunkPos} instances are secretly {@link ChunkPos4} instances.
 */
public interface ChunkPos4 {
	static ChunkPos from(int x, int z, int w) {
		ChunkPos pos = new ChunkPos(x, z);
		ChunkPos4.as(pos).setW(w);
		return pos;
	}

	int w();
	void setW(int w);

	static ChunkPos4 as(ChunkPos pos) {
		return (ChunkPos4) (Object) pos;
	}

	int COORD_BITS = Long.SIZE / 3;
	long COORD_MASK = (1 << COORD_BITS) - 1;

	int X_OFFSET = 0;
	int Z_OFFSET = X_OFFSET + COORD_BITS;
	int W_OFFSET = Z_OFFSET + COORD_BITS;

	static ChunkPos minFromRegion(int regionX, int regionZ, int regionW) {
		return ChunkPos4.from(
			regionX << ChunkPos.REGION_BITS,
			regionZ << ChunkPos.REGION_BITS,
			regionW << ChunkPos.REGION_BITS
		);
	}

	static ChunkPos maxFromRegion(int regionX, int regionZ, int regionW) {
		return ChunkPos4.from(
			(regionX << ChunkPos.REGION_BITS) + ChunkPos.REGION_MAX_INDEX,
			(regionZ << ChunkPos.REGION_BITS) + ChunkPos.REGION_MAX_INDEX,
			(regionW << ChunkPos.REGION_BITS) + ChunkPos.REGION_MAX_INDEX
		);
	}

	static boolean isValid(int x, int z, int w) {
		return MathHelpers.absMax(x, z, w) <= ChunkPos.MAX_COORDINATE_VALUE;
	}

	static long pack(int x, int z, int w) {
		return ((x & ChunkPos4.COORD_MASK) << ChunkPos4.X_OFFSET)
			 | ((z & ChunkPos4.COORD_MASK) << ChunkPos4.Z_OFFSET)
			 | ((w & ChunkPos4.COORD_MASK) << ChunkPos4.W_OFFSET);
	}

	@ApiStatus.Internal
	static int getCoordAtOffset(long pos, int offset) {
		return (int) (pos << (Long.SIZE - offset - COORD_BITS) >> (Long.SIZE - COORD_BITS));
	}

	static int getW(long pos) {
		return ChunkPos4.getCoordAtOffset(pos, ChunkPos4.W_OFFSET);
	}

	static int hash(int x, int z, int w) {
		// HASH_A (multiplier) and HASH_C (increment) are https://en.wikipedia.org/wiki/Linear_congruential_generator constants
		int HASH_A = 1664525;
		int HASH_C = 1013904223;
		// These are just salts so that (x,z,w) and (z,x,w) do not hash the same.
		int HASH_Z_XOR = 0xDEADBEEF;
		int HASH_W_XOR = 0xCAFEBABE;
		final int xTransform = HASH_A * x + HASH_C;
		final int zTransform = HASH_A * (z ^ HASH_Z_XOR) + HASH_C;
		final int wTransform = HASH_A * (w ^ HASH_W_XOR) + HASH_C;
		return xTransform ^ zTransform ^ wTransform;
	}

	default int getMiddleBlockW() {
		return this.getBlockW(SectionPos.SECTION_HALF_SIZE);
	}

	default int getMinBlockW() {
		return SectionPos.sectionToBlockCoord(this.w());
	}

	default int getMaxBlockW() {
		return this.getBlockW(SectionPos.SECTION_MAX_INDEX);
	}

	default int getRegionW() {
		return this.w() >> ChunkPos.REGION_BITS;
	}

	static int getRegionW(long pos) {
		return ChunkPos4.getW(pos) >> ChunkPos.REGION_BITS;
	}

	default int getRegionLocalW() {
		return this.w() & ChunkPos.REGION_MASK;
	}

	BlockPos getBlockAt(int x, int y, int z, int w);

	default int getBlockW(int offset) {
		return SectionPos.sectionToBlockCoord(this.w(), offset);
	}

	int getChessboardDistance(int x, int z, int w);
}