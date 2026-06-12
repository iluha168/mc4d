package com.iluha168.mc4d.world.level.chunk;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.LevelChunkSection;

/**
 * Implemented by {@link net.minecraft.world.level.chunk.DataLayer}.
 */
public interface DataLayer4 {
	int NIBBLE_SIZE = SectionPos.SECTION_BITS;
	int NIBBLES_PER_BYTE = (Byte.SIZE / NIBBLE_SIZE);
	int LAYER_SIZE = LevelChunkSection.SECTION_WIDTH * LevelChunkSection.SECTION_WIDTH * LevelChunkSection.SECTION_WIDTH / NIBBLES_PER_BYTE;
	int SIZE = LAYER_SIZE * LevelChunkSection.SECTION_HEIGHT;

	int get(int x, int y, int z, int w);
	void set(int x, int y, int z, int w, int val);
}
