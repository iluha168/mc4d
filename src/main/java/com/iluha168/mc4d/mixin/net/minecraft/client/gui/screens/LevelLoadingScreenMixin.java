package com.iluha168.mc4d.mixin.net.minecraft.client.gui.screens;

import com.iluha168.mc4d.server.MinecraftServer4;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.server.level.progress.ChunkLoadStatusView;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelLoadingScreen.class)
class LevelLoadingScreenMixin {
	@Redirect(method = "extractChunksForRendering", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/server/level/progress/ChunkLoadStatusView;get(II)Lnet/minecraft/world/level/chunk/status/ChunkStatus;"
	))
	private static ChunkStatus extractChunksForRendering_getXZ(
		ChunkLoadStatusView statusView, int x, int z
	) {
		// TODO Show chunks in W != 0
		return ((MinecraftServer4.ChunkLoadStatusView) statusView).get(x, z, 0);
	}
}
