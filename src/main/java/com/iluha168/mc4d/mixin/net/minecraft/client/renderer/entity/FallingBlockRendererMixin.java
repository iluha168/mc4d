package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.entity;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.util.Err4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FallingBlockRenderer.class)
abstract class FallingBlockRendererMixin extends EntityRendererMixin<FallingBlockEntity, FallingBlockRenderState> {
	@SuppressWarnings({"RedundantMethodOverride", "deprecation"})
	@Overwrite
	@Deprecated
	public boolean shouldRender(FallingBlockEntity entity, Frustum culler, double camX, double camY, double camZ) {
		throw Err4.arguments3("EntityRenderer4#shouldRender");
	}
	@Override
	public boolean shouldRender(FallingBlockEntity entity, Frustum culler, double camX, double camY, double camZ, double camW) {
		return super.shouldRender(entity, culler, camX, camY, camZ, camW) && entity.getBlockState() != entity.level().getBlockState(entity.blockPosition());
	}

	@Redirect(method = "extractRenderState(Lnet/minecraft/world/entity/item/FallingBlockEntity;Lnet/minecraft/client/renderer/entity/state/FallingBlockRenderState;F)V", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos;containing(DDD)Lnet/minecraft/core/BlockPos;"
	))
	BlockPos extractRenderState(double x, double y, double z, @Local(argsOnly = true, name = "entity") FallingBlockEntity entity) {
		return BlockPos4.containing(x, y, z, ((Entity4) entity).getW());
	}
}
