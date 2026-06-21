package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.Biome4;
import com.iluha168.mc4d.world.level.biome.BiomeSpecialEffects4;
import com.iluha168.mc4d.world.level.levelgen.synth.PerlinSimplexNoise4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Biome.class)
class BiomeMixin implements Biome4 {
	@Shadow
	@Final
	private @NonNull BiomeSpecialEffects specialEffects;

	@Shadow
	private int getBaseGrassColor() {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Redirect(method = "getHeightAdjustedTemperature", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/synth/PerlinSimplexNoise;getValue(DDZ)D"
	))
	double getHeightAdjustedTemperature(PerlinSimplexNoise instance, double x, double y, boolean useNoiseStart, BlockPos pos, int seaLevel) {
		return ((PerlinSimplexNoise4)instance).getValue(x, y, Vec4i.getW(pos) / 8.0F, useNoiseStart);
	}

	@ModifyExpressionValue(method = "shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Z)Z", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/LevelReader;isWaterAt(Lnet/minecraft/core/BlockPos;)Z",
		ordinal = 0
	))
	boolean shouldFreeze(boolean original, LevelReader level, BlockPos pos, boolean checkNeighbors) {
		BlockPos4 pos4 = (BlockPos4) pos;
		return original && level.isWaterAt(pos4.kata()) && level.isWaterAt(pos4.ana());
	}

	@Overwrite
	@Deprecated
	public int getGrassColor(double x, double z) {
		throw Err4.arguments2("Biome4#getGrassColor");
	}
	@Override
	public int getGrassColor(double x, double z, double w) {
		int baseGrassColor = this.getBaseGrassColor();
		return BiomeSpecialEffects4.GrassColorModifier.as(this.specialEffects.grassColorModifier()).modifyColor(x, z, w, baseGrassColor);
	}

	@Mixin(targets = "net/minecraft/world/level/biome/Biome$TemperatureModifier$2")
	static class FrozenTemperatureModifierMixin {
		// Why is IDE like this 😭
		@Redirect(method = "modifyTemperature", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/synth/PerlinSimplexNoise;getValue(DDZ)D"
		))
		double modifyTemperature_largeVariation(PerlinSimplexNoise instance, double x, double y, boolean useNoiseStart, BlockPos pos, float baseTemperature) {
			final double ratio = y / pos.getZ();
			return ((PerlinSimplexNoise4) instance).getValue(x, y, Vec4i.getW(pos) * ratio, useNoiseStart);
		}
	}
}
