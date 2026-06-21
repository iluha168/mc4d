package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.synth;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.synth.ImprovedNoise4;
import com.iluha168.mc4d.world.level.levelgen.synth.PerlinNoise4;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PerlinNoise.class)
class PerlinNoiseMixin implements PerlinNoise4 {
	@Shadow @Final private ImprovedNoise[] noiseLevels;
	@Shadow @Final private DoubleList amplitudes;
	@Shadow @Final private double lowestFreqValueFactor;
	@Shadow @Final private double lowestFreqInputFactor;

	@Overwrite
	@Deprecated
	public double getValue(double x, double y, double z) {
		throw Err4.arguments3("PerlinNoise4#getValue");
	}
	@Override
	public double getValue(double x, double y, double z, double w) {
		return this.getValue(x, y, z, w, 0.0, 0.0);
	}

	@Overwrite
	@Deprecated
	public double getValue(double x, double y, double z, double yScale, double yFudge) {
		throw Err4.arguments3("PerlinNoise4#getValue");
	}
	@Override
	public double getValue(double x, double y, double z, double w, double yScale, double yFudge) {
		double value = 0.0;
		double factor = this.lowestFreqInputFactor;
		double valueFactor = this.lowestFreqValueFactor;

		for (int i = 0; i < this.noiseLevels.length; i++) {
			ImprovedNoise noise = this.noiseLevels[i];
			if (noise != null) {
				double noiseVal = ImprovedNoise4.as(noise).noise(
					PerlinNoise.wrap(x * factor),
					PerlinNoise.wrap(y * factor),
					PerlinNoise.wrap(z * factor),
					PerlinNoise.wrap(w * factor),
					yScale * factor,
					yFudge * factor
				);
				value += this.amplitudes.getDouble(i) * noiseVal * valueFactor;
			}

			factor *= 2.0;
			valueFactor /= 2.0;
		}

		return value;
	}
}
