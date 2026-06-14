package com.iluha168.mc4d.mixin.net.neoforged.neoforge.common.extensions;

import com.iluha168.mc4d.world.level.ChunkPos4;
import com.iluha168.mc4d.world.level.chunk.ChunkSource4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LightChunk;
import net.neoforged.neoforge.common.extensions.IBlockGetterExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IBlockGetterExtension.class)
interface IBlockGetterExtensionMixin {
	@Redirect(method = "getAuxLightManager(Lnet/minecraft/world/level/ChunkPos;)Lnet/neoforged/neoforge/common/world/AuxiliaryLightManager;", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/chunk/ChunkSource;getChunkForLighting(II)Lnet/minecraft/world/level/chunk/LightChunk;"
	))
	default LightChunk getAuxLightManager(ChunkSource source, int x, int z, @Local(argsOnly = true, name = "pos") ChunkPos pos) {
		return ((ChunkSource4) source).getChunkForLighting(x, z, ChunkPos4.as(pos).w());
	}
}
