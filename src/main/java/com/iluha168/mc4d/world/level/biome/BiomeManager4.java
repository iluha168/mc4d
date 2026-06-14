package com.iluha168.mc4d.world.level.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

/**
 * Implemented by {@link net.minecraft.world.level.biome.BiomeManager}.
 */
public interface BiomeManager4 {
	Holder<Biome> getNoiseBiomeAtPosition(double x, double y, double z, double w);
	Holder<Biome> getNoiseBiomeAtQuart(int quartX, int quartY, int quartZ, int quartW);

	interface NoiseBiomeSource {
		Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ, final int quartW);
	}
}
