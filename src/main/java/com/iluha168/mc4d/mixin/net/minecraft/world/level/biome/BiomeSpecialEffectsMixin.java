package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.BiomeSpecialEffects4;
import com.iluha168.mc4d.world.level.levelgen.synth.PerlinSimplexNoise4;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BiomeSpecialEffects.class)
class BiomeSpecialEffectsMixin implements BiomeSpecialEffects4 {
	@Mixin(BiomeSpecialEffects.GrassColorModifier.class)
	static class GrassColorModifierMixin implements BiomeSpecialEffects4.GrassColorModifier {
		@Shadow
		@Final
		private BiomeSpecialEffects.GrassColorModifier.ColorModifier delegate;

		@Mixin(targets = "net/minecraft/world/level/biome/BiomeSpecialEffects$GrassColorModifier$1")
		static class NoneMixin implements BiomeSpecialEffects4.GrassColorModifier {
			@Overwrite
			@Deprecated
			public int modifyColor(double x, double z, int baseColor) {
				throw Err4.arguments2("BiomeSpecialEffects4.GrassColorModifier#modifyColor");
			}
			@Override
			public int modifyColor(double x, double z, double w, int baseColor) {
				return baseColor;
			}
		}

		@Mixin(targets = "net/minecraft/world/level/biome/BiomeSpecialEffects$GrassColorModifier$2")
		static class DarkForestMixin implements BiomeSpecialEffects4.GrassColorModifier {
			@Overwrite
			@Deprecated
			public int modifyColor(double x, double z, int baseColor) {
				throw Err4.arguments2("BiomeSpecialEffects4.GrassColorModifier#modifyColor");
			}
			@Override
			public int modifyColor(double x, double z, double w, int baseColor) {
				return ARGB.opaque((baseColor & 16711422) + 2634762 >> 1);
			}
		}

		@Mixin(targets = "net/minecraft/world/level/biome/BiomeSpecialEffects$GrassColorModifier$3")
		static class SwampMixin implements BiomeSpecialEffects4.GrassColorModifier {
			@Overwrite
			@Deprecated
			public int modifyColor(double x, double z, int baseColor) {
				throw Err4.arguments2("BiomeSpecialEffects4.GrassColorModifier#modifyColor");
			}
			@Override
			@SuppressWarnings("removal")
			public int modifyColor(double x, double z, double w, int baseColor) {
				double groundValue = ((PerlinSimplexNoise4) Biome.BIOME_INFO_NOISE).getValue(x * 0.0225, z * 0.0225, w * 0.0225, false);
				return groundValue < -0.1 ? -11766212 : -9801671;
			}
		}

		@Overwrite
		@Deprecated
		public int modifyColor(double x, double z, int baseColor) {
			throw Err4.arguments2("BiomeSpecialEffects4.GrassColorModifier#modifyColor");
		}
		@Override
		public int modifyColor(double x, double z, double w, int baseColor) {
			return ((BiomeSpecialEffects4.GrassColorModifier.ColorModifier) this.delegate).modifyGrassColor(x, z, w, baseColor);
		}
	}
}
