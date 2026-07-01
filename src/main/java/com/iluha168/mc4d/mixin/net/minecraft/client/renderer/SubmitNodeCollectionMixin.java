package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.client.renderer.SubmitNodeStorage4;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SubmitNodeCollection.class)
class SubmitNodeCollectionMixin {
	@Shadow @Final private SubmitNodeStorage submitNodeStorage;

	@ModifyVariable(method = "submitModel", at = @At("HEAD"), argsOnly = true, name = "tintedColor")
	int submitModel(int tintedColor) {
		return ((SubmitNodeStorage4) this.submitNodeStorage).tintByEntity(tintedColor);
	}

	@ModifyVariable(method = "submitModelPart", at = @At("HEAD"), argsOnly = true, name = "tintedColor")
	int submitModelPart(int tintedColor) {
		return ((SubmitNodeStorage4) this.submitNodeStorage).tintByEntity(tintedColor);
	}

	@ModifyExpressionValue(method = "submitItem", at = @At(
		value = "NEW",
		target = "net/minecraft/client/renderer/SubmitNodeStorage$ItemSubmit"
	))
	SubmitNodeStorage.ItemSubmit submitItem(SubmitNodeStorage.ItemSubmit node) {
		// Items are drawn deferred, so my hack in EntityRenderDispatcher4#submit does not work for items :(
		// Here we bake the tint onto the node, and it gets read by ItemFeatureRenderer.
		SubmitNodeStorage4.ItemSubmit.as(node).setEntityTintColor(((SubmitNodeStorage4) this.submitNodeStorage).entityTintColor());
		return node;
	}
}
