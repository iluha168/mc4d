package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.blockentity;

import com.iluha168.mc4d.api.net.minecraft.core.Vec4i;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BannerRenderer.class)
class BannerRendererMixin {
	@Expression("? * 7 + ? * 9 + ? * 13")
	@ModifyExpressionValue(method = "extractRenderState(Lnet/minecraft/world/level/block/entity/BannerBlockEntity;Lnet/minecraft/client/renderer/blockentity/state/BannerRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At("MIXINEXTRAS:EXPRESSION"))
	int extractRenderState(int hash, @Local(name = "blockPos") BlockPos blockPos) {
		return hash + Vec4i.getW(blockPos) * 17;
	}

	// TODO createGroundTransformation wRot
	// TODO createWallTransformation wRot
}
