package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import com.iluha168.mc4d.world.level.biome.BiomeSource4;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeSource.class)
abstract class BiomeSourceMixin implements BiomeSource4, BiomeResolver4 {
	// TODO getBiomesWithin
	// TODO findBiomeHorizontal
	// TODO findClosestBiome3d
	// TODO findBiomeHorizontal
}
