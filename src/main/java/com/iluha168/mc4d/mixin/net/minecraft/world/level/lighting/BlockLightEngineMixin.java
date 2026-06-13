package com.iluha168.mc4d.mixin.net.minecraft.world.level.lighting;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockLightEngine.class)
class BlockLightEngineMixin {
	@Redirect(method = "propagateLightSources", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/LightChunkGetter;getChunkForLighting(II)Lnet/minecraft/world/level/chunk/LightChunk;"
	))
	LightChunk propagateLightSources(LightChunkGetter chunkSource, int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ((ChunkSource4) chunkSource).getChunkForLighting(x, z, ChunkPos4.as(pos).w());
	}
}
