package com.iluha168.mc4d.world.level.levelgen;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

/**
 * Implemented by {@link WorldgenRandom}.
 */
public interface WorldgenRandom4 {
	long setDecorationSeed(long seed, int chunkX, int chunkZ, int chunkW);

	void setLargeFeatureSeed(long seed, int chunkX, int chunkZ, int chunkW);

	void setLargeFeatureWithSalt(long seed, int x, int z, int w, int blend);

	@SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
	static RandomSource seedSlimeChunk(int x, int z, int w, long seed, long salt) {
		return RandomSource.createThreadLocalInstance(
			seed +
				x * x * 4987142 + x * 5947611 +
				z * z * 4392871L + z * 389711 +
				w * w * 7869257L + w * 702733 // Generated using randomizer, I suspect vanilla salts are also just randoms 🤷
				^ salt
			);
	}
}
