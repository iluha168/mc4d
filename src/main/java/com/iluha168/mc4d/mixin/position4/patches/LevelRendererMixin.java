package com.iluha168.mc4d.mixin.position4.patches;

import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.expression.Definition;import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
class LevelRendererMixin {
	@Definition(id = "zOld", field = "Lnet/minecraft/world/entity/Entity;zOld:D")
	@Definition(id = "entity", local = @Local(type = Entity.class, name = "entity"))
	@Definition(id = "getZ", method = "Lnet/minecraft/world/entity/Entity;getZ()D")
	@Expression("entity.zOld = entity.getZ()")
	@Inject(method = "extractVisibleEntities", at = @At("MIXINEXTRAS:EXPRESSION"))
	void setWOld(
		Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState output, CallbackInfo ci,
		@Local(name = "entity") Entity entity
	) {
		Entity4 entity4 = (Entity4) entity;
		entity4.setWOld(entity4.getW());
	}
}
