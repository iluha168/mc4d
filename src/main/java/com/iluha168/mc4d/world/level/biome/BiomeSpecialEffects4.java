package com.iluha168.mc4d.world.level.biome;

import net.minecraft.world.level.biome.BiomeSpecialEffects;

/**
 * Implemented by {@link BiomeSpecialEffects}.
 */
public interface BiomeSpecialEffects4 {
	/**
	 * Implemented by {@link BiomeSpecialEffects.GrassColorModifier}.
	 */
	interface GrassColorModifier {
		static BiomeSpecialEffects4.GrassColorModifier as(BiomeSpecialEffects.GrassColorModifier modifier) {
			return (BiomeSpecialEffects4.GrassColorModifier) (Object) modifier;
		}

		int modifyColor(double x, double z, double w, int baseColor);

		/**
		 * Implemented by the same classes that implement {@link BiomeSpecialEffects.GrassColorModifier.ColorModifier}.
		 */
		@FunctionalInterface
		interface ColorModifier {
			int modifyGrassColor(double x, double z, double w, int color);
		}
	}
}
