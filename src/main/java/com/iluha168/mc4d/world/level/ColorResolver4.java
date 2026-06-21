package com.iluha168.mc4d.world.level;

import com.iluha168.mc4d.util.Err4;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.Nullable;

/**
 * Implemented by the same classes that implement {@link ColorResolver}.
 */
public interface ColorResolver4 {
	int getColor(Biome biome, final double x, final double z, final double w);

	/**
	 * Can be assigned to variables of type {@link ColorResolver}, and can be cast to {@link ColorResolver4}.
	 */
	class Impl implements ColorResolver, ColorResolver4 {
		private final ColorResolver4 resolver;

		public Impl(ColorResolver4 resolver) {
			this.resolver = resolver;
		}

		@Override
		public int getColor(@Nullable Biome biome, double x, double z) {
			throw Err4.arguments2("ColorResolver4#getColor");
		}
		@Override
		public int getColor(Biome biome, double x, double z, double w) {
			return resolver.getColor(biome, x, z, w);
		}
	}
}
