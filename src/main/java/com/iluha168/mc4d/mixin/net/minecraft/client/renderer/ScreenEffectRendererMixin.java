package com.iluha168.mc4d.mixin.net.minecraft.client.renderer;

import com.iluha168.mc4d.core.BlockPos4;
import com.iluha168.mc4d.world.entity.Entity4;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScreenEffectRenderer.class)
class ScreenEffectRendererMixin {
	@ModifyConstant(method = "getOverlayBlock", constant = @Constant(intValue = 1<<3))
	private static int getOverlayBlock_attempts(int attempts) {
		return attempts << 1;
	}
	@Redirect(method = "getOverlayBlock", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(DDD)Lnet/minecraft/core/BlockPos$MutableBlockPos;"
	))
	private static BlockPos.MutableBlockPos getOverlayBlock_set(
		BlockPos.MutableBlockPos testPos, double x, double y, double z,
		@Local(argsOnly = true, name = "player") Player player,
		@Local(name = "i") int i
	) {
		double wo = ((Entity4) player).getW() + ((i >> 3) % 2 - 0.5F) * player.getBbWidth() * 0.8F;
		return ((BlockPos4.MutableBlockPos) testPos).set(x, y, z, wo);
	}

	// TODO the rest
}
