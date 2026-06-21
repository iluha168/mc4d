package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen.synth;

import com.iluha168.mc4d.world.level.levelgen.DensityFunction4;
import com.iluha168.mc4d.world.level.levelgen.synth.ImprovedNoise4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlendedNoise.class)
class BlendedNoiseMixin {
	// Treating all XZ stuff as XZW, similar to what I did with voxel shapes
	@Shadow @Final private double xzMultiplier;
	@Shadow @Final private double xzFactor;

	@Definition(id = "mainZ", local = @Local(type = double.class, name = "mainZ"))
	@Expression("mainZ = @(?)")
	@Inject(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
	void compute_mainW(
		DensityFunction.FunctionContext context, CallbackInfoReturnable<Double> cir,
		@Share("limitW") LocalDoubleRef limitW,
		@Share("mainW") LocalDoubleRef mainW
	) {
		limitW.set(((DensityFunction4.FunctionContext) context).blockW() * this.xzMultiplier);
		mainW.set(limitW.get() / this.xzFactor);
	}
	@Definition(id = "octaveNoise", local = @Local(type = ImprovedNoise.class, name = "noise"))
	@Definition(id = "noise", method = "Lnet/minecraft/world/level/levelgen/synth/ImprovedNoise;noise(DDDDD)D")
	@Expression("octaveNoise.noise(?, ?, ?, ?, ?)")
	@Redirect(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
	double compute_octaveNoise(
		ImprovedNoise instance, double _x, double _y, double _z, double yScale, double yFudge,
		@Share("mainW") LocalDoubleRef mainW,
		@Local(name = "pow") double pow
	) {
		return ImprovedNoise4.as(instance).noise(_x, _y, _z, PerlinNoise.wrap(mainW.get() * pow), yScale, yFudge);
	}
	@Definition(id = "wz", local = @Local(type = double.class, name = "wz"))
	@Expression("wz = @(?)")
	@Inject(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
	void compute_ww(
		DensityFunction.FunctionContext context, CallbackInfoReturnable<Double> cir,
		@Share("limitW") LocalDoubleRef limitW,
		@Local(name = "pow") double pow,
		@Share("ww") LocalDoubleRef ww
	) {
		ww.set(PerlinNoise.wrap(limitW.get() * pow));
	}
	@Definition(id = "minNoise", local = @Local(type = ImprovedNoise.class, name = "minNoise"))
	@Definition(id = "noise", method = "Lnet/minecraft/world/level/levelgen/synth/ImprovedNoise;noise(DDDDD)D")
	@Expression("minNoise.noise(?, ?, ?, ?, ?)")
	@Redirect(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
	double compute_minNoise(
		ImprovedNoise instance, double _x, double _y, double _z, double yScale, double yFudge,
		@Share("ww") LocalDoubleRef ww
	) {
		return ImprovedNoise4.as(instance).noise(_x, _y, _z, ww.get(), yScale, yFudge);
	}
	@Definition(id = "noise", method = "Lnet/minecraft/world/level/levelgen/synth/ImprovedNoise;noise(DDDDD)D")
	@Definition(id = "maxNoise", local = @Local(type = ImprovedNoise.class, name = "maxNoise"))
	@Expression("maxNoise.noise(?, ?, ?, ?, ?)")
	@Redirect(method = "compute", at = @At("MIXINEXTRAS:EXPRESSION"))
	double compute_maxNoise(
		ImprovedNoise instance, double _x, double _y, double _z, double yScale, double yFudge,
		@Share("ww") LocalDoubleRef ww
	) {
		return ImprovedNoise4.as(instance).noise(_x, _y, _z, ww.get(), yScale, yFudge);
	}
}
