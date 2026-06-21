package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.synth;

import com.iluha168.mc4d.math.MathHelpers;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.synth.ImprovedNoise4;
import com.iluha168.mc4d.world.level.levelgen.synth.NoiseUtils4;
import com.iluha168.mc4d.world.level.levelgen.synth.SimplexNoise4;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ImprovedNoise.class)
abstract
class ImprovedNoiseMixin implements ImprovedNoise4 {
	@Shadow @Final public double xo;
	@Shadow @Final public double yo;
	@Shadow @Final public double zo;
	@Unique        public double wo;

	@Shadow
	protected abstract int p(int x);


	@Inject(method = "<init>", at = @At("TAIL"))
	void init(RandomSource random, CallbackInfo ci) {
		this.wo = random.nextDouble() * 256.0;
	}

	@Overwrite
	@Deprecated
	public double noise(double _x, double _y, double _z) {
		throw Err4.arguments3("ImprovedNoise4#noise");
	}
	@Override
	public double noise(double x, double y, double z, double w) {
		return this.noise(x, y, z, w, 0.0, 0.0);
	}

	@Overwrite
	@Deprecated
	public double noise(double _x, double _y, double _z, double yScale, double yFudge) {
		throw Err4.arguments3("ImprovedNoise4#noise");
	}
	@Override
	public double noise(double _x, double _y, double _z, double _w, double yScale, double yFudge) {
		double x = _x + this.xo;
		double y = _y + this.yo;
		double z = _z + this.zo;
		double w = _w + this.wo;
		int xf = Mth.floor(x);
		int yf = Mth.floor(y);
		int zf = Mth.floor(z);
		int wf = Mth.floor(w);
		double xr = x - xf;
		double yr = y - yf;
		double zr = z - zf;
		double wr = w - wf;
		double yrFudge;
		if (yScale != 0.0) {
			double fudgeLimit;
			if (yFudge >= 0.0 && yFudge < yr) {
				fudgeLimit = yFudge;
			} else {
				fudgeLimit = yr;
			}

			yrFudge = Mth.floor(fudgeLimit / yScale + 1.0E-7F) * yScale;
		} else {
			yrFudge = 0.0;
		}

		return this.sampleAndLerp(xf, yf, zf, wf, xr, yr - yrFudge, zr, wr, yr);
	}

	@Overwrite
	@Deprecated
	public double noiseWithDerivative(double _x, double _y, double _z, double[] derivativeOut) {
		throw Err4.arguments3("ImprovedNoise4#noiseWithDerivative");
	}
	@Override
	public double noiseWithDerivative(double _x, double _y, double _z, double _w, double[] derivativeOut) {
		double x = _x + this.xo;
		double y = _y + this.yo;
		double z = _z + this.zo;
		double w = _w + this.wo;
		int xf = Mth.floor(x);
		int yf = Mth.floor(y);
		int zf = Mth.floor(z);
		int wf = Mth.floor(w);
		double xr = x - xf;
		double yr = y - yf;
		double zr = z - zf;
		double wr = w - wf;
		return this.sampleWithDerivative(xf, yf, zf, wf, xr, yr, zr, wr, derivativeOut);
	}

	@Overwrite
	@Deprecated
	private static double gradDot(int hash, double x, double y, double z) {
		throw Err4.arguments3(null);
	}
	@Unique
	private static double gradDot(int hash, double x, double y, double z, double w) {
		return SimplexNoise4.dot(SimplexNoise.GRADIENT[hash & 31], x, y, z, w);
	}

