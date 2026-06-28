package com.iluha168.mc4d.world.level.chunk;

import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;

/**
 * Implemented by {@link ChunkGeneratorStructureState}.
 */
public interface ChunkGeneratorStructureState4 {
	boolean hasStructureChunkInRange(Holder<StructureSet> structureSet, int sourceX, int sourceZ, int sourceW, int range);
}
