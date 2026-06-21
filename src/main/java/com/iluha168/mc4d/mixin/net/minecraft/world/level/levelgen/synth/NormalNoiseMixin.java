package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.synth;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.synth.NormalNoise4;
import com.iluha168.mc4d.world.level.levelgen.synth.PerlinNoise4;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NormalNoise.class)
class NormalNoiseMixin implements NormalNoise4 {
	@Shadow @Final private static double INPUT_FACTOR;
	@Shadow @Final private double valueFactor;
	@Shadow @Final private PerlinNoise first;
	@Shadow @Final private PerlinNoise second;

	@Overwrite
	@Deprecated
	public double getValue(double x, double y, double z) {
		throw Err4.arguments3("NormalNoise4#getValue");
	}
	@Override
	public double getValue(double x, double y, double z, double w) {
		double x2 = x * INPUT_FACTOR;
		double y2 = y * INPUT_FACTOR;
		double z2 = z * INPUT_FACTOR;
		double w2 = w * INPUT_FACTOR;
		return ((PerlinNoise4) this.first).getValue(x, y, z, w) + ((PerlinNoise4) this.second).getValue(x2, y2, z2, w2) * this.valueFactor;
	}
}
