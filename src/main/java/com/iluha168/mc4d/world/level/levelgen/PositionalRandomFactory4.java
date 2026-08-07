package com.iluha168.mc4d.world.level.levelgen;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

/**
 * Implemented by {@link PositionalRandomFactory} and its implementations.
 */
public interface PositionalRandomFactory4 {
	RandomSource at(int x, int y, int z, int w);
}
