package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.debug;

import com.iluha168.mc4d.world.phys.Vec4;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHitboxDebugRenderer.class)
class EntityHitboxDebugRendererMixin {
	@Shadow
	@Final
	Minecraft minecraft;

	@Definition(id = "currentPosition", local = @Local(type = Vec3.class, name = "currentPosition"))
	@Expression("currentPosition = ?")
	@Inject(method = "showHitboxes", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER), cancellable = true)
	void showHitboxes(Entity entity, float partialTicks, boolean isServerEntity, CallbackInfo ci, @Local(name = "currentPosition") Vec3 currentPosition) {
		final double cameraW = ((Vec4) this.minecraft.gameRenderer.getMainCamera().position()).w;
		final double relativeW = ((Vec4) currentPosition).w - cameraW;
		if (Math.abs(relativeW) > entity.getBbWidth() * 0.5) {
			// Do not show the hitbox when the entity is outside camera's 3D slice.
			// Debug renderer does not affect this! We want to show hitboxes that players can actually hit!
			ci.cancel();
		}
	}
}
