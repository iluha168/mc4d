package com.iluha168.mc4d.core;

import com.iluha168.mc4d.world.level.Level4;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The reason chunks are 16x16 horizontally, well, 16x16x16 now!
 * All {@link SectionPos} instances are secretly {@link SectionPos4}
 */
public interface SectionPos4 {
	/** A bit for sign, plus bits to index any section in a Level4. */
	int PACKED_X_LENGTH = 1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(SectionPos.blockToSectionCoord(Level4.MAX_LEVEL_SIZE)));
	int PACKED_Z_LENGTH = PACKED_X_LENGTH;
	int PACKED_W_LENGTH = PACKED_X_LENGTH;
	int PACKED_Y_LENGTH = Long.SIZE - PACKED_X_LENGTH - PACKED_Z_LENGTH - PACKED_W_LENGTH;

	long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
	long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
	long PACKED_W_MASK = (1L << PACKED_W_LENGTH) - 1L;
	long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;

	// Bit layout, low -> high: Y | Z | X | W
	int Y_OFFSET = 0;
	int Z_OFFSET = Y_OFFSET + PACKED_Y_LENGTH;
	int X_OFFSET = Z_OFFSET + PACKED_Z_LENGTH;
	int W_OFFSET = X_OFFSET + PACKED_X_LENGTH;

	int RELATIVE_Y_SHIFT = 0;
	int RELATIVE_Z_SHIFT = RELATIVE_Y_SHIFT + SectionPos.SECTION_BITS;
	int RELATIVE_X_SHIFT = RELATIVE_Z_SHIFT + SectionPos.SECTION_BITS;
	int RELATIVE_W_SHIFT = RELATIVE_X_SHIFT + SectionPos.SECTION_BITS;

	static SectionPos of(int x, int y, int z, int w) {
		SectionPos pos = SectionPos.of(x, y, z);
		Vec4i.setW(pos, w);
		return pos;
	}

	static long offset(long sectionNode, int stepX, int stepY, int stepZ, int stepW) {
		return SectionPos4.asLong(
			SectionPos .x(sectionNode) + stepX,
			SectionPos .y(sectionNode) + stepY,
			SectionPos .z(sectionNode) + stepZ,
			SectionPos4.w(sectionNode) + stepW
		);
	}

	static int sectionRelativeW(short relative) {
		return relative >>> SectionPos4.RELATIVE_W_SHIFT & SectionPos.SECTION_MASK;
	}

	int relativeToBlockW(short relative);

	static int w(long sectionNode) {
		return (int)(sectionNode << (Long.SIZE - SectionPos4.W_OFFSET - SectionPos4.PACKED_W_LENGTH) >> (Long.SIZE - SectionPos4.PACKED_W_LENGTH));
	}

	int w();
	int minBlockW();
	int maxBlockW();

	static long getZeroNode(int x, int z, int w) {
		return SectionPos.getZeroNode(SectionPos4.asLong(x, 0, z, w));
	}

	static long asLong(int x, int y, int z, int w) {
		long node = 0L;
		node |= (x & PACKED_X_MASK) << X_OFFSET;
		node |= (y & PACKED_Y_MASK) << Y_OFFSET;
		node |= (z & PACKED_Z_MASK) << Z_OFFSET;
		node |= (w & PACKED_W_MASK) << W_OFFSET;
		return node;
	}

	SectionPos offset(int x, int y, int z, int w);

	static Stream<SectionPos> betweenClosedStream(
		int minX, int minY, int minZ, int minW,
		int maxX, int maxY, int maxZ, int maxW
	) {
		return StreamSupport.stream(new Spliterators.AbstractSpliterator<>(
			(long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1) * (maxW - minW + 1),
			Spliterator.SIZED
		) {
			final Cursor4D cursor = new Cursor4D(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);

			@Override
			public boolean tryAdvance(Consumer<? super SectionPos> action) {
				if (this.cursor.advance()) {
					action.accept(SectionPos4.of(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ(), this.cursor.nextW()));
					return true;
				}
				return false;
			}
		}, false);
	}

	static void aroundAndAtBlockPos(int blockX, int blockY, int blockZ, int blockW, LongConsumer sectionConsumer) {
		int minSectionX = SectionPos.blockToSectionCoord(blockX - 1);
		int maxSectionX = SectionPos.blockToSectionCoord(blockX + 1);
		int minSectionY = SectionPos.blockToSectionCoord(blockY - 1);
		int maxSectionY = SectionPos.blockToSectionCoord(blockY + 1);
		int minSectionZ = SectionPos.blockToSectionCoord(blockZ - 1);
		int maxSectionZ = SectionPos.blockToSectionCoord(blockZ + 1);
		int minSectionW = SectionPos.blockToSectionCoord(blockW - 1);
		int maxSectionW = SectionPos.blockToSectionCoord(blockW + 1);

		if (minSectionX == maxSectionX && minSectionY == maxSectionY && minSectionZ == maxSectionZ && minSectionW == maxSectionW) {
			sectionConsumer.accept(SectionPos4.asLong(minSectionX, minSectionY, minSectionZ, minSectionW));
		} else {
			for (int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++)
				for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++)
					for (int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++)
						for (int sectionW = minSectionW; sectionW <= maxSectionW; sectionW++)
							sectionConsumer.accept(SectionPos4.asLong(sectionX, sectionY, sectionZ, sectionW));
		}
	}
}