	@Overwrite
	@Deprecated
	private double sampleAndLerp(int x, int y, int z, double xr, double yr, double zr, double yrOriginal) {
		throw Err4.arguments3(null);
	}
	@Unique
	private double sampleAndLerp(int x, int y, int z, int w, double xr, double yr, double zr, double wr, double yrOriginal) {
		int x0 = this.p(x);
		int x1 = this.p(x + 1);
		int xy00 = this.p(x0 + y);
		int xy01 = this.p(x0 + y + 1);
		int xy10 = this.p(x1 + y);
		int xy11 = this.p(x1 + y + 1);
		int xyz000 = this.p(xy00 + z);
		int xyz001 = this.p(xy00 + z + 1);
		int xyz010 = this.p(xy01 + z);
		int xyz011 = this.p(xy01 + z + 1);
		int xyz100 = this.p(xy10 + z);
		int xyz101 = this.p(xy10 + z + 1);
		int xyz110 = this.p(xy11 + z);
		int xyz111 = this.p(xy11 + z + 1);
		double d0000 = gradDot(this.p(xyz000 + w    ),    xr      ,    yr      ,    zr      ,    wr      );
		double d1000 = gradDot(this.p(xyz100 + w    ), xr - 1.0,    yr      ,    zr      ,    wr      );
		double d0100 = gradDot(this.p(xyz010 + w    ),    xr      , yr - 1.0,    zr      ,    wr      );
		double d1100 = gradDot(this.p(xyz110 + w    ), xr - 1.0, yr - 1.0,    zr      ,    wr      );
		double d0010 = gradDot(this.p(xyz001 + w    ),    xr      ,    yr      , zr - 1.0,    wr      );
		double d1010 = gradDot(this.p(xyz101 + w    ), xr - 1.0,    yr      , zr - 1.0,    wr      );
		double d0110 = gradDot(this.p(xyz011 + w    ),    xr      , yr - 1.0, zr - 1.0,    wr      );
		double d1110 = gradDot(this.p(xyz111 + w    ), xr - 1.0, yr - 1.0, zr - 1.0,    wr      );
		double d0001 = gradDot(this.p(xyz000 + w + 1),    xr      ,    yr      ,    zr      , wr - 1.0);
		double d1001 = gradDot(this.p(xyz100 + w + 1), xr - 1.0,    yr      ,    zr      , wr - 1.0);
		double d0101 = gradDot(this.p(xyz010 + w + 1),    xr      , yr - 1.0,    zr      , wr - 1.0);
		double d1101 = gradDot(this.p(xyz110 + w + 1), xr - 1.0, yr - 1.0,    zr      , wr - 1.0);
		double d0011 = gradDot(this.p(xyz001 + w + 1),    xr      ,    yr      , zr - 1.0, wr - 1.0);
		double d1011 = gradDot(this.p(xyz101 + w + 1), xr - 1.0,    yr      , zr - 1.0, wr - 1.0);
		double d0111 = gradDot(this.p(xyz011 + w + 1),    xr      , yr - 1.0, zr - 1.0, wr - 1.0);
		double d1111 = gradDot(this.p(xyz111 + w + 1), xr - 1.0, yr - 1.0, zr - 1.0, wr - 1.0);
		double xAlpha = Mth.smoothstep(xr);
		double yAlpha = Mth.smoothstep(yrOriginal);
		double zAlpha = Mth.smoothstep(zr);
		double wAlpha = Mth.smoothstep(wr);
		return MathHelpers.lerp4(
			xAlpha, yAlpha, zAlpha, wAlpha,
			d0000, d1000, d0100, d1100, d0010, d1010, d0110, d1110,
			d0001, d1001, d0101, d1101, d0011, d1011, d0111, d1111
		);
	}

