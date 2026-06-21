package com.iluha168.mc4d.world.level.biome;

import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

/**
 * Implemented by {@link Biome}.
 */
public interface Biome4 {
	static @NonNull Biome4 as(@NonNull Biome biome) {
		return (Biome4) (Object) biome;
	}

	int getGrassColor(double x, double z, double w);
}
