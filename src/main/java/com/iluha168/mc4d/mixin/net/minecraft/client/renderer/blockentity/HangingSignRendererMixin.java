package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HangingSignRenderer.class)
class HangingSignRendererMixin extends AbstractSignRendererMixin {
	// TODO baseTransformation wRot
	// TODO createGroundTransformation wRot
	// TODO createWallTransformation wRot
}
