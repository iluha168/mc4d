package com.iluha168.mc4d.world.level.levelgen.synth;

import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/**
 * Implemented by {@link SimplexNoise}.
 */
public interface SimplexNoise4 {
	double wo();

	static double dot(int[] g, double x, double y, double z, double w) {
		return g[0] * x + g[1] * y + g[2] * z + g[3] * w;
	}

	double getValue4(double x, double z, double w);
	double getValue(double x, double y, double z, double w);
}
