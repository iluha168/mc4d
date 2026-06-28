package com.iluha168.mc4d.world.level.levelgen.structure.placement;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

/**
 * Implemented by {@link RandomSpreadStructurePlacement}.
 */
public interface RandomSpreadStructurePlacement4 {
	ChunkPos getPotentialStructureChunk(long seed, int sourceX, int sourceZ, int sourceW);
}
