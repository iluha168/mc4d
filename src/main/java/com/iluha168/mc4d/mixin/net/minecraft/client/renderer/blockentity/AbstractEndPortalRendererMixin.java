package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.core.Direction4;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractEndPortalRenderer.class)
public class AbstractEndPortalRendererMixin {
	@Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true)
	private static void FACES(Direction direction, CallbackInfoReturnable<List<Vector3fc>> cir) {
		// TODO make the renderer.. 4D?
		if (direction.getAxis() == Direction4.Axis.W) {
			cir.setReturnValue(List.of());
		}
	}
}
