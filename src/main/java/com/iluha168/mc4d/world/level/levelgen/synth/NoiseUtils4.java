package com.iluha168.mc4d.world.level.levelgen.synth;

import java.util.Locale;

/**
 * Implemented by {@link net.minecraft.world.level.levelgen.synth.NoiseUtils}.
 */
public interface NoiseUtils4 {
	static void parityNoiseOctaveConfigString(StringBuilder sb, double xo, double yo, double zo, double wo, byte[] p) {
		sb.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, wo=%.3f, p0=%d, p255=%d", (float) xo, (float) yo, (float) zo, (float) wo, p[0], p[255]));
	}
	static void parityNoiseOctaveConfigString(StringBuilder sb, double xo, double yo, double zo, double wo, int[] p) {
		sb.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, wo=%.3f, p0=%d, p255=%d", (float) xo, (float) yo, (float) zo, (float) wo, p[0], p[255]));
	}
}
