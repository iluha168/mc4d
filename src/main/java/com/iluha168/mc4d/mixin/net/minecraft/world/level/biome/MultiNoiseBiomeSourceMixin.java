package com.iluha168.mc4d.mixin.net.minecraft.world.level.biome;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.level.biome.BiomeResolver4;
import com.iluha168.mc4d.world.level.biome.Climate4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MultiNoiseBiomeSource.class)
abstract class MultiNoiseBiomeSourceMixin implements BiomeResolver4 {
	@Shadow
	public abstract Holder<Biome> getNoiseBiome(Climate.TargetPoint target);

	@Overwrite
	@Deprecated
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
		throw Err4.arguments3("BiomeResolver4#getNoiseBiome");
	}
	@Override
	public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, int quartW, Climate.Sampler sampler) {
		return this.getNoiseBiome(Climate4.Sampler.as(sampler).sample(quartX, quartY, quartZ, quartW));
	}

	@Redirect(method = "addDebugInfo", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/biome/Climate$Sampler;sample(III)Lnet/minecraft/world/level/biome/Climate$TargetPoint;"
	))
	Climate.TargetPoint addDebugInfo(Climate.Sampler instance, int quartX, int quartY, int quartZ, @Local(argsOnly = true, name = "feetPos") BlockPos feetPos) {
		final int quartW = QuartPos.fromBlock(Vec4i.getW(feetPos));
		return Climate4.Sampler.as(instance).sample(quartX, quartY, quartZ, quartW);
	}
}
