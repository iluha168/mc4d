package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.chunk;

import com.iluha168.mc4d.MC4DClient;
import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkSectionsToRender.class)
class ChunkSectionsToRenderMixin {
	@Inject(method = "renderGroup", at = @At("HEAD"), cancellable = true)
	void renderGroup(ChunkSectionLayerGroup group, GpuSampler sampler, CallbackInfo ci) {
		if (Minecraft.getInstance().debugEntries.isCurrentlyEnabled(MC4DClient.NO_BLOCK_MODEL_RENDERER)) {
			ci.cancel();
		}
	}
}
