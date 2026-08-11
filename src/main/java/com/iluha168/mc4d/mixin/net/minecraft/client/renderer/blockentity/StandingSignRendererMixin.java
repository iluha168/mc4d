package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StandingSignRenderer.class)
class StandingSignRendererMixin extends AbstractSignRendererMixin {
	// TODO baseTransformation wRot
	// TODO createGroundTransformation wRot
	// TODO createWallTransformation Direction#toWRot
}
