package com.iluha168.mc4d.world.level.levelgen.synth;

import net.minecraft.world.level.levelgen.synth.PerlinNoise;

/**
 * Implemented by {@link PerlinNoise}.
 */
public interface PerlinNoise4 {
	double getValue(double x, double y, double z, double w);
	double getValue(double x, double y, double z, double w, double yScale, double yFudge);
}
