package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.feature;

import net.minecraft.client.renderer.feature.BlockFeatureRenderer;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockFeatureRenderer.class)
class BlockFeatureRendererMixin {
	// TODO everything

	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
	))
	private static Direction[] DIRECTIONS3() {
		return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	}

	// TODO everything
}
