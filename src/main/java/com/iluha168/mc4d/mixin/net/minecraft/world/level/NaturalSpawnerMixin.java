package com.iluha168.mc4d.mixin.net.minecraft.world.level;

import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.biome.BiomeManager4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NaturalSpawner.class)
class NaturalSpawnerMixin {
	@Redirect(method = "getRoughBiome", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/ChunkAccess;getNoiseBiome(III)Lnet/minecraft/core/Holder;"
	))
	private static Holder<Biome> getRoughBiome(ChunkAccess instance, int quartX, int quartY, int quartZ, @Local(argsOnly = true, name = "pos") BlockPos pos) {
		return ((BiomeManager4.NoiseBiomeSource) instance).getNoiseBiome(quartX, quartY, quartZ, QuartPos.fromBlock(Vec4i.getW(pos)));
	}

	// TODO everything
}
