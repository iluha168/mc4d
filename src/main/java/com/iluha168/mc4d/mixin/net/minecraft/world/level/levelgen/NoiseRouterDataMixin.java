package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.world.level.levelgen.DensityFunctions4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseRouterData.class)
class NoiseRouterDataMixin {
	@Shadow
	private static ResourceKey<DensityFunction> createKey(String name) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private static DensityFunction registerAndWrap(BootstrapContext<DensityFunction> context, ResourceKey<DensityFunction> name, DensityFunction value) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	private static DensityFunction getFunction(HolderGetter<DensityFunction> functions, ResourceKey<DensityFunction> name) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Unique
	private static final ResourceKey<DensityFunction> SHIFT_W = createKey("shift_w");

	@Definition(id = "shiftZ", local = @Local(type = DensityFunction.class, name = "shiftZ"))
	@Expression("shiftZ = @(?)")
	@Inject(method = "bootstrap", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void bootstrap_shiftW(
		BootstrapContext<DensityFunction> context, CallbackInfoReturnable<Holder<? extends DensityFunction>> cir,
		@Local(name = "noises") HolderGetter<NormalNoise.NoiseParameters> noises,
		@Share("shiftW") LocalRef<DensityFunction> shiftW
	) {
		shiftW.set(registerAndWrap(
			context, SHIFT_W, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions4.shiftC(noises.getOrThrow(Noises.SHIFT))))
		));
	}
	@Redirect(method = "bootstrap", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/DensityFunctions;shiftedNoise2d(Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;DLnet/minecraft/core/Holder;)Lnet/minecraft/world/level/levelgen/DensityFunction;"
	))
	private static DensityFunction bootstrap_shiftedNoise2d(
		DensityFunction shiftX, DensityFunction shiftZ, double xzScale, Holder<NormalNoise.NoiseParameters> noiseData,
		@Share("shiftW") LocalRef<DensityFunction> shiftW
	) {
		return DensityFunctions4.shiftedNoise2d(shiftX, shiftZ, shiftW.get(), xzScale, noiseData);
	}

	@Redirect(method = "overworld", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/DensityFunctions;shiftedNoise2d(Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;DLnet/minecraft/core/Holder;)Lnet/minecraft/world/level/levelgen/DensityFunction;"
	))
	private static DensityFunction overworld_shiftedNoise2d(
		DensityFunction shiftX, DensityFunction shiftZ, double xzScale, Holder<NormalNoise.NoiseParameters> noiseData,
		@Local(argsOnly = true, name = "functions") HolderGetter<DensityFunction> functions
	) {
		return DensityFunctions4.shiftedNoise2d(shiftX, shiftZ, getFunction(functions, SHIFT_W), xzScale, noiseData);
	}

	@Redirect(method = "nether", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/DensityFunctions;shiftedNoise2d(Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;DLnet/minecraft/core/Holder;)Lnet/minecraft/world/level/levelgen/DensityFunction;"
	))
	private static DensityFunction nether_shiftedNoise2d(
		DensityFunction shiftX, DensityFunction shiftZ, double xzScale, Holder<NormalNoise.NoiseParameters> noiseData
	) {
		return DensityFunctions4.shiftedNoise2d(shiftX, shiftZ, DensityFunctions.zero(), xzScale, noiseData);
	}
}
