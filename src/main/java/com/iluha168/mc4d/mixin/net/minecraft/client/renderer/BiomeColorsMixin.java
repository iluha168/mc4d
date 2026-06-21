package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.world.level.ColorResolver4;
import com.iluha168.mc4d.world.level.biome.Biome4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BiomeColors.class)
class BiomeColorsMixin {
	@Definition(id = "GRASS_COLOR_RESOLVER", field = "Lnet/minecraft/client/renderer/BiomeColors;GRASS_COLOR_RESOLVER:Lnet/minecraft/world/level/ColorResolver;")
	@Expression("GRASS_COLOR_RESOLVER = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ColorResolver GRASS_COLOR_RESOLVER(ColorResolver original) {
		return new ColorResolver4.Impl((biome, x, z, w) -> Biome4.as(biome).getGrassColor(x, z, w));
	}

	@Definition(id = "FOLIAGE_COLOR_RESOLVER", field = "Lnet/minecraft/client/renderer/BiomeColors;FOLIAGE_COLOR_RESOLVER:Lnet/minecraft/world/level/ColorResolver;")
	@Expression("FOLIAGE_COLOR_RESOLVER = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ColorResolver FOLIAGE_COLOR_RESOLVER(ColorResolver original) {
		return new ColorResolver4.Impl((biome, _, _, _) -> biome.getFoliageColor());
	}

	@Definition(id = "DRY_FOLIAGE_COLOR_RESOLVER", field = "Lnet/minecraft/client/renderer/BiomeColors;DRY_FOLIAGE_COLOR_RESOLVER:Lnet/minecraft/world/level/ColorResolver;")
	@Expression("DRY_FOLIAGE_COLOR_RESOLVER = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ColorResolver DRY_FOLIAGE_COLOR_RESOLVER(ColorResolver original) {
		return new ColorResolver4.Impl((biome, _, _, _) -> biome.getDryFoliageColor());
	}

	@Definition(id = "WATER_COLOR_RESOLVER", field = "Lnet/minecraft/client/renderer/BiomeColors;WATER_COLOR_RESOLVER:Lnet/minecraft/world/level/ColorResolver;")
	@Expression("WATER_COLOR_RESOLVER = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ColorResolver WATER_COLOR_RESOLVER(ColorResolver original) {
		return new ColorResolver4.Impl((biome, _, _, _) -> biome.getWaterColor());
	}
}
