package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.MC4DClient;
import com.iluha168.mc4d.core.Vec4i;
import com.iluha168.mc4d.world.level.block.entity.CampfireBlockEntity4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.CampfireRenderer;
import net.minecraft.client.renderer.blockentity.state.CampfireRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CampfireRenderer.class)
class CampfireRendererMixin {
	@Shadow
	@Final
	private static float SIZE;

	/** {@link com.iluha168.mc4d.mixin.net.minecraft.world.level.block.entity.CampfireBlockEntityMixin#particleTick_addParticle} */
	@SuppressWarnings("JavadocReference")
	@ModifyExpressionValue(method = "submit(Lnet/minecraft/client/renderer/blockentity/state/CampfireRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;isEmpty()Z"
	))
	boolean submit(boolean original, @Local(argsOnly = true, name = "state") CampfireRenderState state, @Local(name = "slot") int slot) {
		if (original) return true;
		final double w = Vec4i.getW(state.blockPos) + 0.5 + (slot < CampfireBlockEntity4.NUM_SLOTS/2 ? -0.3125F : 0.3125F);
		return Math.abs(MC4DClient.cameraW() - w) > SIZE / 2.0F;
	}
}
