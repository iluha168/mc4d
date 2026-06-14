package com.iluha168.mc4d.world.level.chunk;

import com.iluha168.mc4d.world.level.biome.BiomeManager4;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Implemented by {@link net.minecraft.world.level.chunk.ChunkAccess} and its subclasses.
 */
// TODO: actually implement by subclasses
public interface ChunkAccess4 extends BiomeManager4.NoiseBiomeSource {
	int getHeight(Heightmap.Types type, int x, int z, int w);
}
