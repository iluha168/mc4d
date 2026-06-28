package com.iluha168.mc4d.mixin.net.minecraft.world.level.levelgen;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.levelgen.DensityFunction4;
import com.iluha168.mc4d.world.level.levelgen.DensityFunctions4;
import com.iluha168.mc4d.world.level.levelgen.synth.SimplexNoise4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DensityFunctions.class)
class DensityFunctionsMixin implements DensityFunctions4 {
	@Shadow
	private static MapCodec<? extends DensityFunction> register(Registry<MapCodec<? extends DensityFunction>> registry, String name, KeyDispatchDataCodec<? extends DensityFunction> codec) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Definition(id = "register", method = "Lnet/minecraft/world/level/levelgen/DensityFunctions;register(Lnet/minecraft/core/Registry;Ljava/lang/String;Lnet/minecraft/util/KeyDispatchDataCodec;)Lcom/mojang/serialization/MapCodec;")
	@Definition(id = "CODEC", field = "Lnet/minecraft/world/level/levelgen/DensityFunctions$ShiftB;CODEC:Lnet/minecraft/util/KeyDispatchDataCodec;")
	@Expression("register(?, ?, CODEC)")
	@Inject(method = "bootstrap", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static void bootstrap(Registry<MapCodec<? extends DensityFunction>> registry, CallbackInfoReturnable<MapCodec<? extends DensityFunction>> cir) {
		register(registry, "shift_c", DensityFunctions4.ShiftC.CODEC);
	}

	@Overwrite
	@Deprecated
	public static DensityFunction shiftedNoise2d(DensityFunction shiftX, DensityFunction shiftZ, double xzScale, Holder<NormalNoise.NoiseParameters> noiseData) {
		throw Err4.arguments2("DensityFunctions4#shiftedNoise2d");
	}

	@Mixin(DensityFunctions.EndIslandDensityFunction.class)
	static class EndIslandDensityFunctionMixin {
		@Overwrite
		@Deprecated
		private static float getHeightValue(SimplexNoise islandNoise, int sectionX, int sectionZ) {
			throw Err4.arguments2(null);
		}
		@Unique
		private static float getHeightValue(SimplexNoise islandNoise, int sectionX, int sectionZ, int sectionW) {
			int chunkX = sectionX / 2;
			int chunkZ = sectionZ / 2;
			int chunkW = sectionW / 2;
			int subSectionX = sectionX % 2;
			int subSectionZ = sectionZ % 2;
			int subSectionW = sectionW % 2;
			float doffs = 100.0F - Mth.sqrt(sectionX * sectionX + sectionZ * sectionZ + sectionW * sectionW) * 8.0F;
			doffs = Mth.clamp(doffs, -100.0F, 80.0F);

			for (int xo = -12; xo <= 12; xo++)
				for (int zo = -12; zo <= 12; zo++)
					for (int wo = -12; wo <= 12; wo++) {
						long totalChunkX = chunkX + xo;
						long totalChunkZ = chunkZ + zo;
						long totalChunkW = chunkW + wo;
						if (totalChunkX * totalChunkX + totalChunkZ * totalChunkZ + totalChunkW * totalChunkW > 4096L && ((SimplexNoise4) islandNoise).getValue4(totalChunkX, totalChunkZ, totalChunkW) < -0.9F) {
							// As far as I can tell, these constants are just random salts? So I chose a random number for W
							float islandSize = (Mth.abs((float)totalChunkX) * 3439.0F + Mth.abs((float)totalChunkZ) * 147.0F + Mth.abs((float) totalChunkW) * 773.0F) % 13.0F + 9.0F;
							float xd = subSectionX - xo * 2;
							float zd = subSectionZ - zo * 2;
							float wd = subSectionW - wo * 2;
							float newDoffs = 100.0F - Mth.sqrt(xd * xd + zd * zd + wd * wd) * islandSize;
							newDoffs = Mth.clamp(newDoffs, -100.0F, 80.0F);
							doffs = Math.max(doffs, newDoffs);
						}
					}

			return doffs;
		}

		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/DensityFunctions$EndIslandDensityFunction;getHeightValue(Lnet/minecraft/world/level/levelgen/synth/SimplexNoise;II)F"
		))
		float compute(SimplexNoise islandNoise, int sectionX, int sectionZ, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return getHeightValue(islandNoise, sectionX, sectionZ, DensityFunction4.FunctionContext.as(context).blockW() / 8);
		}
	}

	@Mixin(DensityFunctions.FindTopSurface.class)
	static class FindTopSurfaceMixin {
		@ModifyExpressionValue(method = "compute", at = @At(
			value = "NEW",
			target = "(III)Lnet/minecraft/world/level/levelgen/DensityFunction$SinglePointContext;"
		))
		DensityFunction.SinglePointContext compute(DensityFunction.SinglePointContext original, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			DensityFunction4.SinglePointContext.as(original).setBlockW(DensityFunction4.FunctionContext.as(context).blockW());
			return original;
		}
	}

	@Mixin(DensityFunctions.Noise.class)
	abstract static class NoiseMixin implements DensityFunctionMixin {
		@SuppressWarnings("DeprecatedIsStillUsed")
		@Shadow
		@Final
		@Deprecated
		private double xzScale;

		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/DensityFunction$NoiseHolder;getValue(DDD)D"
		))
		double compute(DensityFunction.NoiseHolder instance, double x, double y, double z, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return DensityFunction4.NoiseHolder.as(instance).getValue(x, y, z, DensityFunction4.FunctionContext.as(context).blockW() * this.xzScale);
		}
	}

	@Mixin(DensityFunctions.Shift.class)
	abstract static class ShiftMixin implements DensityFunctions4.ShiftNoise {
		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/DensityFunctions$Shift;compute(DDD)D"
		))
		double compute(DensityFunctions.Shift instance, double x, double y, double z, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return this.compute(x, y, z, DensityFunction4.FunctionContext.as(context).blockW());
		}
	}

	@Mixin(DensityFunctions.ShiftA.class)
	abstract static class ShiftAMixin implements DensityFunctions4.ShiftNoise {
		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/DensityFunctions$ShiftA;compute(DDD)D"
		))
		double compute(DensityFunctions.ShiftA instance, double x, double y, double z, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return this.compute(x, y, z, DensityFunction4.FunctionContext.as(context).blockW());
		}
	}

	@Mixin(DensityFunctions.ShiftB.class)
	abstract static class ShiftBMixin implements DensityFunctions4.ShiftNoise {
		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/DensityFunctions$ShiftB;compute(DDD)D"
		))
		double compute(DensityFunctions.ShiftB instance, double z, double x, double y, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			return this.compute(z, DensityFunction4.FunctionContext.as(context).blockW(), x, y);
		}
	}

	@Mixin(DensityFunctions.ShiftNoise.class)
	interface ShiftNoiseMixin extends DensityFunctionMixin, DensityFunctions4.ShiftNoise {
		@Shadow
		DensityFunction.NoiseHolder offsetNoise();

		@Overwrite
		@Deprecated
		default double compute(double localX, double localY, double localZ) {
			throw Err4.arguments3("DensityFunctions4.ShiftNoise#compute");
		}
		@Override
		default double compute(double localX, double localY, double localZ, double localW) {
			return DensityFunction4.NoiseHolder.as(this.offsetNoise()).getValue(localX * 0.25, localY * 0.25, localZ * 0.25, localW * 0.25) * 4.0;
		}
	}

	@Mixin(DensityFunctions.ShiftedNoise.class)
	static class ShiftedNoiseMixin implements DensityFunctionMixin, DensityFunctions4.ShiftedNoise {
		@Shadow
		@Final
		private double xzScale;

		@Unique
		private DensityFunction shiftW;

		@Inject(method = "<init>", at = @At("TAIL"))
		void init(DensityFunction shiftX, DensityFunction shiftY, DensityFunction shiftZ, double xzScale, double yScale, DensityFunction.NoiseHolder noise, CallbackInfo ci) {
			this.shiftW = null;
		}
		@Override
		public @NonNull DensityFunction shiftW() {
			if (this.shiftW == null) throw Err4.field4missing("shiftW");
			return this.shiftW;
		}
		@Override
		public void setShiftW(@NonNull DensityFunction shiftW) {
			this.shiftW = shiftW;
		}

		@Definition(id = "DATA_CODEC", field = "Lnet/minecraft/world/level/levelgen/DensityFunctions$ShiftedNoise;DATA_CODEC:Lcom/mojang/serialization/MapCodec;")
		@Expression("DATA_CODEC = @(?)")
		@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
		private static MapCodec<DensityFunctions.ShiftedNoise> DATA_CODEC(MapCodec<DensityFunctions.ShiftedNoise> original) {
			return RecordCodecBuilder.mapCodec(
				i -> i.group(
						DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_x").forGetter(DensityFunctions.ShiftedNoise::shiftX),
						DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_y").forGetter(DensityFunctions.ShiftedNoise::shiftY),
						DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_z").forGetter(DensityFunctions.ShiftedNoise::shiftZ),
						DensityFunction.HOLDER_HELPER_CODEC.fieldOf("shift_w").forGetter(sn -> DensityFunctions4.ShiftedNoise.as(sn).shiftW()),
						Codec.DOUBLE.fieldOf("xz_scale").forGetter(DensityFunctions.ShiftedNoise::xzScale),
						Codec.DOUBLE.fieldOf("y_scale").forGetter(DensityFunctions.ShiftedNoise::yScale),
						DensityFunction.NoiseHolder.CODEC.fieldOf("noise").forGetter(DensityFunctions.ShiftedNoise::noise)
					)
					.apply(i, DensityFunctions4.ShiftedNoise::from)
			);
		}

		@Redirect(method = "compute", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/DensityFunction$NoiseHolder;getValue(DDD)D"
		))
		double compute(DensityFunction.NoiseHolder instance, double x, double y, double z, @Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context) {
			final double w = DensityFunction4.FunctionContext.as(context).blockW() * this.xzScale + this.shiftW.compute(context);
			return DensityFunction4.NoiseHolder.as(instance).getValue(x, y, z, w);
		}

		@ModifyExpressionValue(method = "mapAll", at = @At(
			value = "NEW",
			target = "(Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;Lnet/minecraft/world/level/levelgen/DensityFunction;DDLnet/minecraft/world/level/levelgen/DensityFunction$NoiseHolder;)Lnet/minecraft/world/level/levelgen/DensityFunctions$ShiftedNoise;"
		))
		DensityFunctions.ShiftedNoise mapAll(DensityFunctions.ShiftedNoise original, @Local(argsOnly = true, name = "visitor") DensityFunction.Visitor visitor) {
			DensityFunctions4.ShiftedNoise.as(original).setShiftW(this.shiftW.mapAll(visitor));
			return original;
		}
	}

	@Mixin(DensityFunctions.WeirdScaledSampler.class)
	static class WeirdScaledSamplerMixin implements DensityFunctionMixin {
		@Redirect(method = "transform", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/DensityFunction$NoiseHolder;getValue(DDD)D"
		))
		double transform(
			DensityFunction.NoiseHolder instance, double x, double y, double z,
			@Local(name = "rarity") double rarity,
			@Local(argsOnly = true, name = "context") DensityFunction.FunctionContext context
		) {
			return DensityFunction4.NoiseHolder.as(instance).getValue(x, y, z, DensityFunction4.FunctionContext.as(context).blockW() / rarity);
		}
	}
}
