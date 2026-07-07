package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.client.Camera4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
class GameRendererMixin {
	// TODO everything else

	@ModifyExpressionValue(method = "renderItemInHand", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"
	))
	boolean renderItemInHand(boolean original) {
		// Do not render the hand when peeking.
		return original && !Camera4.mainCameraHasOffset();
	}

	// TODO everything else
}
