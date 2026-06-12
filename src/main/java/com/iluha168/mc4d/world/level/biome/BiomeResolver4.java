package com.iluha168.mc4d.world.level.biome;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

/**
 * Implemented by the same classes that implement {@link net.minecraft.world.level.biome.BiomeResolver}.
 */
// TODO only FixedBiomeSource implements
public interface BiomeResolver4 {
	Holder<Biome> getNoiseBiome(final int quartX, final int quartY, final int quartZ, final int quartW, final Climate.Sampler sampler);
}
