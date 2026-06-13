package com.iluha168.mc4d.mixin.net.minecraft.world.level.chunk.storage;

import com.iluha168.mc4d.core.SectionPos4;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SectionStorage.class)
class SectionStorageMixin {
	// TODO the rest

	@Redirect(method = "getKey", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/SectionPos;asLong(III)J"
	))
	private static long getKey(int x, int y, int z, @Local(argsOnly = true, name = "chunkPos") ChunkPos chunkPos) {
		return SectionPos4.asLong(x, y, z, ChunkPos4.as(chunkPos).w());
	}

	// TODO the rest
}
