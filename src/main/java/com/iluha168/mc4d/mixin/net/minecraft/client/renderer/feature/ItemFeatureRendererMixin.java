package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.feature;

import com.iluha168.mc4d.client.renderer.SubmitNodeStorage4;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemFeatureRenderer.class)
class ItemFeatureRendererMixin {
	@Redirect(method = "renderItem", at = @At(
		value = "INVOKE",
		target = "Lcom/mojang/blaze3d/vertex/QuadInstance;setColor(I)V"
	))
	void renderItem(QuadInstance instance, int color, @Local(argsOnly = true, name = "submit") SubmitNodeStorage.ItemSubmit submit) {
		instance.setColor(SubmitNodeStorage4.ItemSubmit.as(submit).tintByEntity(color));
	}
}
