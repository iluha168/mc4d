package com.iluha168.mc4d.world.level.levelgen.synth;

/**
 * Implemented by {@link net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise}.
 */
public interface PerlinSimplexNoise4 {
	double getValue(double x, double z, double w, boolean useNoiseStart);
}
