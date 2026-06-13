package com.iluha168.mc4d.mixin.net.minecraft.client.renderer.block;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO remove and make 4D rendering
@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
	@Redirect(method = "<clinit>", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/core/Direction;values()[Lnet/minecraft/core/Direction;"
	))
	private static Direction[] directions3() {
		return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	}
}
