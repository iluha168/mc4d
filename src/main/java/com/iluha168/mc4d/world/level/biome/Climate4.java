package com.iluha168.mc4d.world.level.biome;

import net.minecraft.world.level.biome.Climate;

/**
 * Implemented by {@link net.minecraft.world.level.biome.Climate}.
 */
public interface Climate4 {

	/**
	 * Implemented by {@link net.minecraft.world.level.biome.Climate.Sampler}.
	 */
	interface Sampler {
		static Climate4.Sampler as(Climate.Sampler sampler) {
			return (Climate4.Sampler) (Object) sampler;
		}

		Climate.TargetPoint sample(int quartX, int quartY, int quartZ, int quartW);
	}
}
