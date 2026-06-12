package com.iluha168.mc4d.mixin.level4.patches;

import com.iluha168.mc4d.util.StaticCache3D;
import com.iluha168.mc4d.world.level.ChunkPos4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkStatusTasks.class)
class ChunkStatusTasksMixin {
	// TODO everything else

	@Redirect(method = "full", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/util/StaticCache2D;get(II)Ljava/lang/Object;"
	))
	private static <T> T full_get(StaticCache2D<T> chunks, int x, int z, @Local(name = "pos") ChunkPos pos) {
		return ((StaticCache3D<T>) chunks).get(x, z, ChunkPos4.as(pos).w());
	}

	// TODO everything else
}
