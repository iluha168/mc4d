package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.synth;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.synth.PerlinSimplexNoise4;
import com.iluha168.mc4d.world.level.levelgen.synth.SimplexNoise4;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PerlinSimplexNoise.class)
class PerlinSimplexNoiseMixin implements PerlinSimplexNoise4 {
	@Shadow @Final private SimplexNoise[] noiseLevels;
	@Shadow @Final private double highestFreqValueFactor;
	@Shadow @Final private double highestFreqInputFactor;

	@Redirect(method = "<init>(Lnet/minecraft/util/RandomSource;Lit/unimi/dsi/fastutil/ints/IntSortedSet;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/synth/SimplexNoise;getValue(DDD)D"
	))
	private double getValue(SimplexNoise zeroOctave, double x, double y, double z) {
		SimplexNoise4 zeroOctave4 = (SimplexNoise4) zeroOctave;
		return zeroOctave4.getValue(x, y, z, zeroOctave4.wo());
	}

	@Overwrite
	@Deprecated
	public double getValue(double x, double y, boolean useNoiseStart) {
		throw Err4.arguments2("PerlinSimplexNoise4#getValue");
	}
	@Override
	public double getValue(double x, double z, double w, boolean useNoiseStart) {
		double value = 0.0;
		double factor = this.highestFreqInputFactor;
		double valueFactor = this.highestFreqValueFactor;

		for (SimplexNoise noiseLevel : this.noiseLevels) {
			if (noiseLevel != null) {
				value += ((SimplexNoise4) noiseLevel).getValue4(
						x * factor + (useNoiseStart ? noiseLevel.xo : 0.0),
						z * factor + (useNoiseStart ? noiseLevel.yo : 0.0),
						w * factor + (useNoiseStart ? noiseLevel.zo : 0.0)
					)
					* valueFactor;
			}

			factor /= 2.0;
			valueFactor *= 2.0;
		}

		return value;
	}
}
