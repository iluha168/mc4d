package com.iluha168.mc4d.world.level.levelgen.synth;

import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import org.jspecify.annotations.NonNull;

/**
 * Implemented by {@link ImprovedNoise}.
 */
public interface ImprovedNoise4 {
	static @NonNull ImprovedNoise4 as(@NonNull ImprovedNoise improvedNoise) {
		return (ImprovedNoise4) (Object) improvedNoise;
	}

	double noise(double x, double y, double z, double w);
	double noise(double x, double y, double z, double w, double yScale, double yFudge);

	double noiseWithDerivative(double x, double y, double z, double w, double[] derivativeOut);
}