	@Overwrite
	@Deprecated
	private double sampleWithDerivative(int x, int y, int z, double xr, double yr, double zr, double[] derivativeOut) {
		throw Err4.arguments3(null);
	}
	@Unique
	private double sampleWithDerivative(int x, int y, int z, int w, double xr, double yr, double zr, double wr, double[] derivativeOut) {
		int x0 = this.p(x);
		int x1 = this.p(x + 1);
		int xy00 = this.p(x0 + y);
		int xy01 = this.p(x0 + y + 1);
		int xy10 = this.p(x1 + y);
		int xy11 = this.p(x1 + y + 1);
		int xyz000 = this.p(xy00 + z);
		int xyz001 = this.p(xy00 + z + 1);
		int xyz010 = this.p(xy01 + z);
		int xyz011 = this.p(xy01 + z + 1);
		int xyz100 = this.p(xy10 + z);
		int xyz101 = this.p(xy10 + z + 1);
		int xyz110 = this.p(xy11 + z);
		int xyz111 = this.p(xy11 + z + 1);
		int p0000 = this.p(xyz000 + w);
		int p1000 = this.p(xyz100 + w);
		int p0100 = this.p(xyz010 + w);
		int p1100 = this.p(xyz110 + w);
		int p0010 = this.p(xyz001 + w);
		int p1010 = this.p(xyz101 + w);
		int p0110 = this.p(xyz011 + w);
		int p1110 = this.p(xyz111 + w);
		int p0001 = this.p(xyz000 + w + 1);
		int p1001 = this.p(xyz100 + w + 1);
		int p0101 = this.p(xyz010 + w + 1);
		int p1101 = this.p(xyz110 + w + 1);
		int p0011 = this.p(xyz001 + w + 1);
		int p1011 = this.p(xyz101 + w + 1);
		int p0111 = this.p(xyz011 + w + 1);
		int p1111 = this.p(xyz111 + w + 1);
		int[] g0000 = SimplexNoise.GRADIENT[p0000 & 31];
		int[] g1000 = SimplexNoise.GRADIENT[p1000 & 31];
		int[] g0100 = SimplexNoise.GRADIENT[p0100 & 31];
		int[] g1100 = SimplexNoise.GRADIENT[p1100 & 31];
		int[] g0010 = SimplexNoise.GRADIENT[p0010 & 31];
		int[] g1010 = SimplexNoise.GRADIENT[p1010 & 31];
		int[] g0110 = SimplexNoise.GRADIENT[p0110 & 31];
		int[] g1110 = SimplexNoise.GRADIENT[p1110 & 31];
		int[] g0001 = SimplexNoise.GRADIENT[p0001 & 31];
		int[] g1001 = SimplexNoise.GRADIENT[p1001 & 31];
		int[] g0101 = SimplexNoise.GRADIENT[p0101 & 31];
		int[] g1101 = SimplexNoise.GRADIENT[p1101 & 31];
		int[] g0011 = SimplexNoise.GRADIENT[p0011 & 31];
		int[] g1011 = SimplexNoise.GRADIENT[p1011 & 31];
		int[] g0111 = SimplexNoise.GRADIENT[p0111 & 31];
		int[] g1111 = SimplexNoise.GRADIENT[p1111 & 31];
		double d0000 = SimplexNoise4.dot(g0000,    xr      ,    yr      ,    zr      ,    wr      );
		double d1000 = SimplexNoise4.dot(g1000, xr - 1.0,    yr      ,    zr      ,    wr      );
		double d0100 = SimplexNoise4.dot(g0100,    xr      , yr - 1.0,    zr      ,    wr      );
		double d1100 = SimplexNoise4.dot(g1100, xr - 1.0, yr - 1.0,    zr      ,    wr      );
		double d0010 = SimplexNoise4.dot(g0010,    xr      ,    yr      , zr - 1.0,    wr      );
		double d1010 = SimplexNoise4.dot(g1010, xr - 1.0,    yr      , zr - 1.0,    wr      );
		double d0110 = SimplexNoise4.dot(g0110,    xr      , yr - 1.0, zr - 1.0,    wr      );
		double d1110 = SimplexNoise4.dot(g1110, xr - 1.0, yr - 1.0, zr - 1.0,    wr      );
		double d0001 = SimplexNoise4.dot(g0001,    xr      ,    yr      ,    zr      , wr - 1.0);
		double d1001 = SimplexNoise4.dot(g1001, xr - 1.0,    yr      ,    zr      , wr - 1.0);
		double d0101 = SimplexNoise4.dot(g0101,    xr      , yr - 1.0,    zr      , wr - 1.0);
		double d1101 = SimplexNoise4.dot(g1101, xr - 1.0, yr - 1.0,    zr      , wr - 1.0);
		double d0011 = SimplexNoise4.dot(g0011,    xr      ,    yr      , zr - 1.0, wr - 1.0);
		double d1011 = SimplexNoise4.dot(g1011, xr - 1.0,    yr      , zr - 1.0, wr - 1.0);
		double d0111 = SimplexNoise4.dot(g0111,    xr      , yr - 1.0, zr - 1.0, wr - 1.0);
		double d1111 = SimplexNoise4.dot(g1111, xr - 1.0, yr - 1.0, zr - 1.0, wr - 1.0);
		double xAlpha = Mth.smoothstep(xr);
		double yAlpha = Mth.smoothstep(yr);
		double zAlpha = Mth.smoothstep(zr);
		double wAlpha = Mth.smoothstep(wr);
		double d1x = MathHelpers.lerp4(xAlpha, yAlpha, zAlpha, wAlpha, g0000[0], g1000[0], g0100[0], g1100[0], g0010[0], g1010[0], g0110[0], g1110[0], g0001[0], g1001[0], g0101[0], g1101[0], g0011[0], g1011[0], g0111[0], g1111[0]);
		double d1y = MathHelpers.lerp4(xAlpha, yAlpha, zAlpha, wAlpha, g0000[1], g1000[1], g0100[1], g1100[1], g0010[1], g1010[1], g0110[1], g1110[1], g0001[1], g1001[1], g0101[1], g1101[1], g0011[1], g1011[1], g0111[1], g1111[1]);
		double d1z = MathHelpers.lerp4(xAlpha, yAlpha, zAlpha, wAlpha, g0000[2], g1000[2], g0100[2], g1100[2], g0010[2], g1010[2], g0110[2], g1110[2], g0001[2], g1001[2], g0101[2], g1101[2], g0011[2], g1011[2], g0111[2], g1111[2]);
		double d1w = MathHelpers.lerp4(xAlpha, yAlpha, zAlpha, wAlpha, g0000[3], g1000[3], g0100[3], g1100[3], g0010[3], g1010[3], g0110[3], g1110[3], g0001[3], g1001[3], g0101[3], g1101[3], g0011[3], g1011[3], g0111[3], g1111[3]);
		double d2x = Mth.lerp3(yAlpha, zAlpha, wAlpha, d1000 - d0000, d1100 - d0100, d1010 - d0010, d1110 - d0110, d1001 - d0001, d1101 - d0101, d1011 - d0011, d1111 - d0111);
		double d2y = Mth.lerp3(zAlpha, wAlpha, xAlpha, d0100 - d0000, d0110 - d0010, d0101 - d0001, d0111 - d0011, d1100 - d1000, d1110 - d1010, d1101 - d1001, d1111 - d1011);
		double d2z = Mth.lerp3(wAlpha, xAlpha, yAlpha, d0010 - d0000, d0011 - d0001, d1010 - d1000, d1011 - d1001, d0110 - d0100, d0111 - d0101, d1110 - d1100, d1111 - d1101);
		double d2w = Mth.lerp3(xAlpha, yAlpha, zAlpha, d0001 - d0000, d1001 - d1000, d0101 - d0100, d1101 - d1100, d0011 - d0010, d1011 - d1010, d0111 - d0110, d1111 - d1110);
		double xSD = Mth.smoothstepDerivative(xr);
		double ySD = Mth.smoothstepDerivative(yr);
		double zSD = Mth.smoothstepDerivative(zr);
		double wSD = Mth.smoothstepDerivative(wr);
		double dX = d1x + xSD * d2x;
		double dY = d1y + ySD * d2y;
		double dZ = d1z + zSD * d2z;
		double dW = d1w + wSD * d2w;
		derivativeOut[0] += dX;
		derivativeOut[1] += dY;
		derivativeOut[2] += dZ;
		derivativeOut[3] += dW;
		return MathHelpers.lerp4(
			xAlpha, yAlpha, zAlpha, wAlpha,
			d0000, d1000, d0100, d1100, d0010, d1010, d0110, d1110,
			d0001, d1001, d0101, d1101, d0011, d1011, d0111, d1111
		);
	}

	@Redirect(method = "parityConfigString", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/synth/NoiseUtils;parityNoiseOctaveConfigString(Ljava/lang/StringBuilder;DDD[B)V"
	))
	private void parityConfigString(StringBuilder sb, double xo, double yo, double zo, byte[] p) {
		NoiseUtils4.parityNoiseOctaveConfigString(sb, xo, yo, zo, this.wo, p);
	}
}
