package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FixedBiomeSource.class)
public class FixedBiomeSourceMixin implements BiomeResolver4 {
	@Shadow
	@Final
	private Holder<Biome> biome;

	@Overwrite
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
		throw Err4.arguments3("BiomeResolver4#getNoiseBiome");
	}
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, int quartW, Climate.Sampler sampler) {
		return this.biome;
	}
}
