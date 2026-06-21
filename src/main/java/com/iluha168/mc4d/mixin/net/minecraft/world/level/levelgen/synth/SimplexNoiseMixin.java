package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.synth;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.synth.SimplexNoise4;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimplexNoise.class)
abstract
class SimplexNoiseMixin implements SimplexNoise4 {
	@Shadow
	@Final
	@Mutable
	public static int[][] GRADIENT;

	@Shadow
	protected abstract int p(int x);

	@Unique public double wo;
	@Override
	public double wo() {
		return this.wo;
	}

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void GRADIENT(CallbackInfo ci) {
		// Vanilla seems to lock one element to 0, and the other are permutations of +-1
		GRADIENT = new int[][]{
			{+0, +1, +1, +1}, {+0, +1, +1, -1}, {+0, +1, -1, +1}, {+0, +1, -1, -1},
			{+0, -1, +1, +1}, {+0, -1, +1, -1}, {+0, -1, -1, +1}, {+0, -1, -1, -1},

			{+1, +0, +1, +1}, {+1, +0, +1, -1}, {+1, +0, -1, +1}, {+1, +0, -1, -1},
			{-1, +0, +1, +1}, {-1, +0, +1, -1}, {-1, +0, -1, +1}, {-1, +0, -1, -1},

			{+1, +1, +0, +1}, {+1, +1, +0, -1}, {+1, -1, +0, +1}, {+1, -1, +0, -1},
			{-1, +1, +0, +1}, {-1, +1, +0, -1}, {-1, -1, +0, +1}, {-1, -1, +0, -1},

			{+1, +1, +1, +0}, {+1, +1, -1, +0}, {+1, -1, +1, +0}, {+1, -1, -1, +0},
			{-1, +1, +1, +0}, {-1, +1, -1, +0}, {-1, -1, +1, +0}, {-1, -1, -1, +0},
		};
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void init(RandomSource random, CallbackInfo ci) {
		this.wo = random.nextDouble() * 256.0;
	}

	@Overwrite
	@Deprecated
	protected static double dot(int[] g, double x, double y, double z) {
		throw Err4.arguments3("SimplexNoise4#dot");
	}

	@Overwrite
	@Deprecated
	private double getCornerNoise3D(int index, double x, double y, double z, double base) {
		throw Err4.arguments3(null);
	}
	@Unique
	private double getCornerNoise4D(int index, double x, double y, double z, double w, @SuppressWarnings("SameParameterValue") double base) {
		double t0 = base - x * x - y * y - z * z - w * w;
		double n0;
		if (t0 < 0.0) {
			n0 = 0.0;
		} else {
			t0 *= t0;
			n0 = t0 * t0 * SimplexNoise4.dot(GRADIENT[index], x, y, z, w);
		}

		return n0;
	}

	@Overwrite
	@Deprecated
	public double getValue(double xin, double yin) {
		throw Err4.arguments2("SimplexNoise4#getValue4");
	}
	@Override
	public double getValue4(double xin, double zin, double win) {
		double F3 = 1.0 / 3.0;
		double s = (xin + zin + win) * F3;
		int i = Mth.floor(xin + s);
		int j = Mth.floor(zin + s);
		int k = Mth.floor(win + s);
		double G3 = 1.0 / 6.0;
		double t = (i + j + k) * G3;
		double X0 = i - t;
		double Z0 = j - t;
		double W0 = k - t;
		double x0 = xin - X0;
		double z0 = zin - Z0;
		double w0 = win - W0;
		int i1;
		int j1;
		int k1;
		int i2;
		int j2;
		int k2;
		if (x0 >= z0) {
			if (z0 >= w0) {
				i1 = 1;
				j1 = 0;
				k1 = 0;
				i2 = 1;
				j2 = 1;
				k2 = 0;
			} else if (x0 >= w0) {
				i1 = 1;
				j1 = 0;
				k1 = 0;
				i2 = 1;
				j2 = 0;
				k2 = 1;
			} else {
				i1 = 0;
				j1 = 0;
				k1 = 1;
				i2 = 1;
				j2 = 0;
				k2 = 1;
			}
		} else if (z0 < w0) {
			i1 = 0;
			j1 = 0;
			k1 = 1;
			i2 = 0;
			j2 = 1;
			k2 = 1;
		} else if (x0 < w0) {
			i1 = 0;
			j1 = 1;
			k1 = 0;
			i2 = 0;
			j2 = 1;
			k2 = 1;
		} else {
			i1 = 0;
			j1 = 1;
			k1 = 0;
			i2 = 1;
			j2 = 1;
			k2 = 0;
		}

		double x1 = x0 - i1 + G3;
		double z1 = z0 - j1 + G3;
		double w1 = w0 - k1 + G3;
		double x2 = x0 - i2 + 2.0 * G3;
		double z2 = z0 - j2 + 2.0 * G3;
		double w2 = w0 - k2 + 2.0 * G3;
		double x3 = x0 - 1.0 + 3.0 * G3;
		double z3 = z0 - 1.0 + 3.0 * G3;
		double w3 = w0 - 1.0 + 3.0 * G3;
		int ii = i & 0xFF;
		int jj = j & 0xFF;
		int kk = k & 0xFF;
		int gi0 = this.p(ii + this.p(jj + this.p(kk))) % 32;
		int gi1 = this.p(ii + i1 + this.p(jj + j1 + this.p(kk + k1))) % 32;
		int gi2 = this.p(ii + i2 + this.p(jj + j2 + this.p(kk + k2))) % 32;
		int gi3 = this.p(ii + 1 + this.p(jj + 1 + this.p(kk + 1))) % 32;
		// The 4D gradient table is reused with a zeroed 4th component.
		double n0 = this.getCornerNoise4D(gi0, x0, z0, w0, 0.0, 0.6);
		double n1 = this.getCornerNoise4D(gi1, x1, z1, w1, 0.0, 0.6);
		double n2 = this.getCornerNoise4D(gi2, x2, z2, w2, 0.0, 0.6);
		double n3 = this.getCornerNoise4D(gi3, x3, z3, w3, 0.0, 0.6);
		return 32.0 * (n0 + n1 + n2 + n3);
	}

	@Overwrite
	@Deprecated
	public double getValue(double xin, double yin, double zin) {
		throw Err4.arguments3("SimplexNoise4#getValue");
	}
	@Override
	public double getValue(double xin, double yin, double zin, double win) {
		double F4 = (Math.sqrt(5.0) - 1.0) / 4.0;
		double s = (xin + yin + zin + win) * F4;
		int i = Mth.floor(xin + s);
		int j = Mth.floor(yin + s);
		int k = Mth.floor(zin + s);
		int l = Mth.floor(win + s);
		double G4 = (5.0 - Math.sqrt(5.0)) / 20.0;
		double t = (i + j + k + l) * G4;
		double X0 = i - t;
		double Y0 = j - t;
		double Z0 = k - t;
		double W0 = l - t;
		double x0 = xin - X0;
		double y0 = yin - Y0;
		double z0 = zin - Z0;
		double w0 = win - W0;
		int rankX = 0;
		int rankY = 0;
		int rankZ = 0;
		int rankW = 0;
		if (x0 > y0) rankX++; else rankY++;
		if (x0 > z0) rankX++; else rankZ++;
		if (x0 > w0) rankX++; else rankW++;
		if (y0 > z0) rankY++; else rankZ++;
		if (y0 > w0) rankY++; else rankW++;
		if (z0 > w0) rankZ++; else rankW++;
		int i1 = rankX >= 3 ? 1 : 0;
		int j1 = rankY >= 3 ? 1 : 0;
		int k1 = rankZ >= 3 ? 1 : 0;
		int l1 = rankW >= 3 ? 1 : 0;
		int i2 = rankX >= 2 ? 1 : 0;
		int j2 = rankY >= 2 ? 1 : 0;
		int k2 = rankZ >= 2 ? 1 : 0;
		int l2 = rankW >= 2 ? 1 : 0;
		int i3 = rankX >= 1 ? 1 : 0;
		int j3 = rankY >= 1 ? 1 : 0;
		int k3 = rankZ >= 1 ? 1 : 0;
		int l3 = rankW >= 1 ? 1 : 0;

		double x1 = x0 - i1 + G4;
		double y1 = y0 - j1 + G4;
		double z1 = z0 - k1 + G4;
		double w1 = w0 - l1 + G4;
		double x2 = x0 - i2 + 2.0 * G4;
		double y2 = y0 - j2 + 2.0 * G4;
		double z2 = z0 - k2 + 2.0 * G4;
		double w2 = w0 - l2 + 2.0 * G4;
		double x3 = x0 - i3 + 3.0 * G4;
		double y3 = y0 - j3 + 3.0 * G4;
		double z3 = z0 - k3 + 3.0 * G4;
		double w3 = w0 - l3 + 3.0 * G4;
		double x4 = x0 - 1.0 + 4.0 * G4;
		double y4 = y0 - 1.0 + 4.0 * G4;
		double z4 = z0 - 1.0 + 4.0 * G4;
		double w4 = w0 - 1.0 + 4.0 * G4;
		int ii = i & 0xFF;
		int jj = j & 0xFF;
		int kk = k & 0xFF;
		int ll = l & 0xFF;
		int gi0 = this.p(ii + this.p(jj + this.p(kk + this.p(ll)))) % 32;
		int gi1 = this.p(ii + i1 + this.p(jj + j1 + this.p(kk + k1 + this.p(ll + l1)))) % 32;
		int gi2 = this.p(ii + i2 + this.p(jj + j2 + this.p(kk + k2 + this.p(ll + l2)))) % 32;
		int gi3 = this.p(ii + i3 + this.p(jj + j3 + this.p(kk + k3 + this.p(ll + l3)))) % 32;
		int gi4 = this.p(ii + 1 + this.p(jj + 1 + this.p(kk + 1 + this.p(ll + 1)))) % 32;
		double n0 = this.getCornerNoise4D(gi0, x0, y0, z0, w0, 0.6);
		double n1 = this.getCornerNoise4D(gi1, x1, y1, z1, w1, 0.6);
		double n2 = this.getCornerNoise4D(gi2, x2, y2, z2, w2, 0.6);
		double n3 = this.getCornerNoise4D(gi3, x3, y3, z3, w3, 0.6);
		double n4 = this.getCornerNoise4D(gi4, x4, y4, z4, w4, 0.6);
		return 27.0 * (n0 + n1 + n2 + n3 + n4);
	}
}
