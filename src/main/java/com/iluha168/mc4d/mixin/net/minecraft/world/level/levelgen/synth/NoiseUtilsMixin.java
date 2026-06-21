package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.synth;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.synth.NoiseUtils4;
import net.minecraft.world.level.levelgen.synth.NoiseUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(NoiseUtils.class)
class NoiseUtilsMixin implements NoiseUtils4 {
	@Overwrite
	@Deprecated
	public static void parityNoiseOctaveConfigString(StringBuilder sb, double xo, double yo, double zo, byte[] p) {
		throw Err4.arguments3("NoiseUtils4#parityNoiseOctaveConfigString");
	}
	@Overwrite
	@Deprecated
	public static void parityNoiseOctaveConfigString(StringBuilder sb, double xo, double yo, double zo, int[] p) {
		throw Err4.arguments3("NoiseUtils4#parityNoiseOctaveConfigString");
	}
}
